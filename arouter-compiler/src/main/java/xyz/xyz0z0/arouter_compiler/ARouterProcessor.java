package xyz.xyz0z0.arouter_compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.io.IOException;
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
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import xyz.xyz0z0.arouter_annotations.ARouter;
import xyz.xyz0z0.arouter_annotations.bean.RouterBean;
import xyz.xyz0z0.arouter_compiler.utils.ProcessorConfig;
import xyz.xyz0z0.arouter_compiler.utils.ProcessorUtils;


@AutoService(Processor.class)
@SupportedAnnotationTypes({"xyz.xyz0z0.arouter_annotations.ARouter"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ARouterProcessor extends AbstractProcessor {

    private final Map<String, List<RouterBean>> mAllPathMap = new HashMap<>();
    private final Map<String, String> mAllGroupMap = new HashMap<>();
    private Elements elementTool;
    private Types typeTool;
    // 日志相关
    private Messager messager;
    // 生成文件相关操作
    private Filer filer;
    private String options;
    private String aptPackage;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        elementTool = processingEnv.getElementUtils();
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
        typeTool = processingEnv.getTypeUtils();

        options = processingEnv.getOptions().get(ProcessorConfig.OPTIONS);
        aptPackage = processingEnv.getOptions().get(ProcessorConfig.APT_PACKAGE);

        messager.printMessage(Diagnostic.Kind.NOTE, ">>>>>>>> options " + options);
        messager.printMessage(Diagnostic.Kind.NOTE, ">>>>>>>> aptPackage " + aptPackage);
        if (options != null && aptPackage != null) {
            messager.printMessage(Diagnostic.Kind.NOTE, "APT 环境搭建完成。");
        } else {
            messager.printMessage(Diagnostic.Kind.NOTE, "APT 环境有问题，请检查");
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
        if (set.isEmpty()) {
            messager.printMessage(Diagnostic.Kind.NOTE, "并没有发现 ARouter 注解");
            return false;
        }
        // 获取所有被 ARouter 注解的元素合计 TODO 和上面的有什么区别
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(ARouter.class);

        TypeElement activityType = elementTool.getTypeElement(ProcessorConfig.ACTIVITY_PACKAGE);
        TypeMirror activityMirror = activityType.asType();

        for (Element element : elements) {
            String className = element.getSimpleName().toString();
            messager.printMessage(Diagnostic.Kind.NOTE, "被ARouter注解的类有 " + className);

            ARouter aRouter = element.getAnnotation(ARouter.class);
            // 检查工作
            RouterBean routerBean = new RouterBean.Builder()
                    .addGroup(aRouter.group())
                    .addPath(aRouter.path())
                    .addElement(element)
                    .build();
            TypeMirror elementMirror = element.asType();
            if (typeTool.isSubtype(elementMirror, activityMirror)) {
                routerBean.setTypeEnum(RouterBean.TypeEnum.ACTIVITY);
            } else {
                throw new RuntimeException("@ARouter 注解目前仅限用于 Activity 类之上");
            }

            if (checkRouterPath(routerBean)) {
                messager.printMessage(Diagnostic.Kind.NOTE, "RouterBean check success " + routerBean.toString());

                List<RouterBean> routerBeans = mAllPathMap.get(routerBean.getGroup());
                if (ProcessorUtils.isEmpty(routerBeans)) {
                    routerBeans = new ArrayList<>();
                    routerBeans.add(routerBean);
                    mAllPathMap.put(routerBean.getGroup(), routerBeans);
                } else {
                    routerBeans.add(routerBean);
                }
            } else {
                messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter 未按规范配置");
            }
        }

        TypeElement pathType = elementTool.getTypeElement(ProcessorConfig.AROUTER_API_PATH);
        TypeElement groupType = elementTool.getTypeElement(ProcessorConfig.AROUTER_API_GROUP);

        try {
            createPathFile(pathType);
        } catch (Exception e) {
            e.printStackTrace();
            messager.printMessage(Diagnostic.Kind.NOTE, "在生成PATH模板时，异常了 e:" + e.getMessage());
        }

        try {
            createGroupFile(groupType, pathType);
        } catch (Exception e) {
            e.printStackTrace();
            messager.printMessage(Diagnostic.Kind.NOTE, "在生成GROUP模板时，异常了 e:" + e.getMessage());
        }
        return true;
//        messager.printMessage(Diagnostic.Kind.NOTE, ">>>>> start");
//        if (set.isEmpty()) {
//            return false;
//        }
//        for (TypeElement annotation : set) {
//            messager.printMessage(Diagnostic.Kind.NOTE, "annotation " + annotation.toString());
//            generateHelloWorld();
//        }
//        messager.printMessage(Diagnostic.Kind.NOTE, ">>>>> end");
//        return false;
    }

    private void createGroupFile(TypeElement groupType, TypeElement pathType) throws IOException {
        if (ProcessorUtils.isEmpty(mAllGroupMap) || ProcessorUtils.isEmpty(mAllPathMap)) {
            return;
        }
        TypeName methodReturns = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(pathType))));
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(ProcessorConfig.GROUP_METHOD_NAME)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(methodReturns);
        methodBuilder.addStatement("$T<$T,$T> $N = new $T<>()",
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(pathType))),
                ProcessorConfig.GROUP_VAR1,
                ClassName.get(HashMap.class));
        for (Map.Entry<String, String> entry : mAllGroupMap.entrySet()) {
            methodBuilder.addStatement("$N.put($S,$T.class)",
                    ProcessorConfig.GROUP_VAR1,
                    entry.getKey(),
                    ClassName.get(aptPackage, entry.getValue()));
        }
        methodBuilder.addStatement("return $N", ProcessorConfig.GROUP_VAR1);
        String finalClassName = ProcessorConfig.GROUP_FILE_NAME + options;
        messager.printMessage(Diagnostic.Kind.NOTE, "APT 生成路由组 group 类文件：" + aptPackage + "." + finalClassName);
        JavaFile.builder(aptPackage,
                TypeSpec.classBuilder(finalClassName)
                        .addSuperinterface(ClassName.get(groupType))
                        .addModifiers(Modifier.PUBLIC)
                        .addMethod(methodBuilder.build())
                        .build()
        ).build().writeTo(filer);


    }

    private void createPathFile(TypeElement pathType) throws IOException {
        if (ProcessorUtils.isEmpty(mAllPathMap)) {
            return;
        }
        TypeName methodReturn = ParameterizedTypeName.get(ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(RouterBean.class));
        for (Map.Entry<String, List<RouterBean>> entry : mAllPathMap.entrySet()) {
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(ProcessorConfig.PATH_METHOD_NAME)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(methodReturn);

            methodBuilder.addStatement("$T<$T,$T> $N = new $T<>()",
                    ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ClassName.get(RouterBean.class),
                    ProcessorConfig.PATH_VAR1,
                    ClassName.get(HashMap.class));

            List<RouterBean> pathList = entry.getValue();
            for (RouterBean bean : pathList) {
                methodBuilder.addStatement("$N.put($S,$T.create($T.$L,$T.class,$S,$S))",
                        ProcessorConfig.PATH_VAR1,
                        bean.getPath(),
                        ClassName.get(RouterBean.class),
                        ClassName.get(RouterBean.TypeEnum.class),
                        bean.getTypeEnum(),
                        ClassName.get((TypeElement) bean.getElement()),
                        bean.getPath(),
                        bean.getGroup());
            }
            methodBuilder.addStatement("return $N", ProcessorConfig.PATH_VAR1);

            String finalClassName = ProcessorConfig.PATH_FILE_NAME + entry.getKey();
            messager.printMessage(Diagnostic.Kind.NOTE, "APT 生成路由 Path 类文件：" +
                    aptPackage + "." + finalClassName);
            JavaFile.builder(aptPackage,
                    TypeSpec.classBuilder(finalClassName)
                            .addSuperinterface(ClassName.get(pathType))
                            .addModifiers(Modifier.PUBLIC)
                            .addMethod(methodBuilder.build())
                            .build())
                    .build()
                    .writeTo(filer);
            mAllGroupMap.put(entry.getKey(), finalClassName);
        }


    }


    private boolean checkRouterPath(RouterBean bean) {
        String group = bean.getGroup();
        String path = bean.getPath();

        if (ProcessorUtils.isEmpty(path) || !path.startsWith("/")) {
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter 注解中的 path 值，必须以 / 开头");
            return false;
        }
        if (path.lastIndexOf("/") == 0) {
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter 注解未按照规范配置 /app/MainActivity");
            return false;
        }
        String finalGroup = path.substring(1, path.indexOf("/", 1));
        if (!ProcessorUtils.isEmpty(group) && !group.equals(options)) {
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter 注解中的 group 的值必须与子模块值一致");
            return false;
        } else {
            bean.setGroup(finalGroup);
        }
        return true;
    }


    /**
     *
     */
    private void generateHelloWorld() {
        // 方法
        MethodSpec mainMethod = MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(String[].class, "args")
                .addStatement("$T.out.println($S)", System.class, "Hello world.")
                .build();
        // 类
        TypeSpec typeSpec = TypeSpec.classBuilder("MainTest")
                .addMethod(mainMethod)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .build();
        // 包
        JavaFile javaFile = JavaFile.builder("com.hello.test", typeSpec)
                .build();
        // 生成文件
        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
            messager.printMessage(Diagnostic.Kind.NOTE, "生成文件失败，异常：" + e.getMessage());
        }
    }
}
