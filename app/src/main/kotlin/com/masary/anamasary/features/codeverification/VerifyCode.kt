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
import com.masary.anamasary.core.functional.Either
import com.masary.anamasary.core.interactor.UseCase
import com.masary.anamasary.features.codeverification.VerifyCode.Params
import javax.inject.Inject

class VerifyCode
@Inject constructor(private val keyRepository: KeyRepository) : UseCase<Key, Params>() {

    override suspend fun run(params: Params): Either<Failure, Key> = keyRepository.verifyCode(params.verificationCode)

    data class Params(val verificationCode: String)
}
