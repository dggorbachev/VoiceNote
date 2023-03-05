package com.dggorbachev.voicenotes

import android.os.Bundle
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAuthenticationResult
import dagger.android.support.DaggerAppCompatActivity

class MainActivity : DaggerAppCompatActivity() {
    val authLauncher = VK.login(this) { result: VKAuthenticationResult ->
        when (result) {
            is VKAuthenticationResult.Success -> {
            }
            is VKAuthenticationResult.Failed -> {
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}