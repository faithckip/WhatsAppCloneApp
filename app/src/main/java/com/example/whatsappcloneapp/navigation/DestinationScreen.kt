package com.example.whatsappcloneapp.navigation

sealed class DestinationScreen(val route: String){

    object SignUp: DestinationScreen("signup")
    object Login: DestinationScreen("login")
    object Profile: DestinationScreen("profile")
    object ChatList: DestinationScreen("chatList")
    object SingleChat: DestinationScreen("singleChat/{chatId}"){
        fun createRoute(id: String) = "singleChat/$id"
    }
    object StatusList: DestinationScreen("statusList")
    object SingleStatus: DestinationScreen("singleStatus/{userId}"){
        fun createRoute(userId: String?) = "singleStatus/$userId"
    }
}
