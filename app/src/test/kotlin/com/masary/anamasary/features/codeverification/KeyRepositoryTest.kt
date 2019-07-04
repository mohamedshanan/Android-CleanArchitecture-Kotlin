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

import com.masary.anamasary.UnitTest
import com.masary.anamasary.core.exception.Failure.NetworkConnection
import com.masary.anamasary.core.exception.Failure.ServerError
import com.masary.anamasary.core.functional.Either
import com.masary.anamasary.core.functional.Either.Right
import com.masary.anamasary.core.platform.NetworkHandler
import com.masary.anamasary.features.codeverification.KeyRepository.Network
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyZeroInteractions
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import retrofit2.Call
import retrofit2.Response

class KeyRepositoryTest : UnitTest() {

    private lateinit var networkRepository: KeyRepository.Network

    @Mock private lateinit var networkHandler: NetworkHandler
    @Mock
    private lateinit var service: KeysService

    @Mock
    private lateinit var keyCall: Call<KeyEntity>
    @Mock
    private lateinit var keyResponse: Response<KeyEntity>

    @Before fun setUp() {
        networkRepository = Network(networkHandler, service)
    }

    @Test fun `movies service should return network failure when no connection`() {
        given { networkHandler.isConnected }.willReturn(false)

        val encryptionKey = networkRepository.verifyCode("")

        encryptionKey shouldBeInstanceOf Either::class.java
        encryptionKey.isLeft shouldEqual true
        encryptionKey.either({ failure -> failure shouldBeInstanceOf NetworkConnection::class.java }, {})
        verifyZeroInteractions(service)
    }

    @Test fun `movies service should return network failure when undefined connection`() {
        given { networkHandler.isConnected }.willReturn(null)

        val encryptionKey = networkRepository.verifyCode("")

        encryptionKey shouldBeInstanceOf Either::class.java
        encryptionKey.isLeft shouldEqual true
        encryptionKey.either({ failure -> failure shouldBeInstanceOf NetworkConnection::class.java }, {})
        verifyZeroInteractions(service)
    }

    @Test fun `movies service should return server error if no successful response`() {
        given { networkHandler.isConnected }.willReturn(true)

        val encryptionKey = networkRepository.verifyCode("")

        encryptionKey shouldBeInstanceOf Either::class.java
        encryptionKey.isLeft shouldEqual true
        encryptionKey.either({ failure -> failure shouldBeInstanceOf ServerError::class.java }, {})
    }

    @Test fun `movies request should catch exceptions`() {
        given { networkHandler.isConnected }.willReturn(true)

        val encryptionKey = networkRepository.verifyCode("123")

        encryptionKey shouldBeInstanceOf Either::class.java
        encryptionKey.isLeft shouldEqual true
        encryptionKey.either({ failure -> failure shouldBeInstanceOf ServerError::class.java }, {})
    }

    @Test fun `should return empty movie details by default`() {
        given { networkHandler.isConnected }.willReturn(true)
        given { keyResponse.body() }.willReturn(null)
        given { keyResponse.isSuccessful }.willReturn(true)
        given { keyCall.execute() }.willReturn(keyResponse)
        given { service.verifyCode("123") }.willReturn(keyCall)

        val movieDetails = networkRepository.verifyCode("123")

        movieDetails shouldEqual Right(Key.empty())
        verify(service).verifyCode("123")
    }

    @Test fun `should get movie details from service`() {
        given { networkHandler.isConnected }.willReturn(true)
        given { keyResponse.body() }.willReturn(
                KeyEntity("00000"))
        given { keyResponse.isSuccessful }.willReturn(true)
        given { keyCall.execute() }.willReturn(keyResponse)
        given { service.verifyCode("123") }.willReturn(keyCall)

        val movieDetails = networkRepository.verifyCode("123")

        movieDetails shouldEqual Right(Key("00000"))
        verify(service).verifyCode("123")
    }

    @Test fun `movie details service should return network failure when no connection`() {
        given { networkHandler.isConnected }.willReturn(false)

        val movieDetails = networkRepository.verifyCode("123")

        movieDetails shouldBeInstanceOf Either::class.java
        movieDetails.isLeft shouldEqual true
        movieDetails.either({ failure -> failure shouldBeInstanceOf NetworkConnection::class.java }, {})
        verifyZeroInteractions(service)
    }

    @Test fun `movie details service should return network failure when undefined connection`() {
        given { networkHandler.isConnected }.willReturn(null)

        val movieDetails = networkRepository.verifyCode("123")

        movieDetails shouldBeInstanceOf Either::class.java
        movieDetails.isLeft shouldEqual true
        movieDetails.either({ failure -> failure shouldBeInstanceOf NetworkConnection::class.java }, {})
        verifyZeroInteractions(service)
    }

    @Test fun `movie details service should return server error if no successful response`() {
        given { networkHandler.isConnected }.willReturn(true)

        val movieDetails = networkRepository.verifyCode("123")

        movieDetails shouldBeInstanceOf Either::class.java
        movieDetails.isLeft shouldEqual true
        movieDetails.either({ failure -> failure shouldBeInstanceOf ServerError::class.java }, {})
    }

    @Test fun `movie details request should catch exceptions`() {
        given { networkHandler.isConnected }.willReturn(true)

        val movieDetails = networkRepository.verifyCode("123")

        movieDetails shouldBeInstanceOf Either::class.java
        movieDetails.isLeft shouldEqual true
        movieDetails.either({ failure -> failure shouldBeInstanceOf ServerError::class.java }, {})
    }
}