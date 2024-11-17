package com.example.fisherman.ui.theme.components


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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.fisherman.ui.theme.theme.FishermanTheme

@Composable
fun FMScreen(
    viewModel: ComponentViewModel,
    onSendGallery: () -> Unit = {},
    onSendMessages: () -> Unit = {}
){
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Malicious App", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onSendGallery) {
                Text("Send Gallery Files")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onSendMessages) {
                Text("Send SMS Messages")
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FishermanTheme {
        FMScreen(viewModel = ComponentViewModel())
    }
}