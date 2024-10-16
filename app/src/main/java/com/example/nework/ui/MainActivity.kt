package com.example.nework.ui

import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.viewModels
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.nework.R
import com.example.nework.auth.AppAuth
import com.example.nework.databinding.ActivityMainBinding
import com.example.nework.databinding.NavHeaderMainBinding
import com.example.nework.viewmodel.AuthViewModel
import com.example.nework.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.example.nework.utils.loadCircleCrop
import javax.inject.Inject

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navHeaderBinding: NavHeaderMainBinding

    @Inject
    lateinit var appAuth: AppAuth

    private val userViewModel: UserViewModel by viewModels()
    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHeaderBinding = NavHeaderMainBinding.bind(binding.navView.getHeaderView(0))

        setSupportActionBar(binding.appBarMain.toolbar)

        val previousMenuProvider: MenuProvider? = null

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = this.findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_feed,
                R.id.nav_events,
                R.id.nav_my_wall,
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        var showGroup = true

        authViewModel.data.observe(this) {
            setupUser()
        }

        navHeaderBinding.loginMenu.setOnClickListener {
            authViewModel.data.observe(this) {
                showGroup = if (showGroup) {
                    previousMenuProvider?.let(::removeMenuProvider)
                    navView.menu.setGroupVisible(R.id.unauthorized, !authViewModel.authorized)
                    navView.menu.setGroupVisible(R.id.authorized, authViewModel.authorized)
                    false
                } else {
                    previousMenuProvider?.let(::removeMenuProvider)
                    navView.menu.setGroupVisible(R.id.unauthorized, false)
                    navView.menu.setGroupVisible(R.id.authorized, false)
                    true
                }
            }
        }

        onBackPressedDispatcher.addCallback(this) {
            findNavController(R.id.nav_host_fragment_content_main).navigateUp(appBarConfiguration)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun setupUser() {
        userViewModel.getUserSignIn()

        userViewModel.userSignIn.observe(this) { user ->
            with(navHeaderBinding) {
                if (authViewModel.authorized) {
                    if (user.avatar != null) {
                        avatarNavHeader.isVisible = true
                        val urlAvatars = "${user.avatar}"
                        avatarNavHeader.loadCircleCrop(urlAvatars)
                    }

                    nameNavHeader.text = user.name
                    loginNavHeader.text = user.login
                } else {
                    avatarNavHeader.isVisible = false
                    nameNavHeader.text = null
                    loginNavHeader.text = null
                }
            }
        }
    }
}