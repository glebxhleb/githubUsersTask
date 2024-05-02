package com.zamolodchikov.github.screens.user_details.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.zamolodchikov.github.domain.model.Repo
import com.zamolodchikov.github.domain.model.User
import com.zamolodchikov.github.screens.user_details.viewmodel.UserDetailsEffect
import com.zamolodchikov.github.screens.user_details.viewmodel.UserDetailsState
import com.zamolodchikov.github.screens.user_details.viewmodel.UserDetailsViewModel
import com.zamolodchikov.github.ui.components.ProgressIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailsScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    viewModel: UserDetailsViewModel = hiltViewModel()
) {
    val state = viewModel.userStateFlow.collectAsStateWithLifecycle().value
    BackHandler(onBack = viewModel::onBackClick)
    LaunchedEffect(Unit) {
        viewModel.effects.collect {
            when (it) {
                is UserDetailsEffect.NavigateBack -> onNavigateBack()
            }
        }
    }
    Scaffold(
        modifier = modifier.testTag("UserDetailsScreen"),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    Icon(
                        Icons.Rounded.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(40.dp)
                            .clickable(onClick = viewModel::onBackClick)
                    )
                },
                title = {
                    Text(
                        modifier = Modifier.padding(start = 20.dp),
                        text = state.title,
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            if (state.errorMessage != null) {
                ErrorContent(state.errorMessage)
            } else {
                Details(state)
            }

            if (state.showProgress) {
                ProgressIndicator()
            }
        }
    }
}

@Composable
private fun Details(state: UserDetailsState, modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())) {
        if (state.user != null) {
            UserInfo(state.user, state.stars)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Repos(state.repos)
    }
}

@Composable
private fun ColumnScope.Repos(repos: List<Repo>) {
    repos.forEach { repo ->
        Text(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            text = repo.name,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary,
        )
    }
}

@Composable
private fun UserInfo(user: User, stars: Int, modifier: Modifier = Modifier) {
    Row(horizontalArrangement = Arrangement.Start) {
        Surface(
            modifier = modifier
                .padding(20.dp)
                .size(100.dp),
            shape = CircleShape,
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondary)
        ) {
            AsyncImage(
                model = user.avatarUrl,
                contentDescription = "user avatar",
            )
        }
        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "type: ${user.type}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.secondary,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row {
                Icon(
                    Icons.Rounded.Star,
                    contentDescription = "Star",
                    modifier = Modifier
                        .size(40.dp)
                )
                Text(
                    text = stars.toString(),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.tertiary,
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun ErrorContent(text: String, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize()) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
        )
    }
}