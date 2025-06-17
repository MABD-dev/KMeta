package org.mabd.loggable

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.*

internal fun KSFunctionDeclaration.createFunctionSpecs(
    delegateName: String,
    args: Loggable
): FunSpec = FunSpec
    .builder(this.simpleName.asString())
    .addAnnotations(this)
    .addModifiers(KModifier.OVERRIDE)
    .addModifiers(this)
    .addTypeVariableIfFound(this)
    .addKdocIfFound(this)
    .addParams(this)
    .addFunctionBody(this, delegateName, args)
    .addReturnType(this)
    .build()

internal fun FunSpec.Builder.addAnnotations(
    func: KSFunctionDeclaration
) = this.apply {
    addAnnotations(func.annotations.map { it.toAnnotationSpec()}.toList())
}

internal fun FunSpec.Builder.addModifiers(
    func: KSFunctionDeclaration
) = this.apply {
    addModifiers(func.modifiers.mapNotNull { it.toKModifier() })
}

private fun FunSpec.Builder.addTypeVariableIfFound(
    func: KSFunctionDeclaration
) = this.apply {
    val typeVariables = func.typeParameters.map { it.toTypeVariable() }
    this.addTypeVariables(typeVariables)
}

internal fun KSTypeParameter.toTypeVariable(): TypeVariableName {
    val name = this.name.asString()
    val bounds = this.bounds.map { it.toTypeName() }.toList()
    return if (bounds.isEmpty()) TypeVariableName(name)
    else TypeVariableName(name, bounds)
}

internal fun FunSpec.Builder.addKdocIfFound(
    func: KSFunctionDeclaration
) = this.apply {
    func.docString?.let { this.addKdoc(it) }
}

internal fun FunSpec.Builder.addParams(
    func: KSFunctionDeclaration
) = this.apply {
    val params = func.parameters.map { param ->
        val modifiers =  if (param.isVararg) arrayOf(KModifier.VARARG) else arrayOf()

        ParameterSpec(
            name = param.name?.asString() ?: "_",
            type = param.type.toTypeName(),
            modifiers = modifiers
        )
    }
    addParameters(params)
}

internal fun FunSpec.Builder.addReturnType(
    func: KSFunctionDeclaration
) = this.apply {
    val typeParameterResolver = func.typeParameters.toTypeParameterResolver()
    func.returnType?.resolve()?.toTypeName(typeParameterResolver)?.let {
        this.returns(it)
    }
}

internal fun FunSpec.Builder.addFunctionBody(
    func: KSFunctionDeclaration,
    delegateName: String,
    args: Loggable
): FunSpec.Builder = this.apply {
    val functionName = func.simpleName.asString()
    val hasReturn = func.returnType?.toString() != "Unit"

    val paramsNames = func.parameters.joinToString(", ") { param ->
        val varargStr = if (param.isVararg) "*" else ""
        "${varargStr}${param.name?.getShortName()}"
    }
    val paramsPrint = func.parameters.joinToString(", ") { param ->
        val name = param.name?.getShortName()
        val varargStr = if (param.isVararg) ".toList()" else ""
        "${name}=\${${name}${varargStr}}"
    }

    val typedVariableNames = func.typeParameters.joinToString(", ") { it.name.asString() }
        .let { if (it.isBlank()) "" else "<$it>" }

    this.addStatement("val result = ${delegateName}.${functionName}${typedVariableNames}(${paramsNames})")

    var returnStr = ""
    if (hasReturn) {
        returnStr = "->\$result"
    }

    if (func.annotations.doLog()) {
        this.addStatement("""println("${args.tag}: ${functionName}(${paramsPrint})${returnStr}")""")
    }

    if (hasReturn) {
        this.addStatement("return result")
    }
}
