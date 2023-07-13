package server.ligma.plugins

import io.ktor.server.routing.*
import io.ktor.server.application.*
import org.koin.ktor.ext.inject
import server.ligma.room.RoomController
import server.ligma.routes.chatSocket
import server.ligma.routes.getAllMessages

fun Application.configureRouting() {
    val roomController by inject<RoomController>()

    install(Routing) {
        chatSocket(roomController)
        getAllMessages(roomController)
    }
}
