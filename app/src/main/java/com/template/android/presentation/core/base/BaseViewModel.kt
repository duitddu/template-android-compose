package com.template.android.presentation.core.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

typealias OnSuccess<T> = (T) -> Unit

abstract class BaseViewModel<ViewState, ViewEvent>(initialState: ViewState) : ViewModel() {
    protected val _viewState: MutableStateFlow<ViewState> = MutableStateFlow(initialState)
    val viewState: StateFlow<ViewState> = _viewState.asStateFlow()

    protected val _viewEvent: MutableSharedFlow<ViewEvent> = MutableSharedFlow()
    val viewEvent: SharedFlow<ViewEvent> = _viewEvent

    private val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        onCoroutineException(coroutineContext, throwable)
    }

    private val viewModelContext = Dispatchers.IO + coroutineExceptionHandler

    abstract fun onCoroutineException(context: CoroutineContext, throwable: Throwable)

    protected fun <T> launch(
        start: CoroutineStart = CoroutineStart.DEFAULT,
        onSuccess: OnSuccess<T>? = null,
        block: suspend CoroutineScope.() -> T
    ): Job = viewModelScope.launch(
        context = viewModelContext,
        start = start
    ) {
        runCatching {
            block.invoke(this)
        }.onSuccess {
            onSuccess?.invoke(it)
        }
    }
}
