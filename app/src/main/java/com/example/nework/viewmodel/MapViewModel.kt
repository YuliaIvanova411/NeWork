package com.example.nework.viewmodel

import androidx.lifecycle.ViewModel
import com.example.nework.dto.Coordinates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MapViewModel: ViewModel() {
    private val _coordinates = MutableStateFlow<Coordinates?>(null)
    val coordinates = _coordinates.asStateFlow()

    private val _place = MutableStateFlow<Coordinates?>(null)
    val place = _place.asStateFlow()

    fun setCoordinates(coordinates: Coordinates) {
        _coordinates.value = coordinates
    }

    fun setPlace(coordinates: Coordinates) {
        _place.value = coordinates
    }

    fun clearPlace() {
        _place.value = null
    }
}