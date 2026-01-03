package com.skinnyy.plantcare.ui.imagepreview

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.skinnyy.plantcare.R
import com.skinnyy.plantcare.ui.theme.PlantCareTheme
import me.saket.telephoto.zoomable.coil3.ZoomableAsyncImage

@Composable
fun ImagePreviewScreen(
    url: String,
    modifier: Modifier = Modifier,
) {
    ImagePreviewContent(url, modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePreviewContent(
    url: String,
    modifier: Modifier = Modifier,
) {
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { onBackPressedDispatcher?.onBackPressedDispatcher?.onBackPressed() }) {
                        Icon(painter = painterResource(R.drawable.ic_arrow_back), null)
                    }
                },
            )
        },
    ) {
        ZoomableAsyncImage(model = url, contentDescription = null, modifier = Modifier.fillMaxSize())
    }
}

@Preview
@Composable
private fun ImagePreviewContentPreview() {
    PlantCareTheme {
        ImagePreviewContent("www.google.com")
    }
}
