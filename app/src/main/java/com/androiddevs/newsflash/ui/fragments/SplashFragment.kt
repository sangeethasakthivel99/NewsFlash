package com.androiddevs.newsflash.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.androiddevs.newsflash.R
import kotlinx.android.synthetic.main.fragment_splash.*
import kotlinx.coroutines.delay


class SplashFragment : Fragment(R.layout.fragment_splash) {

    val isLoggedIn = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigateForward()
    }

    private fun navigateForward() {
        lifecycleScope.launchWhenCreated {
            delay(2000)
            if (isLoggedIn) {
            } else {
                iv_logo?.let {
                    val extras = FragmentNavigatorExtras(
                        iv_logo to resources.getString(R.string.transition_name_splash_logo)
                    )
                    findNavController().navigate(
                        R.id.action_splashFragment_to_loginFragment,
                        null,
                        null,
                        extras
                    )
                }

            }
        }
    }
}