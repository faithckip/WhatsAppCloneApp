package com.example.whatsappcloneapp.data

data class UserData(
    val userId: String? = "",
    val name: String? = "",
    val number: String? = "",
    val imageUrl: String? = "",
    val status: String? = " ",
    val statusImg: String? =" ",
    val contacts: List<String>? = listOf()
){
    fun toMap() = mapOf(
        "userId" to userId,
        "name" to  name,
        "number" to number,
        "imageUrl" to imageUrl,
        "status" to status,
        "statusImg" to statusImg,
        "contacts" to contacts
    )
}

data class ChatData(                                //contains info  on who is in the chat
    val chatId: String? = " ",
    val user1: ChatUser = ChatUser(),
    val user2: ChatUser = ChatUser()
)

data class ChatUser(                                    //contains info on the chatuser /user1
    val userId: String? = "",
    val name: String? = "",
    val imageUrl: String? ="",
    val number:String? = ""
)

data class Message(                              //data class for sending message
    val sentBy: String? = "",
    val message: String? ="",                     //actual message being sent
    val timestamp: String? =""
)

data class Status(
    val user: ChatUser = ChatUser(),
    val imageUrl: String? = "",
    val timestamp: Long? = null
)
