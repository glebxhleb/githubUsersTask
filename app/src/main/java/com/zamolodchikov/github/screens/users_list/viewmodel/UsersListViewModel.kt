@file:OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)

package com.zamolodchikov.github.screens.users_list.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zamolodchikov.github.domain.Loader
import com.zamolodchikov.github.domain.LoaderState
import com.zamolodchikov.github.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsersListViewModel @Inject constructor(
    private val loader: Loader<LoaderState>,
) : ViewModel() {

    private val _effects = Channel<UsersListEffect>()
    private val _trigger = Channel<String>()
    private val _query = MutableStateFlow("")

    val effects: Flow<UsersListEffect> = _effects.receiveAsFlow()

    val state = combine(
        loader.state,
        _query,
    ) { loaderState, query ->
        UsersListState(
            items = loaderState.items,
            isLoading = loaderState.isLoading,
            isError = loaderState.isError,
            errorMessage = loaderState.errorMessage,
            query = query,
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UsersListState())

    init {
        viewModelScope.launch {
            _trigger
                .consumeAsFlow()
                .distinctUntilChanged()
                .debounce(1000)
                .collectLatest {
                    loader.resetAndLoad(it)
                }
        }
    }

    fun updateQuery(query: String) {
        viewModelScope.launch {
            _query.value = query
            _trigger.send(query)
        }
    }

    fun onReachEnd() {
        loader.load()
    }

    fun onUserClick(user: User) {
        viewModelScope.launch {
            _effects.send(UsersListEffect.NavigateToUser(user.login))
        }
    }

    override fun onCleared() {
        loader.clear()
        super.onCleared()
    }
}