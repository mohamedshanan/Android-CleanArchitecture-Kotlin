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

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.masary.anamasary.R
import com.masary.anamasary.core.extension.close
import com.masary.anamasary.core.extension.failure
import com.masary.anamasary.core.extension.viewModel
import com.masary.anamasary.core.platform.BaseFragment
import com.masary.anamasary.domain.exception.CodeVerificationFailure
import com.masary.anamasary.domain.exception.Failure
import com.masary.anamasary.domain.exception.Failure.NetworkConnection
import com.masary.anamasary.domain.exception.Failure.ServerError
import kotlinx.android.synthetic.main.fragment_movie_details.*

class CodeVerificationFragment : BaseFragment() {

    companion object {
        fun newInstance() = CodeVerificationFragment()
    }

    private lateinit var verificationViewModel: VerificationViewModel

    override fun layoutId() = R.layout.fragment_movie_details

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)

        verificationViewModel = viewModel(viewModelFactory) {
            failure(failure, ::handleFailure)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnVerify.setOnClickListener {
            etVerificationCode.text.isNotEmpty().let { verificationViewModel.verifyCode(etVerificationCode.text.toString()) }
        }
    }

    private fun renderKey(keyModel: EncryptionKeyModel?) {
        keyModel?.let {
            activity?.let {
                Toast.makeText(it, keyModel.key, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun handleFailure(failure: Failure?) {
        when (failure) {
            is NetworkConnection -> { notify(R.string.failure_network_connection); close() }
            is ServerError -> { notify(R.string.failure_server_error); close() }
            is CodeVerificationFailure.InvalidVerificationCode -> {
                notify(R.string.failure_invalid_verification_code); close()
            }
        }
    }
}
