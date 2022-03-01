package com.github.the_sidh.kobbit

import kotlin.reflect.KFunction

data class State(
    val status: Status,
    val node: Node?,
    val context: Context
)

enum class Status { RUNNING, COMPLETED }

enum class Outcome { SUCCESS, FAILURE, UNKNOWN }

data class Node(
    val description: String,
    val command: KFunction<Outcome>,
    val attemptsLeft: Int,
    val nextOnSuccess: Node?,
    val nextOnFailure: Node?
)

typealias Context = Map<String, Any>
