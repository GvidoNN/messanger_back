package server.ligma.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import org.litote.kmongo.util.idValue
import server.ligma.room.MemberAlreadyExistException
import server.ligma.room.RoomController
import server.ligma.sessions.ChatSession


fun Route.chatSocket(roomController: RoomController) {
    webSocket(path = "/chat-socket") {
        val session = call.sessions.get<ChatSession>()
        if (session == null) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session."))
            return@webSocket
        }
        try {
            roomController.onJoin(
                userName = session.userName,
                sessionId = session.sessionId,
                socket = this
            )
            incoming.consumeEach {
                if (it is Frame.Text) {
                    val messageText = it.readText()
                    println(messageText)
                    if (messageText.startsWith("/delete")) {
                        println("Удаляем")
                        println(messageText.length)
                        val messageEdit = messageText.substring(7,messageText.length)
                        println("Оно самое -> $messageEdit")
                        val messageToDelete = roomController.getMessageById(messageEdit)
                        if (messageToDelete != null) {
                            roomController.deleteMessage(messageToDelete)
                        }
                    } else {
                        roomController.sendMessage(
                            senderUserName = session.userName,
                            message = messageText
                        )
                    }
                }
            }
        } catch (e: MemberAlreadyExistException) {
            call.respond(HttpStatusCode.Conflict)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            roomController.disconnect(session.userName)
        }
    }

}

fun Route.getAllMessages(roomController: RoomController) {
    get("/messages") {
        call.respond(HttpStatusCode.OK, roomController.getAllMessages())
    }
}