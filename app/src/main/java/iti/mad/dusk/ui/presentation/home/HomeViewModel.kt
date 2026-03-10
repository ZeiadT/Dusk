package iti.mad.dusk.ui.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import iti.mad.dusk.core.base.Resource
import iti.mad.dusk.domain.repo.WeatherRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val weatherRepository: WeatherRepository) :
    ViewModel() {
    fun getWeather() {
        val flow = weatherRepository.getCurrentWeather(30.555, 30.555, false)

        viewModelScope.launch {

            flow.collect { resource ->
                when (resource) {
                    is Resource.Success -> Log.d("TAG", "getWeather: ${resource.data}")
                    is Resource.Failure -> Log.d("TAG", "getWeather: ${resource.exception.message}")
                    is Resource.Loading -> Log.d("TAG", "getWeather: $resource")
                }
            }
        }
    }

    fun getForecast() {
        val flow = weatherRepository.getForecast(30.555, 30.555, false)

        viewModelScope.launch {

            flow.collect { resource ->
                when (resource) {
                    is Resource.Success -> Log.d("TAG", "getForecast: ${resource.data}")
                    is Resource.Failure -> Log.d("TAG", "getForecast: ${resource.exception.message}")
                    is Resource.Loading -> Log.d("TAG", "getForecast: $resource")
                }
            }
        }
    }
}