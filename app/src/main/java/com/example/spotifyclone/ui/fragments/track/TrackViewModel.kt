package com.example.spotifyclone.ui.fragments.track

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifyclone.network.db.RoomDB
import com.example.spotifyclone.network.db.likedsongs.LikedSongsEntity
import com.example.spotifyclone.resource.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TrackViewModel(private val roomDB: RoomDB) : ViewModel() {

    private val _insertion = MutableLiveData<Long>()

    private val _isInDb = MutableLiveData<Boolean>(false)

    private val likedSongsDao = roomDB.likedSongsDao()
    val insertion: LiveData<Long> get() = _insertion
    val isInDb: LiveData<Boolean> get() = _isInDb

    fun insertLikedSongs(name: String, artist: String, img: String, uri: String) {
        check(name)
        if(_isInDb.value==false){
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val likedSongsDao = roomDB.likedSongsDao()
                    val check = likedSongsDao.insert(LikedSongsEntity(0, name, artist, img, uri))
                    _insertion.postValue(check)
                    _isInDb.postValue(true)

                } catch (e: Exception) {
                    _insertion.postValue(-1L)
                }

            }
        }else{
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    likedSongsDao.delete(name)
                    _insertion.postValue(0)
                    _isInDb.postValue(false)
                } catch (e: Exception) {
                    _insertion.postValue(-1L)
                }
            }
        }


    }


    fun check(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val count = likedSongsDao.check(name)
            _isInDb.postValue(count > 0)
        }
    }

}