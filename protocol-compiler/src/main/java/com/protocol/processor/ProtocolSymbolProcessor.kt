package com.protocol.processor

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.protocol.annotation.IProtocolImplProvider
import com.protocol.annotation.IProtocolProvider
import com.protocol.annotation.Protocol
import com.protocol.annotation.ProtocolImpl
import com.protocol.processor.Constants.PROTOCOL_PROVIDER_PACKAGE
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.writeTo

/**
 * 注解代码处理器
 * <p>
 * Date: 2023-05-25
 *
 * Author: huangyong
 */
class ProtocolSymbolProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val dependencies = Dependencies(false, *resolver.getAllFiles().toList().toTypedArray())
        val protocolSymbols = resolver.getSymbolsWithAnnotation(Protocol::class.qualifiedName ?: "")
        val protocolImplSymbols = resolver.getSymbolsWithAnnotation(ProtocolImpl::class.qualifiedName ?: "")

        protocolSymbols.filter { it is KSClassDeclaration && it.validate() }.forEach {
            generatorProtocol(it as KSClassDeclaration, dependencies)
        }

        protocolImplSymbols.filter { it is KSClassDeclaration && it.validate() }.forEach {
            generatorProtocolImpl(it as KSClassDeclaration, dependencies)
        }

        return emptyList()
    }

    private fun generatorProtocol(element: KSClassDeclaration, dependencies: Dependencies) {
        val className = element.simpleName.asString() + "$\$Protocol$\$Get"
        val argument = element.annotations.firstOrNull { it.shortName.asString() == "Protocol" }?.arguments?.firstOrNull()?.value

        val classType = TypeSpec.classBuilder(className)
            .primaryConstructor(FunSpec.constructorBuilder().build())
            .addModifiers(KModifier.PUBLIC, KModifier.FINAL)
            .addSuperinterface(IProtocolProvider::class)
            .addFunction(
                FunSpec.builder("getProtocol")
                    .addModifiers(KModifier.PUBLIC, KModifier.FINAL, KModifier.OVERRIDE)
                    .returns(String::class)
                    .addCode("return \"$argument\"")
                    .build()
            )
            .build()

        val fileType = FileSpec.builder(PROTOCOL_PROVIDER_PACKAGE, className).addType(classType).build()

        fileType.writeTo(environment.codeGenerator, dependencies)
    }

    private fun generatorProtocolImpl(element: KSClassDeclaration, dependencies: Dependencies) {
        val argument = element.annotations.firstOrNull { it.shortName.asString() == "ProtocolImpl" }?.arguments?.firstOrNull()?.value
        val className = argument.toString() + "$\$ProtocolImpl$\$Get"
        val implClassPath = element.packageName.asString() + "." + element.simpleName.asString()

        val classType = TypeSpec.classBuilder(className)
            .primaryConstructor(FunSpec.constructorBuilder().build())
            .addModifiers(KModifier.PUBLIC, KModifier.FINAL)
            .addSuperinterface(IProtocolImplProvider::class)
            .addFunction(
                FunSpec.builder("getProtocolImpl")
                    .addModifiers(KModifier.PUBLIC, KModifier.FINAL, KModifier.OVERRIDE)
                    .returns(String::class)
                    .addCode("return \"$implClassPath\"")
                    .build()
            )
            .build()

        val fileType = FileSpec.builder(PROTOCOL_PROVIDER_PACKAGE, className).addType(classType).build()

        fileType.writeTo(environment.codeGenerator, dependencies)
    }

}