package com.github.the_sidh.kobbit

const val NO_DEFINED_HANDLER_FOR_FAILURE_MESSAGE =
    "The command failed and there isn't an action defined to handle the failure. Aborting saga execution"
const val UNKNOWN_OUTCOME_EXCEPTION_MESSAGE = "Unknown outcome for command execution. Aborting saga execution"

fun invoke(state: State): State {
    return state.node?.let { node ->
        val newState = when (runCommand(node, state.context)) {
            Outcome.SUCCESS -> state.copy(node = node.nextOnSuccess)
            Outcome.FAILURE -> stateOnFailure(node, state)
            else -> throw SagaException(UNKNOWN_OUTCOME_EXCEPTION_MESSAGE)
        }
        postExecutionState(newState)
    } ?: state.copy(status = Status.COMPLETED)
}

private fun runCommand(node: Node, context: Context) : Outcome{
    val command = node.command
    return command.call(context)
}

private fun postExecutionState(newState: State) = if (newState.node != null)
    newState
else
    newState.copy(status = Status.COMPLETED)

private fun stateOnFailure(
    node: Node,
    state: State
) = if (node.attemptsLeft == 0)
    state.copy(node = node.nextOnFailure ?: throw SagaException(NO_DEFINED_HANDLER_FOR_FAILURE_MESSAGE))
else {
    stateWithNodeAttemptsDecreased(node, state)
}

private fun stateWithNodeAttemptsDecreased(
    node: Node,
    state: State
): State {
    val attemptsLeft = node.attemptsLeft
    val nodeWithOneLessAttemptLeft = node.copy(attemptsLeft = attemptsLeft - 1)
    return state.copy(node = nodeWithOneLessAttemptLeft)
}