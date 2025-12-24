package com.skinnyy.plantcare.ui.plantchecker

import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.viewfinder.compose.Viewfinder
import androidx.camera.viewfinder.surface.ImplementationMode
import androidx.camera.viewfinder.surface.TransformationInfo
import androidx.camera.viewfinder.surface.ViewfinderSurfaceRequest
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.skinnyy.plantcare.PlantPicker
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.util.concurrent.Executor

@Composable
fun PlantCheckerScreen(
    backStack: NavBackStack<NavKey>,
    modifier: Modifier = Modifier,
    viewModel: PlantCheckerViewModel = koinViewModel(),
) {
    val uiAction = viewModel.uiEvents.collectAsState(null).value
    LaunchedEffect(uiAction) {
        when (uiAction) {
            null -> {}
            is PlantCheckerViewModel.UiAction.GoToPlantPicker -> {
                backStack.add(PlantPicker(uiAction.query))
            }
        }
    }
    CameraScreen(onEvent = { viewModel.onEvent(it) })
}

@Composable
fun CameraScreen(onEvent: (PlantCheckerViewModel.UiEvent) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var surfaceRequest by remember { mutableStateOf<ViewfinderSurfaceRequest?>(null) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var captureStatus by remember { mutableStateOf("Ready") }
    var transformationInfo by remember { mutableStateOf<TransformationInfo?>(null) }

    val executor = remember { ContextCompat.getMainExecutor(context) }

    LaunchedEffect(Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val cameraProvider = cameraProviderFuture.get()

        // Build Preview use case (for viewfinder)
        val preview = Preview.Builder().build()

        // Build ImageCapture use case (for taking photos)
        val imageCaptureUseCase =
            ImageCapture
                .Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

        imageCapture = imageCaptureUseCase

        // Select back camera
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            // Unbind all use cases before rebinding
            cameraProvider.unbindAll()

            // Bind both Preview and ImageCapture use cases to camera
            val camera =
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCaptureUseCase,
                )

            // Set up the Preview surface provider for the viewfinder
            preview.setSurfaceProvider { request ->
                // Create ViewfinderSurfaceRequest from Preview.SurfaceRequest
                val viewfinderRequest =
                    ViewfinderSurfaceRequest
                        .Builder(request.resolution)
                        .build()

                // Get transformation info from the camera
                val sensorRotationDegrees = camera.cameraInfo.sensorRotationDegrees
                transformationInfo =
                    TransformationInfo(
                        sourceRotation = sensorRotationDegrees,
                        cropRectLeft = 0,
                        cropRectTop = 0,
                        cropRectRight = request.resolution.width,
                        cropRectBottom = request.resolution.height,
                        shouldMirror = cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA,
                    )

                surfaceRequest = viewfinderRequest

                // Provide the surface from viewfinder to preview (using coroutine)
                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                    val surface = viewfinderRequest.getSurface()
                    request.provideSurface(surface, executor) {
                        // Surface is no longer needed
                        viewfinderRequest.markSurfaceSafeToRelease()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("CameraX", "Use case binding failed", e)
            captureStatus = "Error: ${e.message}"
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Display viewfinder
        if (surfaceRequest != null && transformationInfo != null) {
            surfaceRequest?.let { request ->
                Viewfinder(
                    surfaceRequest = request,
                    modifier = Modifier.fillMaxSize(),
                    implementationMode = ImplementationMode.EMBEDDED,
                    transformationInfo = transformationInfo!!,
                )
            }
        }
        // Capture button and status
        Column(
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = captureStatus,
                color = androidx.compose.ui.graphics.Color.White,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp),
            )

            Button(
                onClick = {
                    imageCapture?.let { capture ->
                        captureImage(
                            capture = capture,
                            executor = executor,
                            onSuccess = { savedUri ->
                                onEvent(PlantCheckerViewModel.UiEvent.OnPictureTaken(savedUri))
                                captureStatus = "Image saved: $savedUri"
                            },
                            onError = { exception ->
                                captureStatus = "Capture failed: ${exception.message}"
                            },
                        )
                    } ?: run {
                        captureStatus = "Camera not ready"
                    }
                },
                modifier = Modifier.size(72.dp),
            ) {
                Text("📷", style = MaterialTheme.typography.headlineMedium)
            }
        }
    }
}

private fun captureImage(
    capture: ImageCapture,
    executor: Executor,
    onSuccess: (android.graphics.Bitmap) -> Unit,
    onError: (ImageCaptureException) -> Unit,
) {
    // Capture to memory
    capture.takePicture(
        executor,
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: androidx.camera.core.ImageProxy) {
                // Convert ImageProxy to Bitmap
                val bitmap = imageProxyToBitmap(image)
                image.close()
                onSuccess(bitmap)
                Log.d("CameraX", "Image captured to bitmap: ${bitmap.width}x${bitmap.height}")
            }

            override fun onError(exception: ImageCaptureException) {
                onError(exception)
                Log.e("CameraX", "Image capture failed", exception)
            }
        },
    )
}

private fun imageProxyToBitmap(image: androidx.camera.core.ImageProxy): android.graphics.Bitmap {
    val planeProxy = image.planes[0]
    val buffer = planeProxy.buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    return android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}
