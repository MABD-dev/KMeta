package org.mabd

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class MyAnnotation


class MyProcessor(
    val env: SymbolProcessorEnvironment
): SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation("org.mabd.MyAnnotation")
        symbols.forEach { symbol ->
            if (symbol is KSClassDeclaration) {
                env.logger.warn("Found class ${symbol.simpleName.asString()}")
            }
        }
        return emptyList()
    }
}

class MyProcessorProvider: SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return MyProcessor(environment)
    }

}
