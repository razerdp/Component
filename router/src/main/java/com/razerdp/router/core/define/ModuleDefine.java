package com.razerdp.router.core.define;


import com.razerdp.annotations.AnnotationConfig;

/**
 * Created by 大灯泡 on 2019/7/18
 * <p>
 * Description：存放常量
 */
public class ModuleDefine {
    public static final String SP_NAME = "ModuleServiceManager";
    public static final String SEPARATOR = "$$";
    public static final String ROOT_PAKCAGE = AnnotationConfig.ModuleServiceConfig.GEN_PACKAGE;
    public static final String GEN_NAME = AnnotationConfig.ModuleServiceConfig.GENERATE_FILE_NAME;

    public static final String KEY_VERSION = "version";
    public static final String KEY_VERSION_NAME = "version_name";

    public static final String KEY_SERVICE_IMPL_CACHE = "servicesImplTableCache";

}
