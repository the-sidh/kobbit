package com.github.the_sidh.kobbit

interface Command {
    fun execute(state: State): Outcome
}