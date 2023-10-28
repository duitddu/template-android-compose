package com.template.android.presentation.core.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

abstract class BaseViewModel<ViewState, ViewEvent>(initialState: ViewState) : ViewModel() {
    protected val _viewState: MutableStateFlow<ViewState> = MutableStateFlow(initialState)
    val viewState: StateFlow<ViewState> = _viewState.asStateFlow()

    protected val _viewEvent: MutableSharedFlow<ViewEvent> = MutableSharedFlow()
    val viewEvent: SharedFlow<ViewEvent> = _viewEvent

    private val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        onCoroutineException(coroutineContext, throwable)
    }

    abstract fun onCoroutineException(context: CoroutineContext, throwable: Throwable)

    protected fun launch(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ): Job = viewModelScope.launch(context, start, block)
}