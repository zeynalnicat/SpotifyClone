package com.example.spotifyclone.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifyclone.db.RoomDB
import com.example.spotifyclone.model.artist.Artist
import com.example.spotifyclone.retrofit.RetrofitInstance
import kotlinx.coroutines.launch

class ChoseArtistViewModel : ViewModel() {
    private val _artists = MutableLiveData<List<Artist>>()
    private val artistApi = RetrofitInstance.artistsApi.value


    val artists: LiveData<List<Artist>>
        get() = _artists


    fun getArtists() {
        viewModelScope.launch {
            val artistIds =
                "06HL4z0CvFAxyc27GXpf02,7vk5e3vY1uw9plTHJAMwjN,6qqNVTkY8uBg9cP3Jd7DAH,0FEJqmeLRzsXj8hgcZaAyB,7dGJo4pcD2V6oG8kP0tJRR,0Y5tJX1MQlPlqiwlOH1tJY,6XyY86QOPPrYVGvF9ch6wz,6eUKZXaKkcviH0Ku9w2n3V,00FQb4jTyendYWaN8pK0wa,77SW9BnxLY8rJ0RciFqkHh,5j4HeCoUlzhfWtjAfM1acR,64d1rUxfizSAOE9UbMnUZd,5pKCCKE2ajJHZ9KAiaK11H,5ZsFI1h6hIdQRw2ti0hz81,7n2wHs1TKAczGzO7Dd2rGr,5WUlDfRSoLAfcVSX1WnrxN,15UsOTVnJzReFVN1VCnxy4,53XhwfbYqKCa1cC15pYq2q"
            val response = artistApi.getArtists(artistIds)
            if (response.isSuccessful) {
                _artists.postValue(response.body()?.artists)
            }
        }
    }

    fun search(text: String) {
        viewModelScope.launch {
            val response = artistApi.searchArtist(text)
            if (response.isSuccessful) {
                _artists.postValue(response.body()?.artists?.items)
            }
        }
    }


}