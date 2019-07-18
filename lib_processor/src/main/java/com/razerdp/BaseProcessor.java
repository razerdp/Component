package com.razerdp;


import com.razerdp.utils.Logger;

import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Created by razerdp on 2019/5/30
 * <p>
 * Description base processor
 */
public abstract class BaseProcessor extends AbstractProcessor {
    protected Filer mFiler;
    protected Logger logger;
    protected Types types;
    protected Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        mFiler = processingEnv.getFiler();
        types = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        logger = new Logger(processingEnv.getMessager());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
        onAppendAnnotation(annotations);
        for (Class<? extends Annotation> annotation : annotations) {
            types.add(annotation.getCanonicalName());
        }
        return types;
    }

    protected abstract void onAppendAnnotation(Set<Class<? extends Annotation>> set);

    public void logi(CharSequence info) {
        logger.i(info);
    }

    public void loge(CharSequence error) {
        logger.e(error);
    }

    public void loge(Throwable error) {
        logger.e(error);
    }

    public void logw(CharSequence warning) {
        logger.w(warning);
    }


    public TypeElement getTypeElement(String str) {
        return elementUtils.getTypeElement(str);
    }

}
