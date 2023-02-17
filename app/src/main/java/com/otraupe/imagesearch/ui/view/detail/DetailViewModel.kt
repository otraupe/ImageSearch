package com.otraupe.imagesearch.ui.view.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.otraupe.imagesearch.data.db.model.imageItem.ImageItem
import com.otraupe.imagesearch.data.db.model.imageItem.ImageItemDbRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val imageItemDbRepository: ImageItemDbRepository
) : ViewModel() {

    private val scope = CoroutineScope(Dispatchers.Default)
    val imageLiveData : MutableLiveData<ImageItem> = MutableLiveData()

    fun getImageData(id: Long) {
        scope.launch {
            imageLiveData.postValue(imageItemDbRepository.getImage(id))
        }
    }
}