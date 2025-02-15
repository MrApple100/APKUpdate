package ru.mrapple100.apkautoupdate

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ru.mrapple100.apkautoupdate.ui.theme.ApkAutoUpdateTheme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.azhon.appupdate.listener.OnButtonClickListener
import com.azhon.appupdate.listener.OnDownloadListenerAdapter
import com.azhon.appupdate.manager.DownloadManager
import com.azhon.appupdate.util.ApkUtil

class MainActivity : ComponentActivity(), OnButtonClickListener {

    companion object {
        private const val TAG = "MainActivity"
    }

    private val url = "https://downv6.qq.com/qqweb/QQ_1/android_apk/Android_9.0.81_64.apk"
    private val apkName = "appupdate.apk"
    private var manager: DownloadManager? = null
    private lateinit var tvPercent: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ApkAutoUpdateTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        onButton1Click = { startUpdate1()},
                        onButton2Click = { startUpdate2() },
                        onButton3Click = { startUpdate3() },
                        onButton4Click = { manager?.cancel()})
                }
            }
        }
        //delete downloaded old Apk
        val result = ApkUtil.deleteOldApk(this, "${externalCacheDir?.path}/$apkName")

    }

    private fun startUpdate1() {
        AlertDialog.Builder(this@MainActivity)
            .setTitle(R.string.dialog_title)
            .setMessage(R.string.dialog_msg)
            .setPositiveButton(R.string.dialog_confirm) { _, _ ->
                startUpdate2()
            }.create()
            .show()
    }

//
//    @Composable
//    fun startUpdate1(
//        onDismissRequest: () -> Unit, // Закрытие диалога
//        onConfirm: () -> Unit,        // Обработка нажатия на кнопку подтверждения
//    ) {
//        AlertDialog(
//            onDismissRequest = onDismissRequest, // Закрытие при нажатии вне диалога
//            title = {
//                Text(text = stringResource(R.string.dialog_title)) // Заголовок
//            },
//            text = {
//                Text(text = stringResource(R.string.dialog_msg)) // Сообщение
//            },
//            confirmButton = {
//                TextButton(
//                    onClick = onConfirm // Обработка нажатия на кнопку подтверждения
//                ) {
//                    Text(text = stringResource(R.string.dialog_confirm))
//                }
//            }
//        )
//    }

    private fun startUpdate2() {
        resetPb()
        manager = DownloadManager.Builder(this).run {
            apkUrl(url)
            apkName(apkName)
            smallIcon(R.mipmap.ic_launcher)
            build()
        }
        manager!!.download()
    }


    private fun startUpdate3() {
        manager = DownloadManager.Builder(this).run {
            apkUrl(url)
            apkName(apkName)
            smallIcon(R.mipmap.ic_launcher)
            showNewerToast(true)
            apkVersionCode(2)
            apkVersionName("v4.2.1")
            apkSize("7.7MB")
            apkDescription(getString(R.string.dialog_msg))
            enableLog(true)
            jumpInstallPage(true)
            dialogButtonTextColor(android.graphics.Color.WHITE)
            showNotification(true)
            showBgdToast(false)
            forcedUpgrade(false)
            onDownloadListener(listenerAdapter)
//            apkMD5("DC501F04BBAA458C9DC33008EFED5E7F")
//            httpManager()
//            dialogImage(R.drawable.ic_dialog)
//            dialogButtonColor(Color.parseColor("#E743DA"))
//            dialogProgressBarColor(Color.parseColor("#E743DA"))
//            notificationChannel()
//            notifyId(1011)
            onButtonClickListener(this@MainActivity)
            build()
        }
        manager?.download()
    }

    private fun resetPb() {
        progressBar.progress = 0
        tvPercent.text = "0%"
    }

    private val listenerAdapter: OnDownloadListenerAdapter = object : OnDownloadListenerAdapter() {

        override fun downloading(max: Int, progress: Int) {
            val curr = (progress / max.toDouble() * 100.0).toInt()
            progressBar.max = 100
            progressBar.progress = curr
            tvPercent.text = "$curr%"
        }
    }

    override fun onButtonClick(id: Int) {
        Log.e(TAG, "onButtonClick: $id")
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ApkAutoUpdateTheme {
        Greeting("Android")
    }
}



@Composable
fun MainScreen(
    onButton1Click: () -> Unit,
    onButton2Click: () -> Unit,
    onButton3Click: () -> Unit,
    onButton4Click: () -> Unit,
) {
    var progress by remember { mutableStateOf(0f) }
    var percent by remember { mutableStateOf("0%") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Progress Bar and Percentage Text
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                color = Color.Blue
            )
            Text(
                text = percent,
                color = Color(0xFF333333),
                fontSize = 16.sp
            )
        }

        // Channel Text
        Text(
            text = "Channel Info",
            color = Color(0xFF333333),
            modifier = Modifier.padding(top = 40.dp),
            fontSize = 16.sp
        )

        // Buttons
        Button(
            onClick = onButton1Click,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text(text = stringResource(R.string.layout_custom_dialog))
        }

        Button(
            onClick = onButton2Click,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(text = stringResource(R.string.layout_simple_use))
        }

        Button(
            onClick = onButton3Click,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(text = stringResource(R.string.layout_library_dialog))
        }

        Button(
            onClick = onButton4Click,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(text = stringResource(R.string.layout_cancel))
        }
    }
}