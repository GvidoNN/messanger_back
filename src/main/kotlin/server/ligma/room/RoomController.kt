package server.ligma.room

import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import server.ligma.data.MessageDataSource
import server.ligma.data.model.Message
import java.util.concurrent.ConcurrentHashMap

class RoomController(
    private val messageDataSource: MessageDataSource
) {
    private val members = ConcurrentHashMap<String, Member>()

    fun onJoin(
        userName: String,
        sessionId: String,
        socket: WebSocketSession
    ) {
        if (members.contains(userName)) {
            throw MemberAlreadyExistException()
        }

        members[userName] = Member(
            username = userName,
            sessionId = sessionId,
            socket = socket
        )
    }

    suspend fun sendMessage(
        senderUserName: String,
        message: String
    ) {
        members.values.forEach{
            val messageEntity = Message(
                text = message,
                userName = senderUserName,
                timestamp = System.currentTimeMillis()
            )
            messageDataSource.insertMessages(messageEntity)

            val parsedMessage = Json.encodeToString(messageEntity)
            it.socket.send(frame = Frame.Text(parsedMessage))
        }
    }

    suspend fun deleteMessage(message: Message) {
        messageDataSource.deleteMessage(message)
    }

    suspend fun getMessageById(id: String): Message? {
        return messageDataSource.getMessageById(id)
    }

    suspend fun getAllMessages(): List<Message> {
        return messageDataSource.getAllMessages()
    }

    suspend fun disconnect(username : String) {
        members[username]?.socket?.close()
        if(members.containsKey(username)) {
            members.remove(username)
        }
    }
}