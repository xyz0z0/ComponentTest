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
    String OPTIONS = "moduleName";
    String APT_PACKAGE = "packageNameForAPT";

    String AROUTER_API_GROUP = AROUTER_API_PACKAGE + ".ARouterGroup";
    String AROUTER_API_PATH = AROUTER_API_PACKAGE + ".ARouterPath";

    String PATH_METHOD_NAME = "getPathMap";
    String GROUP_METHOD_NAME = "getGroupMap";
    Object PATH_VAR1 = "pathMap";
    String PATH_FILE_NAME = "ARouter$$Path$$";
    String GROUP_FILE_NAME = "ARouter$$Group$$";
    String GROUP_VAR1 = "groupMap";
}


