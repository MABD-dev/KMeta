package mimic_data_class.toStringProcessor.generators

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import common.createGeneratedAnnotation

internal class ToNiceStringFunGenerator (
    private val declaration: KSClassDeclaration
) {

    fun generate(logger: KSPLogger): FunSpec? {
        val constructorParameters = declaration
            .primaryConstructor
            ?.parameters
            ?: run {
                logger.warn("${declaration.qualifiedName?.asString()} has no primary constructor")
                return null
            }

        if (constructorParameters.isEmpty()) {
            logger.warn("${declaration.qualifiedName?.asString()} has no parameters in primary constructor")
            return null
        }

        val className = declaration.toClassName()
        val func = FunSpec.Companion.builder("toNiceString")
            .receiver(className)
            .addAnnotation(createGeneratedAnnotation())

        val parametersNames = constructorParameters
            .mapNotNull {
                val paramName = it.name?.asString() ?: return@mapNotNull null
                // TODO: handle nested @ToNiceString parameters
                val value = "\$${paramName}"
                "${paramName}=$value"
            }
            .joinToString(", ")

        func.addStatement("return \"${className.simpleName}(${parametersNames})\"")
        func.returns(String::class)

        return func.build()
    }

}