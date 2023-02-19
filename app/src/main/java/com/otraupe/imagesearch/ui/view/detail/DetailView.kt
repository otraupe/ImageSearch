package com.otraupe.imagesearch.ui.view.detail

import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.otraupe.imagesearch.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalGlideComposeApi::class)
@Composable
fun DetailView(paddingValues: PaddingValues,
               navController: NavController,
               imageId: String?
) {
    val viewModel: DetailViewModel = hiltViewModel()
    val context = LocalContext.current

    val imageItem by viewModel.imageLiveData.observeAsState()

    if (imageId != null) {
        try {
            val id: Long = imageId.toLong()
            viewModel.getImageData(id)
        } catch (e: NumberFormatException) {
            Toast.makeText(context, R.string.detail_error_image_id_format, Toast.LENGTH_SHORT).show()
            navController.navigateUp()
            return
        }
    } else {
        Toast.makeText(context, R.string.detail_error_image_id_missing, Toast.LENGTH_SHORT).show()
        navController.navigateUp()
        return
    }

    // transformation states for zooming etc.
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var rotation by remember { mutableStateOf(0f) }
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
        offset += offsetChange
        rotation += rotationChange
    }
    var presentResetToast by remember { mutableStateOf(true) }

    LaunchedEffect(scale) {
        if (presentResetToast && scale != 1f) {
            presentResetToast = false
            Toast.makeText(context, R.string.detail_zoom_reset, Toast.LENGTH_SHORT).show()
        }
        if (scale > 10f) scale = 10f
        if (scale < .5f) scale = .5f
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp),
            shape = MaterialTheme.shapes.small,
            elevation = 4.dp,
            backgroundColor = MaterialTheme.colors.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalAlignment = CenterHorizontally
            ) {
                imageItem?.let {item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(16.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val iconSize = 20.dp
                        val fontSize = 16.sp
                        val margin = 6.dp
                        Icon(
                            painter = painterResource(id = R.drawable.ic_like),
                            contentDescription = stringResource(id = R.string.ui_cd_detail_likes),
                            modifier = Modifier
                                .padding(end = margin)
                                .size(iconSize)
                        )
                        Text(
                            text = item.likes.toString(),
                            fontSize = fontSize,
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_download),
                            contentDescription = stringResource(id = R.string.ui_cd_detail_downloads),
                            modifier = Modifier
                                .padding(start = 16.dp, end = margin)
                                .size(iconSize)
                        )
                        Text(
                            text = item.downloads.toString(),
                            fontSize = fontSize
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_comment),
                            contentDescription = stringResource(id = R.string.ui_cd_detail_comments),
                            modifier = Modifier
                                .padding(start = 16.dp, end = margin)
                                .size(iconSize)
                        )
                        Text(
                            text = item.comments.toString(),
                            fontSize = fontSize
                        )
                    }
                    val scope = rememberCoroutineScope()
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RectangleShape)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onDoubleTap = {
                                        if (scale != 1f) {
                                            scope.launch {
                                                state.animateZoomBy(1 / scale)
                                                scale = 1f  // rounding errors can happen
                                            }
                                            offset = Offset.Zero
                                            rotation = 0f
                                        } else {
                                            scope.launch {
                                                state.animateZoomBy(2f)
                                            }
                                        }
                                    },
                                )
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        var iconVisible by rememberSaveable { mutableStateOf(true) }
                        var errorVisible by rememberSaveable { mutableStateOf(false) }
                        if (iconVisible) {
                            Image(
                                modifier = Modifier.size(48.dp),
                                painter = painterResource(id = R.drawable.ic_downloading),
                                contentDescription = stringResource(id = R.string.ui_cd_detail_loading)
                            )
                        }
                        if (errorVisible) {
                            Text(
                                text = stringResource(id = R.string.detail_error_image_load),
                                color = colorResource(id = R.color.medium_grey),
                                modifier = Modifier
                                    .wrapContentHeight()
                                    .wrapContentWidth()
                            )
                        }
                        GlideImage(
                            model = item.largeImageURL,
                            contentDescription = stringResource(R.string.ui_cd_detail_image),
                            modifier = Modifier
                                .fillMaxSize()
                                .transformable(state = state, lockRotationOnZoomPan = true)
                                .graphicsLayer(
                                    scaleX = scale,
                                    scaleY = scale,
                                    rotationZ = rotation,
                                    translationX = offset.x,
                                    translationY = offset.y
                                ),
                        ) {
                            it.apply(RequestOptions().transform(FitCenter()))
                                .listener(object : RequestListener<Drawable> {
                                    override fun onLoadFailed(p0: GlideException?, p1: Any?, p2: Target<Drawable>?, p3: Boolean): Boolean {
                                        iconVisible = false
                                        errorVisible = true
                                        return false
                                    }
                                    override fun onResourceReady(p0: Drawable?, p1: Any?, p2: Target<Drawable>?, p3: DataSource?, p4: Boolean): Boolean {
                                        iconVisible = false
                                        return false
                                    }
                                })
                        }
                    }
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(start = 16.dp, top = 16.dp, end = 16.dp),
                        text = item.user,
                        color = MaterialTheme.colors.primary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(16.dp),
                        text = item.tags,
                        textAlign = TextAlign.End,
                        fontSize = 16.sp
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
                onClick = { navController.navigateUp() },
                contentColor = Color.White,
                backgroundColor = MaterialTheme.colors.primary,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    Icons.Rounded.ArrowBack,
                    contentDescription = stringResource(id = R.string.ui_cd_detail_back),
                )
            }
        }
    }
}


