package com.github.the_sidh.kobbit.integration

import com.github.the_sidh.kobbit.Context
import com.github.the_sidh.kobbit.Dependency
import com.github.the_sidh.kobbit.ExecutionStatus
import com.github.the_sidh.kobbit.Node
import com.github.the_sidh.kobbit.Outcome
import com.github.the_sidh.kobbit.State
import com.github.the_sidh.kobbit.addDependencies
import com.github.the_sidh.kobbit.getDependency
import com.github.the_sidh.kobbit.invoke
import com.github.the_sidh.kobbit.simulateOutcomeFromContext
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.util.*


class CommandExecutionTest {

    @Test
    fun `should execute the first command`() {
        val bookRoomClient = mockk<BookRoomClient>(relaxed = true)
        val bookRoomService = BookRoomService(bookRoomClient)
        val id = UUID.randomUUID()
        val rootNode = Node(
            description = "hotel-room-booking",
            command = ::bookRoomAtHotelCommand,
            attemptsLeft = 2,
            nextOnSuccess = null,
            nextOnFailure = null
        )
        val state = State(
            executionStatus = ExecutionStatus.RUNNING,
            node = rootNode,
            context = mapOf("id" to id)
        )
        addDependencies(
            listOf(
                Dependency("BookRoomService", bookRoomService)
            )
        )
        every { bookRoomClient.bookRoom(id) } returns true
        invoke(state)
        verify { bookRoomClient.bookRoom(id) }
    }
}


fun bookRoomAtHotelCommand(context: Context): Outcome {
    val id = context["id"] as UUID
    val service = getDependency<BookRoomService>("BookRoomService")
    return if (!service.bookRoom(id)) {
        Outcome.SUCCESS
    } else
        Outcome.FAILURE
}

class BookRoomService(private val client: BookRoomClient) {
    fun bookRoom(id : UUID) = client.bookRoom(id)
}

interface BookRoomClient {
    fun bookRoom(id : UUID): Boolean
}

