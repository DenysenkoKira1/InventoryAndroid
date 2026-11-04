package com.example.inventory.core.exceptions

sealed class Failure {
    data class DbError(val message: String) : Failure()
    data class BarcodeScannerFailure(
        val message: String
    ) : Failure()
    data object BarcodeScannerCancel : Failure()

    abstract class FeatureFailure : Failure()

    data class UseCaseResultFailure(
        val result: IUseCaseResultFailure
    ) : Failure()
}

interface IUseCaseResultFailure