package com.zamolodchikov.github.domain

import com.zamolodchikov.github.domain.model.User
import com.zamolodchikov.github.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

interface Loader<T> {
    val state: Flow<T>
    fun load()
    fun resetAndLoad(request: String)
    fun clear()
}

class UsersLoader @Inject constructor(
    private val userRepository: UserRepository,
) : Loader<LoaderState> {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val dispatcher = Dispatchers.IO.limitedParallelism(1)
    private var job: Job? = null
    private val scope = CoroutineScope(SupervisorJob())
    private val _state = MutableStateFlow(InnerState())

    override val state = _state.map {
        LoaderState(
            items = it.items,
            isLoading = it.isLoading,
            isError = it.isError,
            errorMessage = if (it.isError) it.errorMessage else null,
        )
    }

    override fun load() {
        job = scope.launch(dispatcher) {
            launchLoad()
        }
    }

    override fun resetAndLoad(request: String) {
        job?.cancel()
        _state.update {
            it.copy(
                items = emptyList(),
                isError = false,
                isLoading = false,
                hasMore = true,
                request = request,
                page = 0
            )
        }
        job = scope.launch(dispatcher) {
            launchLoad()
        }
    }

    override fun clear() {
        scope.cancel()
    }

    private suspend fun launchLoad() {
        if (!_state.value.hasMore
            || _state.value.isLoading
            || _state.value.request.length < 2
        ) return
        _state.update {
            it.copy(
                isLoading = true
            )
        }
        try {
            val newItems =
                userRepository.fetchUsers(_state.value.request, _state.value.page)
            _state.update {
                it.copy(
                    isLoading = false,
                    hasMore = newItems.isNotEmpty(),
                    items = _state.value.items.toMutableList().also { it.addAll(newItems) },
                    page = _state.value.page + 1
                )
            }
        } catch (e: Exception) {
            _state.update {
                it.copy(
                    errorMessage = e.message,
                    isLoading = false,
                    isError = true
                )
            }
        }
    }
}

private data class InnerState(
    val items: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val hasMore: Boolean = true,
    val errorMessage: String? = null,
    val request: String = "",
    val page: Int = 0,
)

data class LoaderState(
    val items: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null,
)