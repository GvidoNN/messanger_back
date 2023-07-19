package server.ligma.data

import server.ligma.data.model.Message

interface MessageDataSource {

    suspend fun getAllMessages(): List<Message>

    suspend fun insertMessages(message: Message)

    suspend fun deleteMessage(message: Message)

    suspend fun getMessageById(id: String) : Message

}