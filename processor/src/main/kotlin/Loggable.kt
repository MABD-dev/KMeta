package org.mabd

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toAnnotationSpec
import com.squareup.kotlinpoet.ksp.toKModifier
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Loggable


/**
 * Mark function with this annotation to skip logging in that function
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class NoLog


class LoggableProcessor(
    private val env: SymbolProcessorEnvironment
): SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation("org.mabd.Loggable")

        symbols
            .filterIsInstance<KSClassDeclaration>()
            .forEach { symbol ->
                val klass = symbol as? KSClassDeclaration ?: return@forEach

                if (klass.classKind != ClassKind.INTERFACE) {
                    env.logger.error("Loggable is only applicable for interfaces only")
                }
                val fileSpec = klass.createFile()
                fileSpec.writeTo(env.codeGenerator, Dependencies(false))
            }
        return emptyList()
    }

    private fun KSClassDeclaration.createFile(): FileSpec {
        val packageName = this.packageName.asString()
        val interfaceName = this.simpleName.asString()
        val fileName = "${interfaceName}LoggerImpl"


        val loggerClassName = ClassName(packageName, fileName)
        val interfaceClassName = ClassName(packageName, interfaceName)

        val delegateName = "delegate"

        val delegateProp = PropertySpec.builder(delegateName, interfaceClassName)
            .initializer(delegateName)
            .addModifiers(KModifier.PRIVATE)
            .build()

        val constructor = FunSpec.constructorBuilder()
            .addParameter(delegateName, interfaceClassName)
            .build()

        val loggerClass = TypeSpec.classBuilder(loggerClassName)
            .primaryConstructor(constructor)
            .addProperty(delegateProp)
            .addSuperinterface(interfaceClassName)

        this.docString?.let { loggerClass.addKdoc(it) }

        val functions = this
            .getDeclaredFunctions()
            .map { it.createFunctionSpecs(delegateName, fileName) }
            .toList()

        loggerClass.addFunctions(functions)

        return FileSpec.builder(loggerClassName)
            .addType(loggerClass.build())
            .build()
    }

    private fun KSFunctionDeclaration.createFunctionSpecs(
        delegateName: String,
        fileName: String
    ): FunSpec = FunSpec
        .builder(this.simpleName.asString())
        .addAnnotations(this)
        .addModifiers(KModifier.OVERRIDE)
        .addModifiers(this)
        .addKdocIfFound(this)
        .addParams(this)
        .addFunctionBody(this, delegateName, fileName)
        .addReturnType(this)
        .build()

    private fun FunSpec.Builder.addModifiers(
        func: KSFunctionDeclaration
    ) = this.apply {
        addModifiers(func.modifiers.mapNotNull { it.toKModifier() })
    }

    private fun FunSpec.Builder.addAnnotations(
        func: KSFunctionDeclaration
    ) = this.apply {
        addAnnotations(func.annotations.map { it.toAnnotationSpec()}.toList())
    }

    private fun FunSpec.Builder.addKdocIfFound(
        func: KSFunctionDeclaration
    ) = this.apply {
        func.docString?.let { this.addKdoc(it) }
    }

    private fun FunSpec.Builder.addParams(
        func: KSFunctionDeclaration
    ) = this.apply {
        val params = func.parameters.map { param ->
            ParameterSpec(param.name?.asString() ?: "_", param.type.toTypeName())
        }
        addParameters(params)
    }

    private fun FunSpec.Builder.addReturnType(
        func: KSFunctionDeclaration
    ) = this.apply {
        func.returnType?.toTypeName()?.let { this.returns(it) }
    }

    private fun FunSpec.Builder.addFunctionBody(
        func: KSFunctionDeclaration,
        delegateName: String,
        fileName: String
    ): FunSpec.Builder = this.apply {
        val functionName = func.simpleName.asString()
        val hasReturn = func.returnType?.toString() != "Unit"

        val params2 = func.parameters.map { param ->
            param.name?.getShortName() to param.type
        }
        val paramsNames = params2.joinToString(", ") { "${it.first}" }
        val paramsPrint = params2.joinToString(", ") { "${it.first}=\$${it.first}" }

        this.addStatement("val result = ${delegateName}.${functionName}(${paramsNames})")

        var returnStr = ""
        if (hasReturn) {
            returnStr = "->\$result"
        }

        val doLog = func.annotations
            .filter { it.shortName.asString() == "NoLog" }
            .toList()
            .isEmpty()
        if (doLog) {
            this.addStatement("""println("${fileName}: ${functionName}(${paramsPrint})${returnStr}")""")
        }

        if (hasReturn) {
            this.addStatement("return result")
        }
    }

}

class LoggableProvider: SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return LoggableProcessor(environment)
    }

}
