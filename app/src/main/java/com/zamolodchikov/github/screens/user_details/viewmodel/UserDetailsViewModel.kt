package com.zamolodchikov.github.screens.user_details.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zamolodchikov.github.domain.repository.UserRepository
import com.zamolodchikov.github.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDetailsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    stateHandle: SavedStateHandle,
) : ViewModel() {

    private val userLogin =
        requireNotNull(stateHandle.get<String>(Screen.UserDetails.KEY_USER_LOGIN))

    private val _effects = Channel<UserDetailsEffect>()
    private val _userStateFlow: MutableStateFlow<UserDetailsState> =
        MutableStateFlow(
            UserDetailsState(
                title = userLogin,
                showProgress = true,
            )
        )

    val effects: Flow<UserDetailsEffect> = _effects.receiveAsFlow()
    val userStateFlow: StateFlow<UserDetailsState> = _userStateFlow.asStateFlow()

    init {
        viewModelScope.launch {
            fetchUserData(userLogin)
        }
    }

    fun onBackClick() {
        viewModelScope.launch {
            _effects.send(UserDetailsEffect.NavigateBack)
        }
    }

    private suspend fun fetchUserData(login: String) {
        val userResult = viewModelScope.async {
            userRepository.fetchUser(login)
        }
        val reposResult = viewModelScope.async {
            userRepository.fetchUsersRepos(login)
        }
        delay(2000)
        try {
            val user = userResult.await()
            val repos = reposResult.await()
            _userStateFlow.update {
                it.copy(
                    user = user,
                    stars = repos.sumOf { it.stars },
                    repos = repos,
                    errorMessage = null,
                    showProgress = false,
                )
            }
        } catch (e: Exception) {
            _userStateFlow.update {
                it.copy(
                    errorMessage = e.message,
                    showProgress = false,
                )
            }
        }
    }
}