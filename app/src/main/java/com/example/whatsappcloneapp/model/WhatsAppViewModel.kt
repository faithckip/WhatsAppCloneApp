package com.example.whatsappcloneapp.model

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.navigation.compose.rememberNavController
import com.example.whatsappcloneapp.data.COLLECTION_CHAT
import com.example.whatsappcloneapp.data.COLLECTION_MESSAGES
import com.example.whatsappcloneapp.data.COLLECTION_STATUS
import com.example.whatsappcloneapp.data.COLLECTION_USER
import com.example.whatsappcloneapp.data.ChatData
import com.example.whatsappcloneapp.data.ChatUser
import com.example.whatsappcloneapp.data.Event
import com.example.whatsappcloneapp.data.Message
import com.example.whatsappcloneapp.data.Status
import com.example.whatsappcloneapp.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.core.Context
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class WhatsAppViewModel @Inject constructor(
    val auth: FirebaseAuth,
    val db: FirebaseFirestore,
    val storage: FirebaseStorage,
    var context : Context
) : ViewModel(){

    val inProgress = mutableStateOf(false)
    val popupNotification = mutableStateOf<Event<String>?>(null)
    val signedIn = mutableStateOf(false)
    val userData = mutableStateOf<UserData?>(null)

    val chats = mutableStateOf<List<ChatData>>(listOf())    //lists the chats of a user
    val inProgressChats = mutableStateOf(false)          //similar to inprogress only that it relates to chats

    val inProgressChatMessages = mutableStateOf(false)     //spinner for receiving the chat messages
    val chatMessages = mutableStateOf<List<Message>>(listOf())
    var currentChatMessagesListener: ListenerRegistration? = null

    val inProgressStatus = mutableStateOf(false)
    val status = mutableStateOf<List<Status>>(listOf())

    init {
       // auth.signOut()  //used to signout automatically from login screen. now below as onlogout function
        //onlogout()
        val currentUser = auth.currentUser
        signedIn.value = currentUser != null
        currentUser?.uid?.let{ uid ->
            getUserData(uid)
        }
    }

    fun onSignUp(name: String, number:String, email: String, password:String){
        if (name.isEmpty() or number.isEmpty() or email.isEmpty() or password.isEmpty()){
            handleException(customMessage = " Please fill in all fields")
            return
        }
        inProgress.value = true
        db.collection(COLLECTION_USER).whereEqualTo("number", number)
            .get()
            .addOnSuccessListener {
                if (it.isEmpty)
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful){
                                signedIn.value = true
                                //create user profile
                                createOrUpdateProfile(name= name, number = number)
                            } else
                                handleException(task.exception, "SignUp failed")
                        }
                else
                    handleException(customMessage = "username already exists")
                inProgress.value = false
            }
            .addOnFailureListener {
                handleException(it)
            }
    }

    fun onLogin(email:String, password: String){
        if (email.isEmpty() or password.isEmpty()){
            handleException(customMessage = "Please fill in all fields")
            return
        }
        inProgress.value = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    signedIn.value = true
                    inProgress.value = false
                   auth.currentUser?.uid?.let {
                        getUserData(it)
                   }
                }
                else
                    handleException(task.exception, "Login failed")
            }
            .addOnFailureListener {
                handleException(it, "Login failed")
            }
    }

    private fun createOrUpdateProfile(
        name: String? = null,
        number: String? = null,
        imageUrl: String? = null,
        status:  String? = null
    ){
        val uid = auth.currentUser?.uid
        val userData = UserData(
            userId = uid,
            name = name ?: userData.value?.name,
            number = number ?: userData.value?.number,
            imageUrl = imageUrl ?: userData.value?.imageUrl,
           status = status ?: userData.value?.status,
            statusImg = userData.value?.statusImg,
            contacts = userData.value?.contacts
        )
        uid?.let{ uid ->
            inProgress.value = true
            db.collection(COLLECTION_USER).document(uid)
                .get()
                .addOnSuccessListener {
                    if (it.exists())
                        //update User
                        it.reference.update(userData.toMap())
                            .addOnSuccessListener {
                                this.userData.value = userData
                                inProgress.value = false
                                //populateCards()
                            }
                            .addOnFailureListener {
                                handleException(it, " Cannot update user")
                            }
                    else
                    {
                        //create user
                        db.collection(COLLECTION_USER).document(uid).set(userData)
                        inProgress.value = false
                        getUserData(uid)
                    }
                }
                .addOnFailureListener { handleException(it, "Cannot retrieve user")
                }
        }
    }

    private fun getUserData(uid: String){

        inProgress.value = true
        db.collection(COLLECTION_USER).document(uid)
            .addSnapshotListener { value, error ->
                if (error != null)
                    handleException(error, " Cannot retrieve user data")
                if (value != null){
                    val user = value.toObject<UserData>()
                    userData.value = user
                    inProgress.value = false
                    //populateCards
                    populateChats()     //automatically retrieves the data on chats when application starts
                    populateStatuses()    //retrieves data on statuses
                }
            }
    }

    private fun handleException(exception: Exception? = null, customMessage: String = " "){

        Log.e("ChatAppClone", " Chat app exception", exception)
        exception?.printStackTrace()

        val errorMsg = exception?.localizedMessage ?: ""
        val message = if (customMessage.isEmpty()) errorMsg else "$customMessage: $errorMsg"
        popupNotification.value = Event(message)
        inProgress.value = false
        inProgressChats.value = false
        inProgressChatMessages.value = false
        inProgressStatus.value = false
    }

    fun updateProfileData(name: String, number: String, status: String){
        createOrUpdateProfile(name= name, number = number, status = status)}

    private fun uploadImage(uri: Uri, onSuccess: (Uri) -> Unit){
        inProgress.value = true

        val storageRef = storage.reference                         //creates unique id for storage
        val uuid = UUID.randomUUID()                               //gives random UUID
        val imageRef = storageRef.child("image/$uuid")    //creates entry into storage reference
        val uploadTask = imageRef.putFile(uri)

        uploadTask
            .addOnSuccessListener {
                val result = it.metadata?.reference?.downloadUrl    //url we need to display the image
                result?.addOnSuccessListener(onSuccess)
                inProgress.value = false
            }
            .addOnFailureListener {
                handleException(it)

            }
    }

    fun uploadProfileImage(uri: Uri){     //calls a publicly accessible function
        uploadImage(uri){
            createOrUpdateProfile(imageUrl = it.toString())
        }
    }

    fun onlogout(){
        auth.signOut()
        signedIn.value = false
        userData.value = null
        popupNotification.value = Event(" Logged out")
        chats.value = listOf()                                     //resets the chats values when we log out

    }
    fun onAddChat(number: String){
        if (number.isNullOrEmpty() or !number.isDigitsOnly())    //check if number is empty or contains digits only
            handleException(customMessage = "Number must contain only digits")
        else
        { //queries the database if the number/chat already exists in the database else create a new one
            db.collection(COLLECTION_CHAT)
                .where(
                    Filter.or(//checks if chats is user 1 or 2 exists
                        Filter.and(     //check if chat user 1 exists
                            Filter.equalTo("user1.number", number),
                            Filter.equalTo("user2.number", userData.value?.number)  //owner is user 2 (my number)
                        ),
                        Filter.and(
                            Filter.equalTo("user1.number", userData.value?.number), //owner is user 1
                            Filter.equalTo("user2.number", number)
                        )
                    )
                )
                .get()
                .addOnSuccessListener {
                    if ( it.isEmpty){
                        db.collection(COLLECTION_USER).whereEqualTo("number", number)
                            .get()
                            .addOnSuccessListener {
                                if (it.isEmpty)      //if its empty then we can add our chat
                                    handleException(customMessage = "Cannot retrieve user with number $number")
                                else{
                                    val chatPartner = it.toObjects<UserData>()[0]
                                    val id = db.collection(COLLECTION_CHAT).document().id
                                    //val uuid = UUID.randomUUID().toString()
                                    val chat = ChatData(
                                        id,
                                        ChatUser(                           //all variables captures for Chat User in DataTypes
                                            userData.value?.userId,
                                            userData.value?.name,
                                            userData.value?.imageUrl,
                                            userData.value?.number
                                        ),
                                        ChatUser(
                                            chatPartner.userId,
                                            chatPartner.name,
                                            chatPartner.imageUrl,
                                            chatPartner.number
                                        )
                                    )
                                    //puts the above chat objects in the chat database
                                    db.collection(COLLECTION_CHAT).document(id).set(chat)
                                }
                            }
                            .addOnFailureListener {      //handles failure listener
                                handleException(it)
                            }
                    }else {
                        handleException(customMessage = "Chat already exists")
                    }
                }
                .addOnFailureListener { handleException(it) }
        }
    }
    //function to Retrieve chat
    private fun populateChats(){
        inProgressChats.value = true
        db.collection(COLLECTION_CHAT).where(   //interrogates the database for the chats
            Filter.or(
                Filter.equalTo("user1.userId", userData.value?.userId),
                Filter.equalTo("user2.userId", userData.value?.userId)
            )
        )
            .addSnapshotListener { value, error ->
                if (error != null)
                    handleException(error)
                if (value != null)
                    chats.value = value.documents.mapNotNull { it.toObject<ChatData>() }
                inProgressChats.value = false
            }
    }

    //function to send message
    fun onSendReply(chatId: String, message: String){
        val time = Calendar.getInstance().time.toString()         //gives actual time for sent message
        val msg = Message(userData.value?.userId, message, time)

        //retrieves particular chat
        db.collection(COLLECTION_CHAT)
            .document(chatId)
            .collection(COLLECTION_MESSAGES)
            .document()
            .set(msg)
    }
    
    fun populateChat(chatId: String){
        inProgressChatMessages.value = true                                     //spinner that displays loading in the background
         currentChatMessagesListener = db.collection(COLLECTION_CHAT)
            .document(chatId)
            .collection(COLLECTION_MESSAGES)
            .addSnapshotListener { value, error ->                              //snapshot listener is updated constantly
                if (error != null)
                    handleException(error)
                if (value != null)
                    chatMessages.value = value.documents
                        .mapNotNull { it.toObject<Message>() }
                        .sortedBy { it.timestamp }                               //timestamp sets them in order they have been sent
                inProgressChatMessages.value = false
            }
    }

    fun depopulateChat(){
        currentChatMessagesListener = null                   //cancels the chat using the null listener

        chatMessages.value = listOf()
    }

    //function to retrieve and populate chats
     private fun populateStatuses(){
         inProgressStatus.value = true
         val milliTimeDelta = 24*60*60*1000                  //limit status retrieve to those posted last 24 hours
         val cutoff = System.currentTimeMillis() - milliTimeDelta     //cutoff takes the status within the timelimit 24 hrs
         db.collection(COLLECTION_CHAT)
             .where(
                 Filter.or(                                        //queries chat collection
                     Filter.equalTo("user1.userId", userData.value?.userId),
                     Filter.equalTo("user2.userId", userData.value?.userId)
                 )
             )
             .addSnapshotListener { value, error ->
                 if (error != null)
                     handleException(error)
                 if (value != null){
                     val currentConnections = arrayListOf(userData.value?.userId)     //extracts relevant users. Lists of all connections
                     val chats = value.toObjects<ChatData>()
                     chats.forEach {chat ->
                         if (chat.user1.userId == userData.value?.userId)
                             currentConnections.add(chat.user2.userId)
                         else
                             currentConnections.add(chat.user1.userId)
                     }
                     //retrieves chat of users retrieved from the above arraylist
                     db.collection(COLLECTION_STATUS)
                             .whereGreaterThan("timestamp", cutoff)   //filters for the time within timestamp and greater than cutoff
                             .whereIn("user.userId", currentConnections)
                             .addSnapshotListener { value, error ->
                                 if (error != null)
                                     handleException(error)
                                 if (value != null)
                                     status.value = value.toObjects()
                                 inProgressStatus.value = false
                             }
                 }
             }
     }

    private fun createStatus(imageUrl: String) {
        //creates new status
        val newStatus = Status(
            ChatUser(
                userData.value?.userId,
                userData.value?.name,
                userData.value?.imageUrl,
                userData.value?.number
            ),
            imageUrl,
            System.currentTimeMillis()           //coz timestamp in dataType is type long
        )

        //Adds the created status to database
        db.collection(COLLECTION_STATUS).document().set(newStatus)

    }
    //function uploads the status
    fun uploadStatus(imageUrl: Uri){
        uploadImage(imageUrl){                                         //1st. uploads image
            createStatus(imageUrl = it.toString())                     //then create status
        }
    }
}