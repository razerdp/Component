package com.razerdp.annotations;

/**
 * Created by razerdp on 2019/7/18
 * <p>
 * Description router code gen
 */
public class AnnotationConfig {

    public static class ModuleServiceConfig {
        public static final String LOG_PRE_FIX = "ModuleServicesCompiler:: ";
        public static final String TARGET_PACKAGE = "com.razerdp.router.core";
        public static final String GEN_PACKAGE = TARGET_PACKAGE + ".gen";
        public static final String GENERATE_FILE_NAME = "ServicesImplGen";
        public static final String FIELD_MAP = "SERVICES_IMPL_MAP";
        public static final String SERVICES_BASE_PACKAGE = "com.razerdp.router";
        public static final String INTERFACE_PROCESSOR = ".IModuleServiceProvider";
    }

}
