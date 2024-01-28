@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.whatsappcloneapp.ui.theme.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.whatsappcloneapp.model.WhatsAppViewModel
import com.example.whatsappcloneapp.navigation.DestinationScreen
import com.example.whatsappcloneapp.model.CommonProgressSpinner
import com.example.whatsappcloneapp.model.CommonRow
import com.example.whatsappcloneapp.model.TitleText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(navController: NavController, vm: WhatsAppViewModel) {

    val inProgress = vm.inProgressChats.value
    if (inProgress)
        CommonProgressSpinner()
    else{
        val chats = vm.chats.value                               //retrieves chats from the view Model
        val userData = vm.userData.value

        val showDialog = remember{ mutableStateOf(false) }   //used to call FAB function below in the scaffold
        val onFabClick:() -> Unit = {showDialog.value = true}
        val onDismiss: () ->Unit = {showDialog.value= false}

        //call the VM
        val onAddChat: (String) -> Unit = {
            vm.onAddChat(it)
        showDialog.value = false}

        Scaffold (                                                //to use a FAB we integrate using a scaffold
            floatingActionButton =
            {
                FAB(
                showDialog = showDialog.value,
                onFabClick=onFabClick,
                onDismiss = onDismiss,
                onAddChat=onAddChat)},

            content = {
                Column (
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                ){
                    TitleText(txt = "Chats")
                    if (chats.isEmpty())
                        Column (
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ){
                            Text(text = "No chats available")

                        }
                    else{
                        //Fill in chats LazyColumn. updates the chat list
                        LazyColumn(modifier = Modifier.weight(1f)){
                            items(chats){ chat ->                     //for each chat
                                val chatUser = if (chat.user1.userId == userData?.userId) chat.user2   //retrieves the chat user
                                else chat.user1
                                //display a row for above particular element
                                CommonRow(                                       // defined in util.kt
                                    imageUrl = chatUser.imageUrl ?: " ",
                                    name = chatUser.name ?: "---")
                                {
                                    chat.chatId?.let { id ->
                                        navigateTo(
                                            navController,
                                            DestinationScreen.SingleChat.createRoute(id)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    BottomNavigationMenu(
                        selectedItem = BottomNavigationItem.CHATLIST,
                        navController = navController)
                }
            }
        )
    }
}

@Composable
fun FAB(                             //Floating Action Button. FAB is integrated to a UI using a scaffold
    showDialog: Boolean,             //will display a dialog which display the number for the user
    onFabClick: () -> Unit,          // the flag will display or hide the dialog
    onDismiss: () -> Unit,           //used to dismiss the dialog
    onAddChat: (String) -> Unit) {   //add the chat functionality that will connect to view-model

    val addChatNumber = remember { mutableStateOf( "") }

    if (showDialog)
        AlertDialog(                           //alert dialog which is shown only if the  If statement is passed
            onDismissRequest = {
                onDismiss.invoke()
                addChatNumber.value =""         //empty string to clear addChatNumber. Number disappears after the chat is closed
                               },

            confirmButton = {
                Button(onClick = {onAddChat(addChatNumber.value)
                addChatNumber.value =""          //addchatNumber is set to empty string so that it clears after the number is added
                }) {
                    Text(text = "Add chat")
                }
            },
            title ={ Text(text = "Add Chat")},
            text= {
                OutlinedTextField(
                    value = addChatNumber.value,
                    onValueChange = {addChatNumber.value = it},
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number) //restricts the keyboard to only show numbers
                )
            })
    FloatingActionButton(
        onClick = onFabClick,
        containerColor = MaterialTheme.colorScheme.secondary,
        shape = CircleShape,
        modifier = Modifier.padding(bottom = 40.dp))
    {
        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription ="Add Chat",
            tint = Color.White
        )
    }

}
