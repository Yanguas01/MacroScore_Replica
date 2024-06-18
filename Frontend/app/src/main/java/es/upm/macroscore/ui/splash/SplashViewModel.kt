package es.upm.macroscore.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.upm.macroscore.domain.usecase.SaveMyUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val saveMyUserUseCase: SaveMyUserUseCase
) : ViewModel() {

    private val _dataLoadResult = MutableStateFlow(false)
    val dataLoadResult get() : StateFlow<Boolean> = _dataLoadResult

    private val _closeSplashScreen = MutableStateFlow(false)
    val closeSplashScreen get() : StateFlow<Boolean> = _closeSplashScreen

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                saveMyUserUseCase().onSuccess {
                    _closeSplashScreen.update { true }
                    _dataLoadResult.update { true }
                }.onFailure {
                    _closeSplashScreen.update { true }
                    _dataLoadResult.update { false }
                }
            }
        }
    }
}

