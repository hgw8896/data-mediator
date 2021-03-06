data-mediator
=======================================
 see [English document](https://github.com/LightSun/data-mediator/wiki/Main-of-Dara-mediator) by click this.
- 一个数据层的框架。利用编译时注解技术， 在java和android平台自动生成 数据实体及相关的代码。
  方便数据层的使用。支持gson.

# 设计思想
- 整个设计分3层： 模型层，代理层，调用层

- 模型层：代表的是数据模型接口and实现
- 代理层：数据实体的代理
- 调用层：操作模型和代理的

# 特点
- 自动生成数据的接口和实现类.可自动实现Serializable和 Parcelable(android)接口。
- 自动生成代理层 以便监听数据变化。
- 为数据实体 自动生成get 和 set方法 
- 字段: 支持生成字段的注解 for 'Google-Gson'.
- 字段: 支持多种类型 , 8大基本类型(int,long,short,byte,float,double,boolean ,char)及其包装类型， String类型, 和其他类型 .
  数组和list结构同样支持。（map暂不支持parcelable）
- 字段: 支持多域， 比如： 重置(IResetable接口), 拷贝（ICopyable接口), 共享（Shareable), 快照（ISnapable)接口。toString.
      作用: 比如重置： 很多时候我们调用了数据的一些方法，改变了一些属性。然后想重置以便重新使用。
      
- 支持依赖或继承 @Field注解的接口（代表数据实体). 继承只能继承一个。
   * 平常我们写 BaseEntity(内有代表http/https响应的code, message, data字段）, 通常业务接口的数据会继承这个BaseEntity。
    所以这里规定 继承@Field注解的接口（代表数据实体) 只能一个。否则error.
    
- 支持toString和 FLAG_TO_STRING 标志.
- 支持android平台的双向绑定

# 即将支持的特性
- 丰富的调用层支持和数据缓存
- 更多接口的支持

# 快速入门

1, 在项目根目录添加apt依赖。
```java
 classpath 'com.neenbedankt.gradle.plugins:android-apt:1.8'
```

2, 在使用的app module中加入。apt plugin
```java
   apply plugin: 'com.neenbedankt.android-apt'
```

3, 添加dependencies
```java
dependencies {
    //......
    compile 'com.heaven7.java.data.mediator:data-mediator:<see release>'
    compile 'com.heaven7.java.data.mediator.annotation:data-mediator-annotations:<see release>'
    apt 'com.heaven7.java.data.mediator.compiler:data-mediator-compiler:<see release>'
    apt 'com.squareup:javapoet:1.9.0'
    
    // 如果需要生成对应的gson注解。请加入gson依赖。比如
    compile "com.google.code.gson:gson:2.7"
}
```

4, 开始定义你的数据实体。比如我要定义关于学生的数据模型, 需要实现Serializable, Parcelable. 
假如学生有。年龄，名称, id属性。
那么简单的数据定义为:
```java

@Fields({
        @Field(propName = "age" , type = int.class, flags = FLAGS_ALL_SCOPES),
        @Field(propName = "name" , type = String.class, flags = FLAGS_ALL_SCOPES),
        @Field(propName = "id" , type = long.class, flags = FLAGS_ALL_SCOPES),
})
public interface Student extends Serializable, Parcelable{
}
```

5, 点击android studio 工具栏上的图标

   ![make project](res/as_make_project.png)

  即可自动生成代码（数据定义没变化，不会重新生成）。


6, 调用示例 （来自data-mediator-demo下的[TestDoubleBindActivity](https://github.com/LightSun/data-mediator/blob/master/Data-mediator-demo/app/src/main/java/com/heaven7/data/mediator/demo/activity/TestDoubleBindActivity.java)）
```java
/**
 * 双向绑定示例程序.
 */
public class TestDoubleBindActivity extends AppCompatActivity {

    @BindView(R.id.tv_desc)
    TextView mTv_desc;

    DataMediator<StudentModule> mMediator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //为数据模型创建  中介者。
        mMediator = DataMediatorFactory.createDataMediator(StudentModule.class);
        //双向绑定
        DoubleBindUtil.bindDouble(mMediator, mTv_desc, "name");

        mMediator.getDataProxy().setName("heaven7");
    }

    //从TextView 设置文本, 同事改变数据的属性.
    @OnClick(R.id.bt_set_text_on_TextView)
    public void onClickSetTextOnTextView(View v){
        mTv_desc.setText("set by set_text_on_TextView");
    }

    //从数据代理去设置 数据属性，同时更改绑定的TextView属性
    @OnClick(R.id.bt_set_text_on_mediator)
    public void onClickSetTextOnMediator(View v){
        mMediator.getDataProxy().setName("set_text_on_mediator");
    }

}
```


# License

    Copyright 2017   
                group of data-mediator
        member: heaven7(donshine723@gmail.com)

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


