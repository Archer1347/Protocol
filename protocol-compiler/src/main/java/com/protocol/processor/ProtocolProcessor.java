package com.protocol.processor;

import com.protocol.annotation.IProtocolImplProvider;
import com.protocol.annotation.IProtocolProvider;
import com.protocol.annotation.ProtocolImpl;
import com.protocol.annotation.Protocol;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
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

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnvironment.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> protocolImplSet = roundEnvironment.getElementsAnnotatedWith(ProtocolImpl.class);
        Set<? extends Element> protocolSet = roundEnvironment.getElementsAnnotatedWith(Protocol.class);
        if (protocolSet != null && protocolSet.size() > 0) {
            createProtocol(protocolSet, roundEnvironment);
        }
        if (protocolImplSet != null && protocolImplSet.size() > 0) {
            createProtocolImpl(protocolImplSet, roundEnvironment);
        }
        return true;
    }

    /**
     * 生成接口的Protocol协议的映射类
     * 创建类，类名为：接口名 + $$Protocol$$Get
     * 实现接口IProtocolProvider，实现方法getProtocol并返回Protocol注解的值
     */
    private void createProtocol(Set<? extends Element> protocolSet, RoundEnvironment roundEnvironment) {
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

    /**
     * 生成实现类的Protocol协议的映射类
     * 创建类，类名为：Protocol注解值 + $$ProtocolImpl$$Get
     * 实现接口IProtocolImplProvider，实现方法getProtocolImpl并返回Protocol实现类的全路径
     */
    private void createProtocolImpl(Set<? extends Element> protocolImplSet, RoundEnvironment roundEnvironment) {
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

}
