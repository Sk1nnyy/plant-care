package com.skinnyy.plantcare.ui.presentation.plantchecker

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.viewfinder.compose.Viewfinder
import androidx.camera.viewfinder.surface.ImplementationMode
import androidx.camera.viewfinder.surface.TransformationInfo
import androidx.camera.viewfinder.surface.ViewfinderSurfaceRequest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.skinnyy.plantcare.R
import com.skinnyy.plantcare.ui.PlantPicker
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.util.concurrent.Executor

@Composable
fun PlantCheckerScreen(
    backStack: NavBackStack<NavKey>,
    modifier: Modifier = Modifier,
    viewModel: PlantCheckerViewModel = koinViewModel(),
) {
    val uiState = viewModel.uiState.collectAsState().value
    val uiAction = viewModel.uiEvents.collectAsState(null).value
    LaunchedEffect(uiAction) {
        when (uiAction) {
            null -> {}
            is PlantCheckerViewModel.UiAction.GoToPlantPicker -> {
                backStack.add(PlantPicker(uiAction.query))
            }
        }
    }
    CameraScreen(uiState, onEvent = { viewModel.onEvent(it) }, modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    uiState: PlantCheckerViewModel.UiState,
    onEvent: (PlantCheckerViewModel.UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var surfaceRequest by remember { mutableStateOf<ViewfinderSurfaceRequest?>(null) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var transformationInfo by remember { mutableStateOf<TransformationInfo?>(null) }
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    val executor = remember { ContextCompat.getMainExecutor(context) }
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA,
            ) == PackageManager.PERMISSION_GRANTED,
        )
    }
    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) { isGranted ->
            hasCameraPermission = true
            if (!isGranted) {
                Toast
                    .makeText(context, "Notifications permission denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    LaunchedEffect(Unit) {
        launcher.launch(Manifest.permission.CAMERA)
    }

    LaunchedEffect(hasCameraPermission) {
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
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(stringResource(R.string.title_plant_checker))
                },
                navigationIcon = {
                    IconButton(onClick = { onBackPressedDispatcher?.onBackPressed() }) {
                        Icon(painter = painterResource(R.drawable.ic_arrow_back), null)
                    }
                },
            )
        },
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(it),
        ) {
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
            Column(
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                FloatingActionButton(
                    onClick = {
                        imageCapture?.let { capture ->
                            captureImage(
                                capture = capture,
                                executor = executor,
                                onSuccess = { savedUri ->
                                    onEvent(PlantCheckerViewModel.UiEvent.OnPictureTaken(savedUri))
                                },
                                onError = { exception ->
                                    Log.e("CameraX", "Image capture failed", exception)
                                },
                            )
                        } ?: run {
                        }
                    },
                ) {
                    Icon(painterResource(R.drawable.ic_search), contentDescription = null)
                }
            }
            if (uiState.isLoading) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = .7f)),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }

    if (uiState.error) {
        Dialog(onDismissRequest = { onEvent(PlantCheckerViewModel.UiEvent.DismissError) }) {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        stringResource(R.string.title_dialog_no_plant_error),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Button(
                        onClick = { onEvent(PlantCheckerViewModel.UiEvent.DismissError) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(stringResource(R.string.action_try_again))
                    }
                }
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
    capture.takePicture(
        executor,
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: androidx.camera.core.ImageProxy) {
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
