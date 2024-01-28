package com.example.whatsappcloneapp.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.whatsappcloneapp.model.WhatsAppViewModel
import com.example.whatsappcloneapp.ui.theme.screen.ChatListScreen
import com.example.whatsappcloneapp.ui.theme.screen.LoginScreen
import com.example.whatsappcloneapp.ui.theme.screen.ProfileScreen
import com.example.whatsappcloneapp.ui.theme.screen.SignUpScreen
import com.example.whatsappcloneapp.ui.theme.screen.SingleChatScreen
import com.example.whatsappcloneapp.ui.theme.screen.SingleStatusScreen
import com.example.whatsappcloneapp.ui.theme.screen.StatusListScreen
import com.example.whatsappcloneapp.model.*

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    //val vm = hiltViewModel<WhatsAppViewModel>()
    val vm:WhatsAppViewModel= hiltViewModel()

    NotificationMessage(vm = vm)


    NavHost(navController = navController, startDestination = DestinationScreen.SignUp.route ){

        composable(DestinationScreen.SignUp.route){
            SignUpScreen(navController, vm)
        }

        composable(DestinationScreen.Login.route){
            LoginScreen(navController, vm )
        }
        composable(DestinationScreen.Profile.route){
            ProfileScreen(navController, vm)
        }
        composable(DestinationScreen.StatusList.route){
            StatusListScreen(navController, vm)
        }
        composable(DestinationScreen.ChatList.route){
            ChatListScreen(navController, vm)
        }
        composable(DestinationScreen.SingleChat.route){
            val chatId = it.arguments?.getString("ChatId")   //retrieves chatId
            chatId?.let {
            SingleChatScreen(navController = navController, vm = vm, chatId = it)}
        }
        composable(DestinationScreen.SingleStatus.route){
            val userId = it.arguments?.getString("userId")
            userId?.let {
            SingleStatusScreen(navController = navController, vm=vm, userId = it)}
        }

    }

}




