package com.androiddevs.newsflash.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.androiddevs.newsflash.R
import com.androiddevs.newsflash.data.local.shared_preferences.PreferencesHelper
import com.androiddevs.newsflash.databinding.FragmentLoginBinding
import com.androiddevs.newsflash.di.CoreInjector
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import javax.inject.Inject

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    @Inject
    lateinit var preferencesHelper: PreferencesHelper


    private val googleSignInClient by lazy {
        val googleSignInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(requireContext().getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(requireContext(), googleSignInOption)
    }


    operator fun invoke(): Intent {
        return googleSignInClient.signInIntent
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CoreInjector.injector.inject(this)
        sharedElementEnterTransition =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.post {
            binding.bvBlurredView.setView(binding.ivBackground)
        }
        binding.btnGoogleLogin.setOnClickListener {
            preferencesHelper.setLoggedIn()
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
        }
    }
}