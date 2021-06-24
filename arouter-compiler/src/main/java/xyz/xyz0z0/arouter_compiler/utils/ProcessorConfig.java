package xyz.xyz0z0.arouter_compiler.utils;

/**
 * Author: Cheng
 * Date: 2021/6/24 9:50
 * Description: xyz.xyz0z0.arouter_compiler.utils
 */
public interface ProcessorConfig {

    String PARAMETER_PACKAGE = "xyz.xyz0z0.arouter_annotations.Parameter";

    String ACTIVITY_PACKAGE = "android.app.Activity";

    String AROUTER_API_PACKAGE = "xyz.xyz0z0.arouter_api";

    String AROUTER_AIP_PARAMETER_GET = AROUTER_API_PACKAGE + ".ParameterGet";
    String PARAMETER_NAME = "targetParameter";

    String PARAMETER_METHOD_NAME = "getParameter";

    String STRING = "java.lang.String";
    String PARAMETER_FILE_NAME = "$$Parameter";
}


