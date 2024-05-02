package com.zamolodchikov.github

import app.cash.turbine.test
import com.zamolodchikov.github.domain.LoaderState
import com.zamolodchikov.github.domain.UsersLoader
import com.zamolodchikov.github.domain.model.User
import com.zamolodchikov.github.domain.repository.UserRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LoaderTest {

    private val validRequest = "qwe"
    private val invalidRequest = "q"
    private val errorRequest = "error"
    private val exception = "Exception"
    private val items1 = listOf(User(id = 1, login = "1", avatarUrl = "a1", type = "t1"))
    private val items2 = listOf(User(id = 2, login = "2", avatarUrl = "a2", type = "t2"))

    private lateinit var repository: UserRepository
    private lateinit var loader: UsersLoader

    @Before
    fun before() = runTest {
        repository = mock(UserRepository::class.java)
        `when`(repository.fetchUsers(validRequest, 0)).thenReturn(items1)
        `when`(repository.fetchUsers(validRequest, 1)).thenReturn(items2)
        `when`(repository.fetchUsers(errorRequest, 0)).thenThrow(RuntimeException(exception))
        loader = UsersLoader(repository)
    }

    @Test
    fun `should load 1 page on resetAndLoad and 2 page on load`() = runTest {
        loader.state.test {
            loader.resetAndLoad(validRequest)

            awaitItem()
            assertEquals(LoaderState(), awaitItem())
            assertEquals(LoaderState(isLoading = true), awaitItem())
            assertEquals(LoaderState(isLoading = false, items = items1), awaitItem())

            loader.load()

            assertEquals(LoaderState(isLoading = true, items = items1), awaitItem())
            assertEquals(
                LoaderState(
                    isLoading = false,
                    items = items1.toMutableList().also { it.addAll(items2) }), awaitItem()
            )
        }
    }

    @Test
    fun `should not load users on resetAndLoad if request is short`() =
        runTest {
            loader.state.test {
                loader.resetAndLoad(invalidRequest)

                awaitItem()
                assertEquals(LoaderState(), awaitItem())
            }
        }

    @Test
    fun `should save error message on resetAndLoad if receive Exception`() =
        runTest {
            loader.state.test {
                awaitItem()

                loader.resetAndLoad(errorRequest)

                assertEquals(LoaderState(), awaitItem())
                assertEquals(LoaderState(isLoading = true), awaitItem())
                assertEquals(
                    LoaderState(
                        isLoading = false,
                        isError = true,
                        errorMessage = exception
                    ), awaitItem()
                )
            }
        }
}