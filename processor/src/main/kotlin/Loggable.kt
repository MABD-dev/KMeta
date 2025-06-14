package org.mabd

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import java.io.OutputStreamWriter

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Loggable


class LoggableProcessor(
    val env: SymbolProcessorEnvironment
): SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        println("Process function")
        val symbols = resolver.getSymbolsWithAnnotation("org.mabd.Loggable")

        symbols
            .filterIsInstance<KSClassDeclaration>()
            .forEach { symbol ->
                val kclass = symbol as? KSClassDeclaration ?: return@forEach

                if (kclass.classKind != ClassKind.INTERFACE) {
                    env.logger.error("Loggable is only applicable for interfaces only")
                }

//                val functionsStr = symbol.functionsString().joinToString("\n")
//                env.logger.warn(functionsStr)

                val packageName = symbol.packageName.asString()
                val interfaceName = symbol.simpleName.asString()
                val fileName = "${interfaceName}LoggerImpl"

                val file = env.codeGenerator.createNewFile(
                    dependencies = Dependencies(aggregating = false),
                    packageName = packageName,
                    fileName = fileName
                )
                file
                    .writer()
                    .use { writer ->
                        writer.writeLoggerImpl(packageName, interfaceName, fileName, symbol)
                    }
            }



//        symbols.forEach { symbol ->
//            env.logger.warn("testing shit")

            // Limit usage to interfaces only

//            env.logger.warn("functions:-----")
//            kclass.getAllFunctions().forEach functions@ { ksFunctionDecleration ->
//                if (ksFunctionDecleration.functionKind != FunctionKind.MEMBER) return@functions
//
//                env.logger.warn("function: name=${ksFunctionDecleration.simpleName.asString()}, returnType=${ksFunctionDecleration.returnType}")
//                env.logger.warn("\tparams:-----")
//                ksFunctionDecleration.parameters.forEach { param ->
//                    env.logger.warn("\tparam name=${param.name?.getShortName()}, isVal=${param.isVal}, isVar=${param.isVar}, type=${param.type}, hasDefault=${param.hasDefault}, ")
//                }
//                env.logger.warn("\t")
//
//            }
//            env.logger.warn("---------")
//        }
        return emptyList()
    }

    private fun OutputStreamWriter.writeLoggerImpl(
        packageName: String,
        interfaceName: String,
        fileName: String,
        symbol: KSClassDeclaration
    ) {
        val delegateName = "delegate"
        val functionsStr = symbol
            .functionsBody(fileName, delegateName)
            .joinToString("\n")

        write(
            """
            package $packageName
            
            class $fileName(
                private val $delegateName: $interfaceName
            ): $interfaceName {
                $functionsStr
                
            }
            """.trimIndent()
        )
    }

    private fun KSClassDeclaration.functionsBody(
        fileName: String,
        delegateName: String
    ): Sequence<String> {
        return this.getDeclaredFunctions().map { func ->
            val functionName = func.simpleName.asString()
            val returnType = func.returnType
            val params = func.parameters.map { param ->
                param.name?.getShortName() to param.type
            }
//                .joinToString(", ")
            val paramsString = params.joinToString(", ") { "${it.first}: ${it.second}" }
            val paramsNames = params.joinToString(", ") { "${it.first}" }
            val paramsPrint = params.joinToString(", ") { "${it.first}=\$${it.first}" }

            val returnStr = if (returnType?.toString() == "Unit") "" else "return "


//            return delegate.test3(b)
//                .also { println("ShitLogger: test3(b=$b)=$it") }
            """
                override fun ${functionName}(${paramsString}): ${returnType} {
                    ${returnStr}${delegateName}.${functionName}(${paramsNames})
                        .also { println("${fileName}: ${functionName}(${paramsPrint})=\$" + it) }
                }
            """.trimIndent()
        }
    }

}

class LoggableProvider: SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return LoggableProcessor(environment)
    }

}
