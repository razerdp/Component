package com.razerdp.processor.modules;

import com.google.auto.service.AutoService;
import com.razerdp.BaseProcessor;
import com.razerdp.annotations.modules.ServiceImpl;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import static com.razerdp.annotations.AnnotationConfig.ModuleServiceConfig.FIELD_MAP;
import static com.razerdp.annotations.AnnotationConfig.ModuleServiceConfig.GENERATE_FILE_NAME;
import static com.razerdp.annotations.AnnotationConfig.ModuleServiceConfig.GEN_PACKAGE;
import static com.razerdp.annotations.AnnotationConfig.ModuleServiceConfig.INTERFACE_PROCESSOR;
import static com.razerdp.annotations.AnnotationConfig.ModuleServiceConfig.LOG_PRE_FIX;
import static com.razerdp.annotations.AnnotationConfig.ModuleServiceConfig.SERVICES_BASE_PACKAGE;
import static com.razerdp.annotations.AnnotationConfig.ModuleServiceConfig.TARGET_PACKAGE;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

/**
 * Created by razerdp on 2019/7/18
 * <p>
 * Description:compiler for {@link com.razerdp.annotations.modules.ServiceImpl}
 */
@AutoService(Processor.class)
public class ModuleServicesProcessor extends BaseProcessor {

