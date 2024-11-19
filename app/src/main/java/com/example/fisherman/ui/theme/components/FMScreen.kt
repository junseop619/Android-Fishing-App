package com.example.fisherman.ui.theme.components


import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.fisherman.ui.theme.theme.FishermanTheme

@Composable
fun FMScreen(
    viewModel: ComponentViewModel
){
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val context = LocalContext.current
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                viewModel.readImage() // 권한이 허용되면 이미지 읽기 시작
            } else {
                Toast.makeText(context, "권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // 권한 요청 이벤트 관찰
        val requestImagePermissionEvent = viewModel.imagePermissionEvent.collectAsState().value
        val requestMessagePermissionEvent = viewModel.imagePermissionEvent.collectAsState().value

        LaunchedEffect(requestImagePermissionEvent) {
            if (requestImagePermissionEvent != null) { // null이 아닌 경우에만 실행
                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                viewModel.resetImagePermissionEvent() // 이벤트를 초기화
            }
        }



        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Malicious App", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {viewModel.readImage()}) {
                Text("Send Gallery Files")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {viewModel.readMessage()}) {
                Text("Send SMS Messages")
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FishermanTheme {
        val context = LocalContext.current
        FMScreen(viewModel = ComponentViewModel(context))
    }
}