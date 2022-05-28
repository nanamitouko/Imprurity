package com.yoruneko.ksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.validate
import com.yoruneko.annotation.KInterface
import com.yoruneko.ksp.generate.MyCodeGenerator

class GenerateProcessor constructor(private val env: SymbolProcessorEnvironment) : SymbolProcessor {

    @Volatile
    private var loaded = false

    private lateinit var codeGenerator: MyCodeGenerator

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (loaded) return emptyList()
        loaded = true
        codeGenerator = MyCodeGenerator(resolver)
        val symbols = resolver.getSymbolsWithAnnotation(KInterface::class.java.name)
        symbols.asSequence().forEach {
            codeGenerator.addSymbol(it)
        }
        codeGenerator.generate(env.codeGenerator, env.logger)
        return symbols.toList().filter { it.validate() }
    }

}