    private static final String KEY_MODULE_NAME = "MODULE_NAME";
    private String moduleName = null;
    private static final HashMap<TypeName, List<InnerInfo>> mServiceImplMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        Map<String, String> options = processingEnv.getOptions();
        if (options != null && !options.isEmpty()) {
            moduleName = options.get(KEY_MODULE_NAME);
        }
    }

    private static class InnerInfo {
        final TypeElement mirror;
        final String fullClassName;
        final int tag;

        InnerInfo(TypeElement mirror, String fullClassName, int tag) {
            this.mirror = mirror;
            this.fullClassName = fullClassName;
            this.tag = tag;
        }
    }

    @Override
    protected void onAppendAnnotation(Set<Class<? extends Annotation>> set) {
        set.add(ServiceImpl.class);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (roundEnvironment.processingOver()) {
            if (!set.isEmpty()) {
                loge(LOG_PRE_FIX + "Unexpected processing state: annotations still available after processing over");
                return false;
            }
        }
        if (set.isEmpty()) {
            return false;
        }
        logi(LOG_PRE_FIX + "Processing");
        mServiceImplMap.clear();
        try {
            collectionImpls(set, roundEnvironment);
            if (!mServiceImplMap.isEmpty()) {
                printMethod();
                createFile();
            } else {
                logi(LOG_PRE_FIX + "find nothings");
            }
            logi(LOG_PRE_FIX + "finish\n\n");
        } catch (Exception e) {
            loge(e);
        }
        return false;
    }

    private void collectionImpls(Set<? extends TypeElement> set, RoundEnvironment env) {
        Set<? extends Element> elements = env.getElementsAnnotatedWith(ServiceImpl.class);
        for (Element element : elements) {

            //if this element is non of class,don't work for that
            if (!(element instanceof TypeElement)) continue;

            TypeElement typeElement = (TypeElement) element;

            TypeMirror mirror = typeElement.asType();
            if (!(mirror.getKind() == TypeKind.DECLARED)) {
                continue;
            }

            List<? extends TypeMirror> superClassElement = types.directSupertypes(mirror);

            if (superClassElement == null || superClassElement.size() <= 0) continue;

            TypeMirror serviceSuperClassElement = null;
            for (TypeMirror typeMirror : superClassElement) {
                logi(LOG_PRE_FIX + typeMirror.toString());

                //check service path
                if (typeMirror.toString().startsWith(SERVICES_BASE_PACKAGE)) {
                    serviceSuperClassElement = typeMirror;
                    break;
                }
            }

            if (serviceSuperClassElement == null) continue;


            TypeName classType = ClassName.get(serviceSuperClassElement);


            if (element.getKind() != ElementKind.CLASS) {
                loge(LOG_PRE_FIX + elements.toString() + " :: @ServiceImpl is only valid for class");
                continue;
            }
            if (!element.getModifiers().contains(PUBLIC)) {
                loge(LOG_PRE_FIX + elements.toString() + "  :: @ServiceImpl is only valid for public class");
                continue;
            }


            List<InnerInfo> innerInfos = null;
            if (mServiceImplMap.containsKey(classType)) {
                innerInfos = mServiceImplMap.get(classType);
            }

            if (innerInfos == null) {
                innerInfos = new ArrayList<>();
                mServiceImplMap.put(classType, innerInfos);
            }

            int tag = element.getAnnotation(ServiceImpl.class).value();

            InnerInfo info = new InnerInfo(typeElement, typeElement.getQualifiedName().toString(), tag);
            innerInfos.add(info);
        }
    }

    private void createFile() {
        try {
            logi(LOG_PRE_FIX + "generate code");
            JavaFile javaFile = JavaFile.builder(GEN_PACKAGE, createType())
                    .addFileComment("$S", "Generated code from " + GEN_PACKAGE + "." + GENERATE_FILE_NAME + " Do not modify!")
                    .build();
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
            loge(e);
        }
    }


    private TypeSpec createType() {
        TypeSpec.Builder result = TypeSpec.classBuilder(GENERATE_FILE_NAME + "$$" + moduleName)
                .addModifiers(PUBLIC, FINAL);
        result.addSuperinterface(ClassName.get(getTypeElement(TARGET_PACKAGE + INTERFACE_PROCESSOR)));
        result.addField(createMapField());
        result.addMethod(createMethod());
        result.addStaticBlock(fillStaticBlock());
        return result.build();
    }

    private FieldSpec createMapField() {
        ParameterizedTypeName typeMap = ParameterizedTypeName.get(ClassName.get(HashMap.class),
                ClassName.get(Class.class),
                ParameterizedTypeName.get(ClassName.get("android.util", "SparseArray"),
                        ClassName.get(Object.class)));

        FieldSpec.Builder result = FieldSpec.builder(typeMap, FIELD_MAP)
                .addModifiers(PRIVATE, STATIC, FINAL)
                .initializer("new $T()", typeMap);
        return result.build();
    }

    private MethodSpec createMethod() {
        ParameterizedTypeName typeMap = ParameterizedTypeName.get(ClassName.get(HashMap.class),
                ClassName.get(Class.class),
                ParameterizedTypeName.get(ClassName.get("android.util", "SparseArray"),
                        ClassName.get(Object.class)));

        MethodSpec.Builder result = MethodSpec.methodBuilder("getMap")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addStatement("return " + FIELD_MAP)
                .returns(typeMap);

        return result.build();
    }

    private CodeBlock fillStaticBlock() {
        CodeBlock.Builder result = CodeBlock.builder();
        final String serviceInfosName = "servicesImplList";
        TypeName sparseArrayType = ClassName.get("android.util", "SparseArray");

        result.addStatement("\t$T<$T> " + serviceInfosName,
                sparseArrayType,
                ClassName.get(Object.class));

        for (HashMap.Entry<TypeName, List<InnerInfo>> entry : mServiceImplMap.entrySet()) {

            result.addStatement("\t" + serviceInfosName + " = new $T()",
                    sparseArrayType);

            for (InnerInfo innerInfo : entry.getValue()) {
                result.addStatement("\t" + serviceInfosName + ".put($L, new $T())",
                        innerInfo.tag,
                        ClassName.get(innerInfo.mirror));
            }

            result.addStatement("\t" + FIELD_MAP + ".put($T.class, " + serviceInfosName + ")",
                    entry.getKey());

        }
        return result.build();
    }

    private void printMethod() {
        for (HashMap.Entry<TypeName, List<InnerInfo>> entry : mServiceImplMap.entrySet()) {
            String key = entry.getKey().toString();
            StringBuilder builder = new StringBuilder();
            builder.append(" find services impl for : ").append(key).append("  [\n");
            List<InnerInfo> list = entry.getValue();
            if (list != null && list.size() > 0) {
                for (InnerInfo innerInfo : list) {
                    builder.append("tag = ")
                            .append(String.valueOf(innerInfo.tag))
                            .append('\n')
                            .append("class name = ")
                            .append(innerInfo.fullClassName)
                            .append('\n');

                }
                builder.append("]");
            }
            logi(LOG_PRE_FIX + builder);
        }
    }
}
