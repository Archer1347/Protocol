package com.protocol.processor

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

/**
 * 编译器入口
 * <p>
 * Date: 2023-05-25
 *
 * Author: huangyong
 */
class ProtocolSymbolProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return ProtocolSymbolProcessor(environment)
    }

}