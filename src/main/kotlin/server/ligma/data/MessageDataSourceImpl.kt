package server.ligma.data

import org.litote.kmongo.coroutine.CoroutineDatabase
import server.ligma.data.model.Message

class MessageDataSourceImpl(
    private val db: CoroutineDatabase
): MessageDataSource {

    private val messages = db.getCollection<Message>()
    override suspend fun getAllMessages(): List<Message> {
        return messages.find().descendingSort(Message::timestamp).toList()
    }

    override suspend fun insertMessages(message: Message) {
        messages.insertOne(message)
    }
}