package server.ligma.di

import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import server.ligma.data.MessageDataSource
import server.ligma.data.MessageDataSourceImpl
import server.ligma.room.RoomController
import kotlin.math.sin

val mainModule = module {
    single {
        org.litote.kmongo.reactivestreams.KMongo.createClient().coroutine.getDatabase("message_db")
    }

    single<MessageDataSource> {
        MessageDataSourceImpl(get())
    }

    single {
        RoomController(get())
    }
}