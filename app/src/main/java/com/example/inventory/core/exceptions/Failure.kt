package com.example.inventory.core.exceptions

sealed class Failure {
    data class DbError(val message: String) : Failure()
    data class BarcodeScannerFailure(
        val message: String
    ) : Failure()
    data object BarcodeScannerCancel : Failure()

    data class MediaFailure(
        val message: String
    ) : Failure()

    data object AssetNotFound : Failure()
    data object InvalidName : Failure()
    data object EmptyInventoryNumber : Failure()
    data object EmptySerialNumber : Failure()
    data object InvalidQuantity : Failure()
    data object InvalidCondition : Failure()
    data object CommentRequired : Failure()
    data object PhotoRequired : Failure()
    data object DuplicateInventoryNumber : Failure()
    data object DuplicateSerialNumber : Failure()

    abstract class FeatureFailure : Failure()

    data class UseCaseResultFailure(
        val result: IUseCaseResultFailure
    ) : Failure()
}

interface IUseCaseResultFailure