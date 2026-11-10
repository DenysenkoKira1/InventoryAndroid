package com.example.inventory.core.functional

import com.example.inventory.core.exceptions.Failure
import com.example.inventory.core.functional.Either.Left
import com.example.inventory.core.functional.Either.Right

/**

 * Represents a value of one of two possible types (a disjoint union).

 * Instances of [Either] are either an instance of [Left] or [Right].

 * FP Convention dictates that [Left] is used for "failure"

 * and [Right] is used for "success".

 *

 * @see Left

 * @see Right

 */

sealed class Either<out L, out R> {
    data class Left<out L>(val a: L) : Either<L, Nothing>()
    data class Right<out R>(val b: R) : Either<Nothing, R>()

    val isLeft get() = this is Left<L>
    val isRight get() = this is Right<R>

    fun <T> either(fnL: (L) -> T, fnR: (R) -> T): T =
        when (this) {
            is Left -> fnL(a)
            is Right -> fnR(b)
        }
}

suspend fun <T, L, R> Either<L, R>.eitherSuspend(
    fnL: suspend (L) -> T,
    fnR: suspend (R) -> T
): T =
    when (this) {
        is Left -> fnL(a)
        is Right -> fnR(b)
    }

fun <T> T.toRight(): Either<Failure, T> = Right(this)

fun <T : Failure> T.toLeft(): Either<Failure, Nothing> = Left(this)


fun Failure.UseCaseResultFailure.toLeft():
        Either<Failure.UseCaseResultFailure, Nothing> = Left(this)

fun <L, R> Either<L, R>.rightOrNull(): R? {
    return when (this) {
        is Left -> null
        is Right -> b
    }
}