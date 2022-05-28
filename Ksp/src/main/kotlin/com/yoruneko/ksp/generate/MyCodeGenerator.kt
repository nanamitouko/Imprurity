package com.yoruneko.ksp.generate

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import com.squareup.kotlinpoet.ksp.writeTo

class MyCodeGenerator constructor(private val resolver: Resolver) {

    private val symbols: MutableList<KSClassDeclaration> = mutableListOf()

    fun addSymbol(symbol: KSAnnotated) {
        check(symbol is KSClassDeclaration) {
            "KInterface must annotated on Interface !!"
        }
        symbols.add(symbol)
    }

    fun generate(cg: CodeGenerator, logger: KSPLogger) {
        with(logger) {
            withRecord {
                generateInternal(cg)
            }
        }
    }

    context(KSPLogger)
    private fun withRecord(action: () -> Unit) {
        val startTime = System.currentTimeMillis()
        action()
        logging("action cost time: ${System.currentTimeMillis() - startTime}ms")
    }

    @OptIn(DelicateKotlinPoetApi::class, KotlinPoetKspPreview::class)
    private fun generateInternal(cg: CodeGenerator) {
        symbols.forEach { declaration ->
            val pkgName = declaration.packageName.toString() //包名
            val name = declaration.simpleName.toString() //接口名
            val file = FileSpec.builder(pkgName, name)
                .addType(
                    TypeSpec.classBuilder(name)
                        .addFunctions(
                            declaration.getAllFunctions().map {
                                val typeParam = it.typeParameters.toTypeParameterResolver()
                                FunSpec.builder(it.simpleName.toString())
                                    .apply {
                                        if (it.parameters.isNotEmpty()) {
                                            addParameters(it.parameters.map { params ->
                                                ParameterSpec.builder(
                                                    params.name.toString(),
                                                    params.type.toTypeName(typeParam)
                                                ).build()
                                            }.asIterable())
                                        }
                                    }
                                    .apply {
                                        it.returnType?.resolve()?.takeIf { type ->
                                            !type.isAssignableFrom(resolver.builtIns.nothingType)
                                        }?.run {
                                            returns(
                                                this.toTypeName(typeParam),
                                                CodeBlock.of("return %s", generateReturns(this))
                                            ) // todo list support types
                                        }
                                    }
                                    .addModifiers(KModifier.OVERRIDE)
                                    .addStatement("// doNothing")
                                    .build()
                            }.asIterable()
                        ).build()
                ).build()
            file.writeTo(cg, Dependencies.ALL_FILES)
        }
    }

    private fun generateReturns(ksType: KSType): String {
        return with(resolver.builtIns) {
            when {
                ksType.isAssignableFrom(this.stringType) -> "\"\""
                ksType.isAssignableFrom(this.booleanType) -> "false"
                ksType.isAssignableFrom(this.intType) -> "0"
                ksType.isAssignableFrom(this.longType) -> "0L"
                ksType.isAssignableFrom(this.doubleType) -> "0.0"
                ksType.isAssignableFrom(this.floatType) -> "0.0"
                ksType.isAssignableFrom(this.shortType) -> "0"
                ksType.isAssignableFrom(this.charType) -> "\'\'"

                else -> throw Exception("not support type !!")
            }
        }
    }
}


/**
 * @KInterface
 * interface aaa {
 *
 *
 * }
 */