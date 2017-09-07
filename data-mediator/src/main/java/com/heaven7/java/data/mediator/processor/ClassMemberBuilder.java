package com.heaven7.java.data.mediator.processor;

import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;

import java.util.List;

import static com.heaven7.java.data.mediator.processor.Util.getFieldModifier;
import static com.heaven7.java.data.mediator.processor.Util.hasFlag;
/**
 * Created by heaven7 on 2017/8/30.
 */
public class ClassMemberBuilder extends BaseMemberBuilder {

    private static final String GOOGLE_GSON_ANNO_PACKAGE ="com.google.gson.annotations";

    @Override
    public void build(TypeSpec.Builder builder, List<FieldData> mFields) {
        super.build(builder, mFields);
        //empty constructor
        builder.addMethod(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .build());
    }

    @Override
    protected boolean isInterface() {
        return false;
    }

    @Override
    protected MethodSpec.Builder onBuildGet(FieldData field, String nameForMethod, TypeInfo info) {
        MethodSpec.Builder get = MethodSpec.methodBuilder(GET_PREFIX + nameForMethod)
                .returns(info.typeName)
                .addModifiers(Modifier.PUBLIC)
               .addCode("return $N;\n", field.getPropertyName());
        return get;
    }
    @Override
    protected MethodSpec.Builder onBuildSet(FieldData field, String nameForMethod, TypeInfo info) {
        MethodSpec.Builder set = MethodSpec.methodBuilder(SET_PREFIX + nameForMethod)
                .addParameter(info.typeName, info.paramName)
                .returns(TypeName.VOID)
                .addModifiers(Modifier.PUBLIC)
                .addCode("this.$N = $N;\n", field.getPropertyName(), info.paramName)
                ;
        return set;
    }
    @Override
    protected FieldSpec.Builder onBuildField(FieldData field, TypeInfo info) {
        final FieldSpec.Builder builder   = FieldSpec.builder(info.typeName,
                field.getPropertyName(), getFieldModifier(field));
        //check serializeName , that support serialize and deserialize for GSON
        final String serializeName = field.getSerializeName();
        if (serializeName != null && !serializeName.equals("")) {
            builder.addAnnotation(AnnotationSpec.builder(
                    ClassName.get(GOOGLE_GSON_ANNO_PACKAGE, "SerializedName"))
                    .addMember("value","$S", serializeName)
                    .build());
        }
        if(hasFlag(field.getFlags(), FieldData.FLAG_EXPOSE_DEFAULT)){
            //if have FLAG_EXPOSE_SERIALIZE_FALSE, serialize = false. same as FLAG_EXPOSE_DESERIALIZE_FALSE.
            boolean falseSerialize =  hasFlag(field.getFlags(), FieldData.FLAG_EXPOSE_SERIALIZE_FALSE);
            boolean falseDeserialize = hasFlag(field.getFlags(), FieldData.FLAG_EXPOSE_DESERIALIZE_FALSE);
            builder.addAnnotation(AnnotationSpec.builder(ClassName.get(GOOGLE_GSON_ANNO_PACKAGE, "Expose"))
                    .addMember("serialize", "$L", !falseSerialize)
                    .addMember("deserialize", "$L", !falseDeserialize)
                    .build()
            );
        }
        return builder;
    }
}
