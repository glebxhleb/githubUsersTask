@file:OptIn(ExperimentalMaterial3Api::class)

package com.zamolodchikov.github.screens.users_list.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zamolodchikov.github.domain.model.User
import com.zamolodchikov.github.screens.users_list.viewmodel.UsersListEffect
import com.zamolodchikov.github.screens.users_list.viewmodel.UsersListState
import com.zamolodchikov.github.screens.users_list.viewmodel.UsersListViewModel
import com.zamolodchikov.github.ui.components.ProgressIndicator
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersListScreen(
    modifier: Modifier = Modifier,
    onNavigateToUser: (String) -> Unit,
    viewModel: UsersListViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.effects.collect {
            when (it) {
                is UsersListEffect.NavigateToUser -> onNavigateToUser(it.login)
            }
        }
    }
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface),
        topBar = {
            TopBar(
                searchQuery = state.value.query,
                updateQuery = viewModel::updateQuery,
            )
        }
    ) { paddingValues ->
        UsersList(
            state = state.value,
            onReachEnd = viewModel::onReachEnd,
            onUserClick = viewModel::onUserClick,
            modifier = Modifier.padding(paddingValues),
        )
    }
}

@Composable
private fun TopBar(
    searchQuery: String,
    updateQuery: (String) -> Unit,
) {
    Box(modifier = Modifier.background(color = MaterialTheme.colorScheme.primary)) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            value = searchQuery,
            onValueChange = updateQuery,
            singleLine = true,
            shape = RoundedCornerShape(4.dp),
            placeholder = {
                Text(text = "Start typing user name")
            },
            leadingIcon = {
                Icon(
                    Icons.Rounded.Search,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    contentDescription = "Search"
                )
            },
            textStyle = MaterialTheme.typography.titleLarge,
            trailingIcon = {
                Icon(
                    Icons.Rounded.Clear,
                    contentDescription = "Clear",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.clickable { updateQuery("") }
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                textColor = MaterialTheme.colorScheme.onPrimaryContainer,
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}

@Composable
private fun UsersList(
    state: UsersListState,
    onUserClick: (User) -> Unit,
    onReachEnd: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    listState: LazyListState = rememberLazyListState(),
) {
    LaunchedEffect(listState) {
        snapshotFlow {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
        }
            .map { index -> index >= listState.layoutInfo.totalItemsCount - 10 }
            .collect {
                onReachEnd()
            }
    }
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = contentPadding,
        state = listState,
    ) {
        items(
            count = state.items.size,
            key = { state.items.getOrNull(it)?.login ?: Random.nextInt().toString() }
        ) { position ->
            state.items.getOrNull(position)?.let { user ->
                UserItem(
                    user = user,
                    onUserClick = onUserClick,
                )
            }
        }
        when {
            state.isLoading -> Loading("append_loading")
            state.isError -> Error(message = state.errorMessage ?: "", key = "append_error")
            state.items.isEmpty() && (state.query.length > 1) -> EmptyResult(
                message = "Empty result",
                key = "append_error"
            )
        }
    }
}

@Composable
private fun UserItem(user: User, onUserClick: (User) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.clickable(role = Role.Button) { onUserClick(user) }) {
        Text(
            modifier = modifier.padding(16.dp),
            text = user.login,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Divider()
    }
}

private fun LazyListScope.Loading(key: String) {
    item(key = key) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            ProgressIndicator()
        }
    }
}

private fun LazyListScope.EmptyResult(
    message: String,
    key: String
) {
    item(key = key) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private fun LazyListScope.Error(
    message: String,
    key: String
) {
    item(key = key) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}