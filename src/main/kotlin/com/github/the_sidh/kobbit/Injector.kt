package com.github.the_sidh.kobbit

import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import org.koin.dsl.module


inline fun <reified T> getDependency(name: String): T {
    val component = object : SagaKoinComponent {
        val instance = get<Any>(named(name))
    }
    return component.instance as T
}

fun addDependencies(dependencies: List<Dependency>) {
    SagaKoinContext.koinApplication = koinApplication{
        modules(dependencies.map { dependency -> module(createdAtStart = true) { single(named(dependency.name)) { dependency.instance } } })
    }
}

data class Dependency(val name: String, val instance: Any)

internal object SagaKoinContext {
    lateinit var koinApplication: KoinApplication
}

interface SagaKoinComponent : KoinComponent {
    override fun getKoin(): Koin {
        return SagaKoinContext.koinApplication.koin
    }
}