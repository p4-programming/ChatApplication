package com.example.chatapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var chatViewModel: ChatViewModel
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var roomId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        chatViewModel = ViewModelProvider(this).get(ChatViewModel::class.java)
        messageAdapter = MessageAdapter()

        val recyclerViewMessages: RecyclerView = findViewById(R.id.recyclerViewMessages)
        recyclerViewMessages.adapter = messageAdapter

        val editTextMessage: EditText = findViewById(R.id.editTextMessage)
        val buttonSend: Button = findViewById(R.id.buttonSend)

        roomId = "663870bfa9b6b47f6faa11d4"  // Replace with actual room ID if needed
        chatViewModel.joinRoom(roomId)

        chatViewModel.messages.observe(this) { messages ->
            messageAdapter.submitList(messages)
            recyclerViewMessages.scrollToPosition(messages.size - 1)
        }

        buttonSend.setOnClickListener {
            val messageText = editTextMessage.text.toString()
            if (messageText.isNotBlank()) {
                val message = Message(
                    roomId = roomId,
                    sentBy = "Viewer",
                    broadcaster = "6634c405b9f96049526fe7d0",
                    viewer = "661f9202013795767a946414",
                    message = messageText
                )
                chatViewModel.sendMessage(message)
                editTextMessage.text.clear()
            }
        }
    }
}
