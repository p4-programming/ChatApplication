package com.example.chatapplication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import io.socket.client.IO
import org.json.JSONObject

class ChatViewModel : ViewModel() {
    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> get() = _messages
    val socketUrl = "http://13.58.147.138:8082".trim()
    val socket = IO.socket(socketUrl)
//    private val socket: Socket = IO.socket(SocketConstants.SOCKET_URI)

    init {
        socket.connect()
        listenForMessages()
        listenForRoomJoinStatus()
    }

    private fun listenForMessages() {
        socket.on(SocketConstants.EVENT_LISTEN_MESSAGE) { args ->
            val messageJson = args[0] as JSONObject
            val message = Gson().fromJson(messageJson.toString(), Message::class.java)
            addMessage(message)
        }
    }

    private fun listenForRoomJoinStatus() {

        socket.on(SocketConstants.EVENT_ROOM_LISTEN) { args ->
            Log.d("SocketEvent", "Event args: ${args.joinToString()}")
            if (args.isNotEmpty()) {
                val statusJson = args[0] as JSONObject
                Log.d("SocketEvent", "Received JSON: ${statusJson.toString()}")
                val statusMessage = statusJson.optString("status")
                Log.i("SocketEvent", "listenForRoomJoinStatus: $statusMessage")
            } else {
                Log.e("SocketEvent", "No data received")
            }
        }
    }


    private fun addMessage(message: Message) {
        val updatedMessages = _messages.value.orEmpty().toMutableList()
        updatedMessages.add(message)
        _messages.postValue(updatedMessages)
    }

    fun joinRoom(roomId: String) {
        val joinRoomPayload = JSONObject().apply {
            put(SocketConstants.ROOM_ID, roomId)
        }
        Log.d("SocketEvent", "Joining room with payload: $joinRoomPayload")
        socket.emit(SocketConstants.EVENT_ROOM_JOIN, joinRoomPayload)
    }

    fun sendMessage(message: Message) {
        val sendMessagePayload = JSONObject().apply {
            put(SocketConstants.ROOM_ID, message.roomId)
            put(SocketConstants.SEND_BY, message.sentBy)
            put(SocketConstants.BROADCASTER, message.broadcaster)
            put(SocketConstants.VIEWER, message.viewer)
            put(SocketConstants.MESSAGE, message.message)
        }
        socket.emit(SocketConstants.EVENT_SEND_MESSAGE, sendMessagePayload)
        addMessage(message)
    }

    override fun onCleared() {
        super.onCleared()
        socket.disconnect()
    }
}
