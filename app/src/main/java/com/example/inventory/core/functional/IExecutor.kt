package com.example.inventory.core.functional

import kotlinx.coroutines.flow.Flow

interface IExecutor {
    interface Unit {
        suspend operator fun invoke()
    }

    interface InOut<T, R> {
        suspend operator fun invoke(params: T): R
    }

    interface InOutSuspend<T, R> {
        suspend operator fun invoke(params: T): R
    }

    interface Out<R> {
        suspend operator fun invoke(): R
    }

    interface In<T> {
        suspend operator fun invoke(params: T)
    }

    interface FlowOut<T> {
        operator fun invoke(): Flow<T>
    }

    interface FlowInOut<T, R> {
        operator fun invoke(params: T): Flow<R>
    }
}