package com.masary.anamasary.data.net

import com.masary.anamasary.core.platform.NetworkHandler
import com.masary.anamasary.data.Entity.KeyEntity
import com.masary.anamasary.domain.exception.Failure
import com.masary.anamasary.domain.functional.Either
import com.masary.anamasary.domain.interactor.verifycode.Key
import com.masary.anamasary.domain.repository.KeyRepository
import retrofit2.Call
import javax.inject.Inject

class KeyRepositoryImpl
@Inject constructor(private val networkHandler: NetworkHandler,
                    private val service: KeysService) : KeyRepository {

    override fun verifyCode(verificationCode: String): Either<Failure, Key> {
        return when (networkHandler.isConnected) {
            true -> request(service.verifyCode(verificationCode), { it.toMovieDetails() }, KeyEntity.empty())
            false, null -> Either.Left(Failure.NetworkConnection)
        }
    }

    private fun <T, R> request(call: Call<T>, transform: (T) -> R, default: T): Either<Failure, R> {
        return try {
            val response = call.execute()
            when (response.isSuccessful) {
                false -> Either.Left(Failure.ServerError)
                true -> Either.Right(transform((response.body() ?: default)))
            }
        } catch (exception: Throwable) {
            Either.Left(Failure.ServerError)
        }
    }
}