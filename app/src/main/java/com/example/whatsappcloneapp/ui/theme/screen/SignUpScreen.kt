@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class
)

package com.example.whatsappcloneapp.ui.theme.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.whatsappcloneapp.R
import com.example.whatsappcloneapp.model.WhatsAppViewModel
import com.example.whatsappcloneapp.navigation.DestinationScreen
import com.example.whatsappcloneapp.model.CheckSignedIn
import com.example.whatsappcloneapp.model.CommonProgressSpinner

@Composable
fun SignUpScreen(navController: NavController, vm: WhatsAppViewModel) {

    CheckSignedIn(vm = vm, navController = navController)

    Box (modifier = Modifier.fillMaxSize()){

        Column (
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val nameState = remember{ mutableStateOf(TextFieldValue()) }
            val numberState = remember{ mutableStateOf(TextFieldValue()) }
            val emailState = remember{ mutableStateOf(TextFieldValue()) }
            val passwordState = remember{ mutableStateOf(TextFieldValue()) }

            val focus = LocalFocusManager.current

            Image(
                painter = painterResource(id = R.drawable.whatsapp_chat ),
                contentDescription = null,
                modifier = Modifier
                    .width(200.dp)
                    .padding(top = 16.dp)
                    .padding(8.dp))

            Text(text = "Signup",
                modifier = Modifier.padding(8.dp),
                fontSize = 30.sp,
                fontFamily = FontFamily.SansSerif
            )
            OutlinedTextField(
                value = nameState.value,
                onValueChange = {nameState.value = it},
                modifier = Modifier.padding(8.dp),
                label = { Text(text = "Name")})

            OutlinedTextField(
                value = emailState.value,
                onValueChange = {emailState.value = it},
                modifier = Modifier.padding(8.dp),
                label = { Text(text = "Email")})

            OutlinedTextField(
                value = passwordState.value,
                onValueChange = {passwordState.value = it},
                modifier = Modifier.padding(8.dp),
                label = { Text(text = "Password")},
                visualTransformation = PasswordVisualTransformation())

            Button(
                onClick = { navController.navigate(DestinationScreen.Profile.route)
                focus.clearFocus(force = true)
                vm.onSignUp(
                    nameState.value.text,
                    numberState.value.text,
                    emailState.value.text,
                    passwordState.value.text
                )},
                modifier = Modifier.padding(8.dp),
                )
            {
                Text(text = "SIGN UP")

            }
            Text(text = "Already a user? Go to login ->",
                color = Color.Blue,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        navigateTo(navController, DestinationScreen.Login.route)
                    })
        }
        val isLoading = vm.inProgress.value
        if (isLoading)
            CommonProgressSpinner()
    }
}


@Preview
@Composable
fun signUpprev() {

    SignUpScreen(rememberNavController() , hiltViewModel<WhatsAppViewModel>())

}


