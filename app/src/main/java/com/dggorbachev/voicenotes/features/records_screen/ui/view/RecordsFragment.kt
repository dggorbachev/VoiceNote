package com.dggorbachev.voicenotes.features.records_screen.ui.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.dggorbachev.voicenotes.MainActivity
import com.dggorbachev.voicenotes.R
import com.dggorbachev.voicenotes.base.BaseFragment
import com.dggorbachev.voicenotes.base.HostSelectionInterceptor
import com.dggorbachev.voicenotes.base.LambdaFactory
import com.dggorbachev.voicenotes.databinding.FragmentRecordsBinding
import com.dggorbachev.voicenotes.features.records_screen.stateholders.NotesViewModel
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.api.sdk.auth.VKScope
import com.vk.sdk.api.users.UsersService
import com.vk.sdk.api.users.dto.UsersFields
import com.vk.sdk.api.users.dto.UsersUserFull
import kotlinx.coroutines.launch
import javax.inject.Inject


class RecordsFragment : BaseFragment<FragmentRecordsBinding>(FragmentRecordsBinding::inflate) {

    @Inject
    lateinit var factory: NotesViewModel.Factory

    @Inject
    lateinit var hostSelectionInterceptor: HostSelectionInterceptor

    private val viewModel: NotesViewModel by viewModels {
        LambdaFactory(this) { stateHandle ->
            factory.create(
                stateHandle
            )
        }
    }

    private val playerViewController: PlayerViewController by lazy {
        PlayerViewController(
            binding,
            lifecycleScope,
            requireActivity(),
            viewModel,
            hostSelectionInterceptor,
            viewLifecycleOwner
        )
    }

    private val recorderViewController: RecorderViewController by lazy {
        RecorderViewController(requireActivity(), binding, playerViewController)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            bindAppBar()
            bindRefresh()
            playerViewController.bindNotes()
            recorderViewController.setUpRecorderView()
            playerViewController.setUpPlayerView()
        }
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            playerViewController.bindNotes()
            bindUserAuth()
        }
    }

    private fun bindAppBar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.appBarLayout.outlineProvider = null
    }

    private fun bindRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            lifecycleScope.launch {
                playerViewController.bindNotes()
            }
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun bindUserAuth() {
        setUserIcon()

        binding.ivUser.setOnClickListener {
            if (!VK.isLoggedIn())
                (requireActivity() as MainActivity).authLauncher.launch(
                    arrayListOf(
                        VKScope.DOCS
                    )
                )
        }
    }

    private fun setUserIcon() {
        VK.execute(
            UsersService().usersGet(
                listOf(VK.getUserId()), listOf(UsersFields.PHOTO_50)
            ),
            object : VKApiCallback<List<UsersUserFull>> {
                override fun fail(error: Exception) {
                }

                override fun success(result: List<UsersUserFull>) {
                    if (result[0].photo50 != null) {
                        Glide.with(binding.ivUser).load(result[0].photo50)
                            .placeholder(R.drawable.ic_guest)
                            .centerCrop()
                            .into(binding.ivUser)
                    }
                }
            })
    }

    companion object {
        const val PERMISSION_CODE = 96
    }
}