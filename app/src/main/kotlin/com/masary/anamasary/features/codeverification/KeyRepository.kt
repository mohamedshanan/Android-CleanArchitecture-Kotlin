/**
 * Copyright (C) 2018 Fernando Cejas Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.masary.anamasary.features.codeverification

import com.masary.anamasary.core.exception.Failure
import com.masary.anamasary.core.exception.Failure.NetworkConnection
import com.masary.anamasary.core.exception.Failure.ServerError
import com.masary.anamasary.core.functional.Either
import com.masary.anamasary.core.functional.Either.Left
import com.masary.anamasary.core.functional.Either.Right
import com.masary.anamasary.core.platform.NetworkHandler
import retrofit2.Call
import javax.inject.Inject

interface KeyRepository {
    fun verifyCode(movieId: String): Either<Failure, Key>

    class Network
    @Inject constructor(private val networkHandler: NetworkHandler,
                        private val service: KeysService) : KeyRepository {

        override fun verifyCode(verificationCode: String): Either<Failure, Key> {
            return when (networkHandler.isConnected) {
                true -> request(service.verifyCode(verificationCode), { it.toMovieDetails() }, KeyEntity.empty())
                false, null -> Left(NetworkConnection)
            }
        }

        private fun <T, R> request(call: Call<T>, transform: (T) -> R, default: T): Either<Failure, R> {
            return try {
                val response = call.execute()
                when (response.isSuccessful) {
                    false -> Left(ServerError)
                    true -> Right(transform((response.body() ?: default)))
                }
            } catch (exception: Throwable) {
                Left(ServerError)
            }
        }
    }
}
