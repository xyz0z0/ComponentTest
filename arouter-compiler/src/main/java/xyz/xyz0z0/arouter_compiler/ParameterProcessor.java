package xyz.xyz0z0.arouter_compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import xyz.xyz0z0.arouter_annotations.Parameter;
import xyz.xyz0z0.arouter_compiler.utils.ProcessorConfig;
import xyz.xyz0z0.arouter_compiler.utils.ProcessorUtils;

/**
 * Author: Cheng
 * Date: 2021/6/24 9:49
 * Description: xyz.xyz0z0.arouter_compiler
 *
 * @author Cheng
 */

@AutoService(Processor.class)
@SupportedAnnotationTypes({ ProcessorConfig.PARAMETER_PACKAGE })
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ParameterProcessor extends AbstractProcessor {

    private final Map<TypeElement, List<Element>> tempParameterMap = new HashMap<>();
    private Elements elementUtils;
    private Types typeUtils;
    private Messager messager;
    private Filer filer;


    @Override public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
        messager.printMessage(Diagnostic.Kind.NOTE, ">>>>>>111 ");
    }


    @Override public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("====================");
        messager.printMessage(Diagnostic.Kind.NOTE, ">>>>>>222 ");
        if (!ProcessorUtils.isEmpty(annotations)) {
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Parameter.class);
            if (!ProcessorUtils.isEmpty(elements)) {
                for (Element element : elements) {
                    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
                    System.out.println("----"+element.toString());
                    System.out.println("----"+enclosingElement.toString());
                    if (tempParameterMap.containsKey(enclosingElement)) {
                        tempParameterMap.get(enclosingElement).add(element);
                    } else {
                        List<Element> fields = new ArrayList<>();
                        fields.add(element);
                        tempParameterMap.put(enclosingElement, fields);
                    }
                }
                if (ProcessorUtils.isEmpty(tempParameterMap)) {
                    return true;
                }
                TypeElement activityType = elementUtils.getTypeElement(ProcessorConfig.ACTIVITY_PACKAGE);
                TypeElement parameterType = elementUtils.getTypeElement(ProcessorConfig.AROUTER_AIP_PARAMETER_GET);

                ParameterSpec parameterSpec = ParameterSpec.builder(TypeName.OBJECT, ProcessorConfig.PARAMETER_NAME).build();

                for (Map.Entry<TypeElement, List<Element>> entry : tempParameterMap.entrySet()) {
                    TypeElement typeElement = entry.getKey();
                    if (!typeUtils.isSubtype(typeElement.asType(), activityType.asType())) {
                        throw new RuntimeException("注解只能用在 Activity 上");
                    }
                    ClassName className = ClassName.get(typeElement);
                    ParameterFactory factory = new ParameterFactory.Builder(parameterSpec)
                        .setMessager(messager)
                        .setClassName(className)
                        .build();
                    factory.addFirstStatement();
                    for (Element element : entry.getValue()) {
                        factory.buildStatement(element);
                    }
                    String finalClassName = typeElement.getSimpleName() + ProcessorConfig.PARAMETER_FILE_NAME;
                    messager.printMessage(Diagnostic.Kind.NOTE, "APT 生成获取参数类文件");
                    TypeSpec typeSpec = TypeSpec.classBuilder(finalClassName)
                        .addSuperinterface(ClassName.get(parameterType))
                        .addModifiers(Modifier.PUBLIC)
                        .addMethod(factory.build())
                        .build();
                    try {
                        JavaFile.builder(className.packageName(), typeSpec)
                            .build()
                            .writeTo(filer);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }
        }

        return true;
    }
}
