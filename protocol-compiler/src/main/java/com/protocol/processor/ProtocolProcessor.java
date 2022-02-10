package com.protocol.processor;

import com.protocol.annotation.IProtocolImplProvider;
import com.protocol.annotation.IProtocolProvider;
import com.protocol.annotation.ProtocolImpl;
import com.protocol.annotation.Protocol;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
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

/**
 * Created by ljq on 2019/5/8
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({Constants.ANNOTATION_IMPL, Constants.ANNOTATION_PROXY})
@AutoService(Processor.class)
public class ProtocolProcessor extends AbstractProcessor {

    private Filer mFiler;
    private Elements mElementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnvironment.getFiler();
        mElementUtils = processingEnvironment.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> protocolImplSet = roundEnvironment.getElementsAnnotatedWith(ProtocolImpl.class);
        Set<? extends Element> protocolSet = roundEnvironment.getElementsAnnotatedWith(Protocol.class);
        if (protocolSet != null && protocolSet.size() > 0) {
            createProtocol2(protocolSet, roundEnvironment);
        }
        if (protocolImplSet != null && protocolImplSet.size() > 0) {
            createProtocolImpl2(protocolImplSet, roundEnvironment);
        }
        return true;
    }

    private void createProtocol2(Set<? extends Element> protocolSet, RoundEnvironment roundEnvironment) {
        for (Element element : protocolSet) {
            Protocol protocol = element.getAnnotation(Protocol.class);
            TypeElement enclosingElement = (TypeElement) element;
            ClassName className = ClassName.get(enclosingElement);
            String route = protocol.value();
            TypeSpec.Builder builder = TypeSpec.classBuilder(className.simpleName() + "$$Protocol$$Get")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addSuperinterface(IProtocolProvider.class);
            MethodSpec.Builder methodBuild = MethodSpec.methodBuilder("getProtocol")
                    .returns(String.class)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addAnnotation(Override.class)
                    .addCode("return \"" + route + "\";\n");
            builder.addMethod(methodBuild.build());
            String pck = Constants.PROTOCOL_PROVIDER_PACKAGE;
            JavaFile file = JavaFile.builder(pck, builder.build()).build();
            try {
                file.writeTo(mFiler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void createProtocolImpl2(Set<? extends Element> protocolImplSet, RoundEnvironment roundEnvironment) {
        for (Element element : protocolImplSet) {
            ProtocolImpl protocol = element.getAnnotation(ProtocolImpl.class);
            TypeElement enclosingElement = (TypeElement) element;
            ClassName className = ClassName.get(enclosingElement);
            String route = protocol.value();
            TypeSpec.Builder builder = TypeSpec.classBuilder(route + "$$ProtocolImpl$$Get")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addSuperinterface(IProtocolImplProvider.class);
            MethodSpec.Builder methodBuild = MethodSpec.methodBuilder("getProtocolImpl")
                    .returns(String.class)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addAnnotation(Override.class)
                    .addCode("return \"" + className.canonicalName() + "\";\n");
            builder.addMethod(methodBuild.build());
            String pck = Constants.PROTOCOL_PROVIDER_PACKAGE;
            JavaFile file = JavaFile.builder(pck, builder.build()).build();
            try {
                file.writeTo(mFiler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 创建LotusImplProvider类，实现ILotusImplProvider接口
     * 创建HashMap，存放所有LotusImpl注解的类的映射
     * 包名增加moduleName用于区分不同mudule，避免生成多个相同包名的LotusProxyProvider产生冲突
     */
    private void createProtocolImpl(Set<? extends Element> protocolSet, RoundEnvironment roundEnvironment) {
        TypeSpec.Builder builder = TypeSpec.classBuilder("ProtocolImplProvider")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(IProtocolImplProvider.class);

        TypeName map = ParameterizedTypeName.get(ClassName.get(HashMap.class), ClassName.get(String.class), ClassName.get(String.class));

        FieldSpec.Builder mapBuild = FieldSpec.builder(map, "map", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer("new HashMap<>()");

        CodeBlock.Builder blockBuilder = CodeBlock.builder();

        String pck = null;

        for (Element element : protocolSet) {
            ProtocolImpl protocol = element.getAnnotation(ProtocolImpl.class);
            TypeElement enclosingElement = (TypeElement) element;
            ClassName className = ClassName.get(enclosingElement);
            String route = protocol.value();
            if (pck == null) {
                pck = mElementUtils.getPackageOf(enclosingElement) + ".protocol.provider";
            }
            blockBuilder.addStatement("map.put(\"$L\", \"$L\")", route, className.simpleName());
        }

        MethodSpec.Builder methodBuild = MethodSpec.methodBuilder("get")
                .returns(Map.class)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addAnnotation(Override.class)
                .addCode("return map;\n");

        builder.addField(mapBuild.build());
        builder.addStaticBlock(blockBuilder.build());
        builder.addMethod(methodBuild.build());
        assert pck != null;
        JavaFile file = JavaFile.builder(pck, builder.build()).build();
        try {
            file.writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
