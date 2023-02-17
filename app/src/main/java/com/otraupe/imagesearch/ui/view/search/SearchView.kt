package com.otraupe.imagesearch.ui.view.search

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.view.KeyEvent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideLazyListPreloader
import com.bumptech.glide.signature.ObjectKey
import com.otraupe.imagesearch.data.db.model.imageItem.ImageItem
import com.otraupe.imagesearch.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SearchView(paddingValues: PaddingValues,    // bottom padding changes if bottomBar is included,
                                                // the rest are zero atm; useful for positioning scrollable views
               onNavigateToDetails: (String) -> Unit,
) {
    val viewModel: SearchViewModel = hiltViewModel()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val uiScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    val uiState by viewModel.imageListLiveData.observeAsState()

    viewModel.currentLocale = context.resources.configuration.locales.get(0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp)
    ) {
        Row {
            Text(
                modifier = Modifier
                    .align(Alignment.Bottom)
                    .wrapContentWidth()
                    .padding(start = 16.dp, bottom = 16.dp),
                text = stringResource(id = R.string.ui_powered_by)
            )
            IconButton(
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(16.dp)
                    .weight(1f),
                onClick = {
                    try {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(viewModel.providerHomeUrl))
                        startActivity(context, browserIntent, null)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(context, context.getText(R.string.external_no_browser_found), Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_pixbay_logo),
                    contentDescription = stringResource(id = R.string.ui_cd_logo)
                )
            }
        }
        Row {
            var searchTerm by rememberSaveable { mutableStateOf(viewModel.currentSearchTerm) }
            val focusRequester = FocusRequester()

            TextField(
                value = searchTerm,
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.search_terms),
                        fontSize = 20.sp
                    )
                              },
                textStyle = TextStyle.Default.copy(fontSize = 20.sp),
                onValueChange = { if (!it.contains("\n")) searchTerm = it },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        when (searchTerm) {
                            "" -> Toast.makeText(context,R.string.search_please_enter_terms,Toast.LENGTH_SHORT).show()
                            else -> {
                                uiState?.userMessageSent = false
                                focusManager.clearFocus()
                                viewModel.findImages(searchTerm)
                            }
                        }
                    },
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .focusRequester(focusRequester)
                    .onKeyEvent {
                        if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                            when (searchTerm) {
                                "" -> Toast.makeText(context, R.string.search_please_enter_terms, Toast.LENGTH_SHORT).show()
                                else -> {
                                    uiState?.userMessageSent = false
                                    focusManager.clearFocus()
                                    viewModel.findImages(searchTerm)
                                }
                            }
                            true
                        } else {
                            false
                        }
                    },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = stringResource(id = R.string.ui_cd_search_icon),
                        modifier = Modifier
                            .padding(16.dp)
                            .size(24.dp)
                    )
                },
                trailingIcon = {
                    if (searchTerm != "") {
                        IconButton(
                            onClick = {
                                searchTerm = ""
                                focusRequester.requestFocus()
                            }
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = stringResource(id = R.string.ui_cd_search_clear_icon),
                                modifier = Modifier
                                    .padding(16.dp)
                                    .size(24.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = CircleShape,
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.White,
                    cursorColor = Color.White,
                    leadingIconColor = Color.White,
                    trailingIconColor = Color.White,
                    backgroundColor = MaterialTheme.colors.primary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )
        }
        val listState = rememberLazyListState()
        val requestManager = Glide.with(context)
        val thumbnailSize = Size(100f,100f)

        GlideLazyListPreloader(     // TODO: will sometimes throw IndexOutOfBoundsException
            state = listState,
            data = uiState!!.images,
            size = thumbnailSize,
            numberOfItemsToPreload = 15,
            fixedVisibleItemCount = 6,
        ) { item: ImageItem, requestBuilder ->
            requestBuilder
                .load(item.previewURL)
                .signature(ObjectKey(System.currentTimeMillis() / (24 * 60 * 60 * 1000)))
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = paddingValues.calculateBottomPadding()),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            state = listState
        ) {
            val images = uiState?.images
            images?.let {
                if (it.isNotEmpty()) {
                    items(it) { hit ->
                        ImageItemView(image = hit, glideRequestManager = requestManager, size = thumbnailSize, onNavigateToDetails)
                    }
                }
            }
        }

        LaunchedEffect(uiState) {
            if (uiState?.state == SearchUiState.State.IMAGES_FOUND && viewModel.resetScrollPosition) {
                viewModel.resetScrollPosition = false
                listState.scrollToItem(index = 0)   // reset scroll position on new items
            }
        }

        LaunchedEffect(uiState?.state) {
            if (uiState?.userMessageSent == true) {
                return@LaunchedEffect
            }
            val messageId = when (uiState?.state) {
                SearchUiState.State.ERROR_UNKNOWN -> {
                    uiState?.userMessageSent = true
                    R.string.search_error_api_connection
                }
                SearchUiState.State.NONE_FOUND -> {
                    uiState?.userMessageSent = true
                    R.string.search_no_item_found
                }
                SearchUiState.State.NO_MORE_FOUND -> {
                    uiState?.userMessageSent = true
                    R.string.search_no_more_items_found
                }
                else -> null
            }
            if (messageId != null) {
                uiScope.launch {
                    Toast.makeText(context, messageId, Toast.LENGTH_SHORT).show()
                }
            }
        }

        LaunchedEffect(listState) {
            snapshotFlow { listState.firstVisibleItemIndex }
                .map { index -> index > viewModel.scrollThreshold }
                .distinctUntilChanged()
                .filter { it }
                .collect {
                    if (uiState?.state != SearchUiState.State.NO_MORE_FOUND) {
                        Timber.d("Scrolled past ${viewModel.scrollThreshold}th item")
                        viewModel.findMoreImages()
                    }
                }
        }
    }
}