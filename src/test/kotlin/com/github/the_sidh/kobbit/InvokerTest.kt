package com.github.the_sidh.kobbit

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class InvokerTest {

    private val nodeOnSuccess = Node(
        description = "rent-car",
        command = ::rentCarCommand,
        attemptsLeft = 2,
        nextOnSuccess = null,
        nextOnFailure = null
    )
    private val nodeOnFailure = Node(
        description = "inform-customer",
        command = ::informCustomerAboutFailureCommand,
        attemptsLeft = 2,
        nextOnSuccess = null,
        nextOnFailure = null
    )
    private val rootNode = Node(
        description = "hotel-room-booking",
        command = ::bookRoomAtHotelCommand,
        attemptsLeft = 2,
        nextOnSuccess = nodeOnSuccess,
        nextOnFailure = nodeOnFailure
    )

    private val state = State(
        executionStatus = ExecutionStatus.RUNNING,
        node = rootNode,
        context = mapOf("outcome" to Outcome.SUCCESS)
    )

    @Test
    fun `given that the execution was a success should return a new graph with the supplied next-on-success as current node`() {
        val expectedState = state.copy(node = nodeOnSuccess)
        Assertions.assertEquals(expectedState, invoke(state))
        invoke(state)
    }

    @Test
    fun `given that the execution was a failure and attempts-left is greater than zero should return a new graph with attempts-left decreased by one`() {
        val contextWithTestConditions = mapOf("outcome" to Outcome.FAILURE)
        val nodeWithOneAttemptLeft = rootNode.copy(attemptsLeft = 1)
        val expectedState = state.copy(
            node = nodeWithOneAttemptLeft,
            context = contextWithTestConditions
        )
        Assertions.assertEquals(
            expectedState,
            invoke(
                state.copy(
                    context = contextWithTestConditions
                )
            )
        )
        invoke(state)
    }

    @Test
    fun `given that the execution was a failure and attempts-left is zero should return a new graph with the supplied next-on-failure as current node`() {
        val contextWithTestConditions = mapOf("outcome" to Outcome.FAILURE)
        val nodeWithNoAttemptsLeft = rootNode.copy(attemptsLeft = 0)
        val expectedState = state.copy(
            node = nodeOnFailure,
            context = contextWithTestConditions
        )
        Assertions.assertEquals(
            expectedState, invoke(
                state.copy(
                    node = nodeWithNoAttemptsLeft,
                    context = contextWithTestConditions
                )
            )
        )
        invoke(state)
    }

    @Test
    fun `given that the execution was a failure, attempts-left is zero and there are no next-node-on-failure defined, should throw an exception`() {
        val contextWithTestConditions = mapOf("outcome" to Outcome.FAILURE)
        val nodeWithNoAttemptsLeft = rootNode.copy(attemptsLeft = 0, nextOnFailure = null)

        val exception =
            assertThrows<SagaException> {
                invoke(
                    state.copy(
                        node = nodeWithNoAttemptsLeft,
                        context = contextWithTestConditions
                    )
                )
            }
        Assertions.assertEquals(NO_DEFINED_HANDLER_FOR_FAILURE_MESSAGE, exception.message)
    }

    @Test
    fun `given that the execution outcome was unknown should throw an exception`() {
        val contextWithTestConditions = mapOf("outcome" to Outcome.UNKNOWN)
        val exception =
            assertThrows<SagaException> {
                invoke(
                    state.copy(
                        context = contextWithTestConditions
                    )
                )
            }
        Assertions.assertEquals(UNKNOWN_OUTCOME_EXCEPTION_MESSAGE, exception.message)
    }

    @Test
    fun `given that there are no more nodes, should return a state with status equals to completed`() {
        val expectedState = State(
            executionStatus = ExecutionStatus.COMPLETED,
            node = null,
            context = mapOf("outcome" to Outcome.SUCCESS)
        )

        val startingState = state.copy(node = rootNode.copy(nextOnSuccess = null))

        Assertions.assertEquals(expectedState, invoke(startingState))
    }

}

fun informCustomerAboutFailureCommand(context: Context) = simulateOutcomeFromContext(context)
fun bookRoomAtHotelCommand(context: Context) = simulateOutcomeFromContext(context)
fun rentCarCommand(context: Context) = simulateOutcomeFromContext(context)
fun simulateOutcomeFromContext(context: Context) = context["outcome"] as Outcome