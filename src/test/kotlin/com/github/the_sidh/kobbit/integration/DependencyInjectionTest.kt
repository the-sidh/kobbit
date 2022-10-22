package com.github.the_sidh.kobbit

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.dsl.module

class DependencyInjectionTest {

    @Test
    fun `given an application with its own koin context, should be able to start it's context without receiving an exception for koin already initialized`() {
        addDependencies(listOf(Dependency("Foo", User("My name is Foo"))))
        val module = module {
            single { (UserRepositoryImpl()) as UserRepository }
        }
        Assertions.assertDoesNotThrow {
            startKoin {
                modules(module)
            }
        }
    }
}

data class User(val name: String)

interface UserRepository {
    fun findUser(name: String): User?
    fun addUsers(users: List<User>)
}

class UserRepositoryImpl : UserRepository {

    private val _users = arrayListOf<User>()

    override fun findUser(name: String): User? {
        return _users.firstOrNull { it.name == name }
    }

    override fun addUsers(users: List<User>) {
        _users.addAll(users)
    }
}
