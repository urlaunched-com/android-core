package com.urlaunched.android.logger.aspect

import androidx.paging.PagingData
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.urlaunched.android.common.pagination.PagingFlowWithMeta
import com.urlaunched.android.common.response.Response
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import kotlin.coroutines.Continuation

@Aspect
class KotlinAspect {
    @Before(
        "execution(public * *..*ViewModel.*(..)) && " + "!execution(* *..*ViewModel.access$*(..)) && " + "!execution(* *..*ViewModel.getUiState(..)) && " + "!execution(* *..*ViewModel.getSideEffect(..)) && " + "!execution(* *..*ViewModel.*showSnackbar*(..)) && " + "!execution(* *..*ViewModel.*showSnackBar*(..)) && " + "!execution(* *..*ViewModel.*onPassword*(..)) && !@annotation(com.urlaunched.android.logger.annotations.NotLoggable)"
    )
    fun log(joinPoint: JoinPoint?) {
        val methodName = joinPoint?.signature?.name
        val paramValues = joinPoint?.args?.map { it?.toString() }?.toList()

        val methodWithParams = "$methodName(${
            paramValues?.mapIndexed { index, arg -> "param${index + 1} = $arg" }
                ?.filter { it.contains("Continuation").not() }
                ?.joinToString(", ")
        })"

        Firebase.crashlytics.log(methodWithParams)
    }

    @Around("execution(public * *..*UseCase.*(..)) && !@annotation(com.urlaunched.android.logger.annotations.NotLoggable)")
    fun invokeUseCase(joinPoint: ProceedingJoinPoint?): Any? {
        val methodName = joinPoint?.signature?.name
        val args = joinPoint?.args

        val methodWithParams = "$methodName(${
            args?.mapIndexed { index, arg -> "param${index + 1} = $arg" }
                ?.filter { it.contains("Continuation").not() }
                ?.joinToString(", ")
        })"

        val originalContinuation = args?.firstOrNull { it is Continuation<*> }

        if (originalContinuation != null) {
            (originalContinuation as? Continuation<Any?>)?.let { continuation ->
                val wrappedContinuation = object : Continuation<Any?> {
                    override val context = continuation.context

                    override fun resumeWith(result: Result<Any?>) {
                        result.fold(
                            onSuccess = { continuationResult ->
                                when (continuationResult) {
                                    is Response.Success<*> -> {
                                        Firebase.crashlytics.log(
                                            "$methodWithParams ${
                                                SUCCESS.format(
                                                    continuationResult.toString().substringAfter("Success(data=")
                                                        .substringBeforeLast(")")
                                                )
                                            }"
                                        )
                                    }

                                    is Response.Error<*> -> {
                                        Firebase.crashlytics.log(
                                            "$methodWithParams ${ERROR.format(continuationResult.error)}"
                                        )
                                    }

                                    else -> {
                                        // Do nothing
                                    }
                                }
                            },
                            onFailure = { _ ->
                                // Do nothing
                            }
                        )

                        continuation.resumeWith(result)
                    }
                }
                args[args.indexOf(originalContinuation)] = wrappedContinuation
            }

            return joinPoint?.proceed(args)
        } else {
            val result = joinPoint?.proceed(args)

            when (result) {
                is Flow<*> -> {
                    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
                    scope.launch {
                        try {
                            result.first()?.let { data ->
                                if (data is PagingData<*>) {
                                    Firebase.crashlytics.log(methodWithParams)
                                }
                            }
                        } finally {
                            scope.cancel()
                        }
                    }

                    scope.coroutineContext[Job]?.invokeOnCompletion {
                        scope.cancel()
                    }
                }

                is PagingFlowWithMeta<*, *> -> {
                    Firebase.crashlytics.log(methodWithParams)
                }
            }

            return result
        }
    }
}

const val SUCCESS = "Success: %s"
const val ERROR = "Error: %s"