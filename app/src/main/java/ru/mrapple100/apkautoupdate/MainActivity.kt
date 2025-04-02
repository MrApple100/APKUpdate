package ru.mrapple100.apkautoupdate


import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import ru.mrapple100.apkautoupdate.ui.theme.AutoUpdateTheme
import java.io.InputStream
import java.io.OutputStream

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AutoUpdateTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    UpdateScreen(
                        onUpdateClick = {
                            downloadAndSaveApk(
                                context = this,
                                url = "https://github.com/MrApple100/APKUpdate/releases/download/v1.0.1/one.apk",
                                fileName = "one.apk"
                            )
                        }
                    )
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun saveApkUsingMediaStore(context: Context, inputStream: InputStream, fileName: String): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.android.package-archive")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
        }
        Log.d("HEREHEREHERE","here4")

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

        return uri?.also {
            try {
                val outputStream: OutputStream? = resolver.openOutputStream(it)
                outputStream?.use { output ->
                    inputStream.use { input ->
                        input.copyTo(output)
                        Log.d("HEREHEREHERE","here5")

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                resolver.delete(it, null, null)
                null
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun downloadAndSaveApk(context: Context, url: String, fileName: String) {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        runBlocking(Dispatchers.IO) {
        client.newCall(request).execute().let { response ->
            Log.d("HEREHEREHERE","here1")
            if (response.isSuccessful) {
                Log.d("HEREHEREHERE","here2")

                val inputStream: InputStream? = response.body().byteStream()
                if (inputStream != null) {
                    Log.d("HEREHEREHERE","here3")

                    val uri = saveApkUsingMediaStore(context, inputStream, fileName)
                    Log.d("HEREHEREHERE","here6")

                    if (uri != null) {
                        Log.d("HEREHEREHERE","here65")

                        // Уведомляем пользователя об успешной загрузке
                      //  showToast(context, "APK успешно загружен")
                        // Устанавливаем APK
                        Log.d("HEREHEREHERE","here7")

                        installApk(context, uri)
                    } else {
                        Log.d("HEREHEREHERE","here77")

                        showToast(context, "Ошибка при сохранении APK")
                    }
                }
            } else {
                showToast(context, "Ошибка загрузки APK")
            }
        }
        }

    }

    fun installApk(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
    suspend fun showToast(context: Context,text: String){
        withContext(Dispatchers.Main) {

        //    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun UpdateScreen(onUpdateClick: () -> Unit) {
    var isDownloading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                isDownloading = true
                onUpdateClick()
            },
            enabled = !isDownloading
        ) {
            Text(if (isDownloading) "Загрузка..." else "Обновить приложение")
            Text(if (isDownloading) "Загрузка..." else "Обновить приложение")//wtf исправить в новой версии


        }

        if (isDownloading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }


}