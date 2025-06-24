package org.mabd.copy.generators

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName


class ClassCopyFunGenerator (
    private val declaration: KSClassDeclaration
) {

    fun generate(): FunSpec? {
        val constructorParameters = declaration
            .primaryConstructor
            ?.parameters
            ?: return null

        val className = declaration.toClassName()
        val func = FunSpec.builder("copy")
            .receiver(className)

        val parameterSpecs = constructorParameters.createParameterSpecs()
        func.addParameters(parameterSpecs)

        val parametersNames = constructorParameters
            .mapNotNull { it.name?.asString() }
            .joinToString(", ")

        func.addStatement("return ${className}(${parametersNames})")
        func.returns(declaration.toClassName())

        return func.build()
    }

    private fun List<KSValueParameter>.createParameterSpecs(): List<ParameterSpec> {
        return this.mapNotNull {
            val name = it.name?.asString() ?: return@mapNotNull null
            val type = it.type.toTypeName()

            ParameterSpec.builder(name, type)
                .defaultValue("this.${name}")
                .build()
        }
    }

}