package xyz.xyz0z0.arouter_compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.Modifier;

/**
 * Author: Cheng
 * Date: 2021/6/22 14:55
 * Description: xyz.xyz0z0.arouter_compiler
 */
public class JavaPoetTest {

    public static void main(String[] args) {
        // testHelloWorld();
        // testARouter();
        testOther();
    }


    private static void testOther() {
        // List<String>
        ParameterizedTypeName list1 = ParameterizedTypeName.get(ClassName.get(List.class),
            ClassName.get(String.class));
        // List<? extends String>
        WildcardTypeName typeName = WildcardTypeName.subtypeOf(ClassName.get(String.class));
        ParameterizedTypeName list2 = ParameterizedTypeName.get(ClassName.get(List.class), typeName);
        // Map<String,String>
        ParameterizedTypeName map1 = ParameterizedTypeName.get(ClassName.get(Map.class), ClassName.get(String.class),
            ClassName.get(String.class));
        MethodSpec methodSpec = MethodSpec.methodBuilder("test")
            .addStatement("$T", list1)
            .addStatement("$T", list2)
            .addStatement("$T", map1)
            .build();
        System.out.println(methodSpec.toString());
    }


    /**
     * package com.example.helloworld;
     * <p>
     * public final class HelloWorld {
     * public static void main(String[] args) {
     * System.out.println("Hello, JavaPoet!");
     * }
     * }
     */
    private static void testHelloWorld() {
        MethodSpec main = MethodSpec.methodBuilder("main")
            .returns(void.class)
            .addParameter(String[].class, "args")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
            .build();
        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
            .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
            .addMethod(main)
            .build();
        JavaFile javaFile = JavaFile.builder("com.example.helloworld", helloWorld)
            .build();
        System.out.println(javaFile.toString());
    }


    private static void testARouter() {
        ClassName IRouteGroup =
            ClassName.get("com.alibaba.android.arouter.facade.template", "IRouteGroup");
        ClassName IRouteRoot =
            ClassName.get("com.alibaba.android.arouter.facade.template", "IRouteRoot");
        ClassName mapClazz = ClassName.get("java.util", "Map");
        TypeName map = ParameterizedTypeName.get(mapClazz,
            ClassName.get(String.class),

            ParameterizedTypeName.get(ClassName.get(Class.class), WildcardTypeName.subtypeOf(IRouteGroup))
        );
        MethodSpec loadInto = MethodSpec.methodBuilder("loadInto")
            .returns(void.class)
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override.class)
            .addParameter(map, "routes")
            .addStatement("$N.put($S,$T.class)", "routes", "farmer", ClassName.get(ARouter$$Group$$test.class))
            .build();

        TypeSpec main = TypeSpec.classBuilder("ARouter$$Root$$demo")
            .addModifiers(Modifier.PUBLIC)
            .addSuperinterface(IRouteRoot)
            .addMethod(loadInto)
            .build();
        JavaFile javaFile = JavaFile.builder("com.alibaba.android.arouter.routes", main)
            .build();
        System.out.println(javaFile.toString());

    }

}
