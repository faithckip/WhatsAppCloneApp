package com.example.whatsappcloneapp.ui.theme.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.whatsappcloneapp.model.WhatsAppViewModel
import com.example.whatsappcloneapp.model.CommonDivider
import com.example.whatsappcloneapp.model.CommonImage
import com.example.whatsappcloneapp.data.Message
import androidx.compose.foundation.layout.Column as Column

@Composable
fun SingleChatScreen(navController: NavController, vm: WhatsAppViewModel, chatId: String) {


//use a launch effect to call populate and depopulatechat from view model
    LaunchedEffect(key1 = Unit){
        vm.populateChat(chatId)                          //populate chat with chatId
    }
//back handle helps user when clicking the back button
    BackHandler {
        vm.depopulateChat()
    }

    var reply by rememberSaveable { mutableStateOf(" ")}
    val currentChat = vm.chats.value.first{it.chatId == chatId}
    val myUser = vm.userData.value

    val chatUser = if (myUser?.userId == currentChat.user1.userId) currentChat.user2
    else currentChat.user1

    val onSendReply = {
        vm.onSendReply(chatId, reply)
        reply = " "                                         //once the reply has been sent, the message is deleted from chat box with an empty string
    }
    val chatMessages = vm.chatMessages

    Column(modifier = Modifier.fillMaxSize()){
        //Column has 3 components (chat header, message list and Reply Box. 1st build their respective functions)
        //1.Chat header
        ChatHeader(name = chatUser.name?: "", imageUrl = chatUser.imageUrl ?: " "){      //provide empty string for both imageurl & name

            navController.popBackStack()

            //remove chat messages
            vm.depopulateChat()
        }

        //2.Message list - Display message and live chat
        Messages(
            modifier = Modifier.weight(1f),
            chatMessages.value,
            myUser?.userId ?:""

        )


        //3.Reply Box
        ReplyBox(reply = reply, onReplyChange = { reply = it }, onSendReply = onSendReply)
    }


}

@Composable
fun Messages(modifier: Modifier, chatMessages: List<Message>, currentUserId: String) {
    LazyColumn(modifier = Modifier){
//items of type list
        items(chatMessages){ msg ->
            val alignment = if (msg.sentBy == currentUserId) Alignment.End       //alignment.end displays our message on rightside
            else Alignment.Start                                                 //displays user2 message on the leftside

            val color = if (msg.sentBy == currentUserId) Color(0xFF68C400)
            else Color(0xFFC0C0C0)                                           //color for user2 messages

            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = alignment
            ) {
                Text(
                    text = msg.message ?: "",
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(color)
                        .padding(12.dp),
                    color = Color.White,
                    fontWeight = FontWeight.Bold)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReplyBox(reply: String, onReplyChange: (String) -> Unit, onSendReply: () -> Unit) {
    Column (modifier = Modifier.fillMaxWidth())
    {
        CommonDivider()
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            TextField(value = reply, onValueChange = onReplyChange, maxLines = 3 )
            Button(onClick = onSendReply)
            {
                Text(text = "Send")

            }

        }

    }
}


@Composable
fun ChatHeader(name: String, imageUrl: String, onBackClicked: () -> Unit) {

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Rounded.ArrowBack,
            contentDescription = null,
            modifier = Modifier
                .clickable { onBackClicked.invoke() }
                .padding(8.dp)
            )
        CommonImage(
            data = imageUrl,
            modifier = Modifier
                .padding(8.dp)
                .size(50.dp)
                .clip(CircleShape)
        )
        Text(
            text = name,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp))
    }
    CommonDivider()
}

