package com.otraupe.imagesearch.ui.view.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.RequestManager
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.otraupe.imagesearch.data.db.model.imageItem.ImageItem
import com.otraupe.imagesearch.R

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterialApi::class)
@Composable
fun ImageItemView(image: ImageItem, glideRequestManager: RequestManager, size: Size, onViewDetails: (String) -> Unit) {
    val defaultPadding = 8
    var openDialog by remember { mutableStateOf(false)  }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(size.height.dp),
        shape = MaterialTheme.shapes.small,
        elevation = 4.dp,
        backgroundColor = MaterialTheme.colors.surface,
        onClick = { openDialog = true }
    ) {
        Row {
            GlideImage(
                model = image.previewURL,
                contentDescription = stringResource(id = R.string.ui_cd_hit_card_preview),
                modifier = Modifier
                    .size(size.height.dp)
            ) {
                it
                    // This thumbnail request exactly matches the request in GlideLazyListPreloader
                    // so that the preloaded image can be used here and display more quickly than
                    // the primary request.
                    .thumbnail(
                        glideRequestManager
                            .asDrawable()
                            .load(image.previewURL)
                            .override((size.height/2).toInt())
                            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(5)))
                            .signature(ObjectKey(System.currentTimeMillis() / (24 * 60 * 60 * 1000)))
                    )
                    .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(20)))
                    .signature(ObjectKey(System.currentTimeMillis() / (24 * 60 * 60 * 1000)))
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((size.height / 2).toInt().dp)
                        .padding(top = defaultPadding.dp, end = (4 + defaultPadding).dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp),
                        text = image.user,
                        color = MaterialTheme.colors.primary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((size.height / 2).toInt().dp)
                        .padding(bottom = defaultPadding.dp, end = (4 + defaultPadding).dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp),
                        text = image.tags,
                        textAlign = TextAlign.End,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
    if (openDialog) {
        AlertDialog(
            onDismissRequest = { openDialog = false },
            title = { Text(text = stringResource(id = R.string.search_detail_dialog_title)) },
            text = { Text(text = stringResource(id = R.string.search_detail_dialog_body)) },
            confirmButton = {
                Button(
                    onClick = {
                        openDialog = false
                        onViewDetails(image.id.toString())
                    },
                    shape = CircleShape,
                    elevation = null
                ) {
                    Text(text = stringResource(id = R.string.search_detail_dialog_confirm_button))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { openDialog = false }
                ) {
                    Text(text = stringResource(id = R.string.search_detail_dialog_dismiss_button))
                }
            }
        )
    }
}