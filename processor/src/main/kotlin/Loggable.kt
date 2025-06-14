package org.mabd

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import java.io.OutputStreamWriter

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Loggable


class LoggableProcessor(
    private val env: SymbolProcessorEnvironment
): SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        println("Process function")
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

        val functions = this.getDeclaredFunctions().map { func ->
            val functionName = func.simpleName.asString()

            val funcBuilder = FunSpec
                .builder(functionName)
                .addModifiers(KModifier.OVERRIDE)

            // set function parameters
            val params = func.parameters.map { param ->
                ParameterSpec(param.name?.asString() ?: "_", param.type.toTypeName())
            }
            funcBuilder.addParameters(params)

            // set function return type

            func.returnType?.toTypeName()?.let { funcBuilder.returns(it) }

            // set function body
            val hasReturn = func.returnType?.toString() != "Unit"

            val params2 = func.parameters.map { param ->
                param.name?.getShortName() to param.type
            }
            val paramsNames = params2.joinToString(", ") { "${it.first}" }
            val paramsPrint = params2.joinToString(", ") { "${it.first}=\$${it.first}" }

            funcBuilder.addStatement("val result = ${delegateName}.${functionName}(${paramsNames})")

            var returnStr = ""
            if (hasReturn) {
                returnStr = "->\$result"
            }

            funcBuilder.addStatement("""println("${fileName}: ${functionName}(${paramsPrint})${returnStr}")""")
            if (hasReturn) {
                funcBuilder.addStatement("return result")
            }

            funcBuilder.build()
        }

        loggerClass.addFunctions(functions.toList())

        return FileSpec.builder(loggerClassName)
            .addType(loggerClass.build())
            .build()
    }

}

class LoggableProvider: SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return LoggableProcessor(environment)
    }

}
