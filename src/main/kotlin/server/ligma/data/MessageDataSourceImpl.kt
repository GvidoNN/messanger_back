package server.ligma.data

import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
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

    override suspend fun deleteMessage(message: Message) {
        messages.deleteOne(Message::id eq message.id)
        println("Удалили сообщение $message")
    }

    override suspend fun getMessageById(id: String): Message {
        print("Находимся в MessageDataSource $id")
        print(messages.findOne(Message::id eq id)!!)
        return messages.findOne(Message::id eq id)!!
    }
}