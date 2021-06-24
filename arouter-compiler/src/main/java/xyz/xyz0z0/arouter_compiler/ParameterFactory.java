package xyz.xyz0z0.arouter_compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import xyz.xyz0z0.arouter_annotations.Parameter;
import xyz.xyz0z0.arouter_compiler.utils.ProcessorConfig;
import xyz.xyz0z0.arouter_compiler.utils.ProcessorUtils;

/**
 * Author: Cheng
 * Date: 2021/6/24 10:10
 * Description: xyz.xyz0z0.arouter_compiler
 *
 * @author Cheng
 * @Override public void getParameter(Object targetParameter) {
 * Personal_MainActivity t = (Personal_MainActivity) targetParameter;
 * t.name = t.getIntent().getStringExtra("name");
 * t.sex = t.getIntent().getStringExtra("sex");
 * }
 */
public class ParameterFactory {

    private final MethodSpec.Builder method;

    private final ClassName className;

    private final Messager messager;


    private ParameterFactory(Builder builder) {
        this.messager = builder.messager;
        this.className = builder.className;

        method = MethodSpec.methodBuilder(ProcessorConfig.PARAMETER_METHOD_NAME)
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(builder.parameterSpec);
    }


    public void addFirstStatement() {
        method.addStatement("$T t = ($T) " + ProcessorConfig.PARAMETER_NAME, className, className);
    }


    public MethodSpec build() {
        return method.build();
    }


    public void buildStatement(Element element) {
        TypeMirror typeMirror = element.asType();

        int type = typeMirror.getKind().ordinal();
        String fieldName = element.getSimpleName().toString();
        System.out.println("fieldName   "+fieldName);
        String annotationValue = element.getAnnotation(Parameter.class).name();
        annotationValue = ProcessorUtils.isEmpty(annotationValue) ? fieldName : annotationValue;
        String finalValue = "t." + fieldName;
        String methodContent = finalValue + " = t.getIntent().";

        if (type == TypeKind.INT.ordinal()) {
            methodContent += "getIntExtra($S, " + finalValue + ")";
        } else if (type == TypeKind.BOOLEAN.ordinal()) {
            methodContent += "getBooleanExtra($S, " + finalValue + ")";
        } else {
            if (typeMirror.toString().equalsIgnoreCase(ProcessorConfig.STRING)) {
                methodContent += "getStringExtra($S)";
            }
        }

        if (methodContent.endsWith(")")) {
            method.addStatement(methodContent, annotationValue);
        } else {
            messager.printMessage(Diagnostic.Kind.ERROR, "不支持的类型");
        }
    }


    public static class Builder {

        private final ParameterSpec parameterSpec;
        private Messager messager;
        private ClassName className;


        public Builder(ParameterSpec parameterSpec) {
            this.parameterSpec = parameterSpec;
        }


        public Builder setMessager(Messager messager) {
            this.messager = messager;
            return this;
        }


        public Builder setClassName(ClassName className) {
            this.className = className;
            return this;
        }


        public ParameterFactory build() {
            if (parameterSpec == null) {
                throw new IllegalArgumentException("parameterSpec 方法参数体为空");
            }
            if (className == null) {
                throw new IllegalArgumentException("方法内容中的 className 为空");
            }
            if (messager == null) {
                throw new IllegalArgumentException("messager 为空");
            }
            return new ParameterFactory(this);
        }
    }

}
