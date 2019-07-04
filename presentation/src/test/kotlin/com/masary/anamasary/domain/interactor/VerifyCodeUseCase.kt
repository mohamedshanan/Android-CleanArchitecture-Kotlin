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
package com.masary.anamasary.domain.interactor

import com.masary.anamasary.UnitTest
import com.masary.anamasary.domain.functional.Either.Right
import com.masary.anamasary.domain.interactor.verifycode.Key
import com.masary.anamasary.domain.interactor.verifycode.VerifyCode
import com.masary.anamasary.domain.repository.KeyRepository
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock

class VerifyCodeUseCase : UnitTest() {

    private val FAKE_VERIFICATION_CODE = "123456"

    private lateinit var verifyCode: VerifyCode

    @Mock
    private lateinit var keyRepository: KeyRepository

    @Before fun setUp() {
        verifyCode = VerifyCode(keyRepository)
        given { keyRepository.verifyCode(FAKE_VERIFICATION_CODE) }.willReturn(Right(Key.empty()))
    }

    @Test fun `should get data from repository`() {
        runBlocking { verifyCode.run(VerifyCode.Params(FAKE_VERIFICATION_CODE)) }

        verify(keyRepository).verifyCode(FAKE_VERIFICATION_CODE)
        verifyNoMoreInteractions(keyRepository)
    }
}
