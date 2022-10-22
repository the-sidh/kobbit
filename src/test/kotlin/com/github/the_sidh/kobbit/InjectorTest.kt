package com.github.the_sidh.kobbit

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class InjectorTest {

    @Test
    fun `should add a retrievable to the injection context`() {
        addDependencies(
            listOf(
                Dependency("SumTwoNumbersService2", SumTwoNumbersService(1, 2)),
                Dependency("User", User("ee")),
            )
        )
        val retrieved = getDependency<SumTwoNumbersService>("SumTwoNumbersService2")
        Assertions.assertTrue(retrieved is SumTwoNumbersService)
    }
}

class SumTwoNumbersService(private val a: Int, private val b: Int) {
    fun action() = a + b
}
