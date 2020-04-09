package com.fsck.k9.ui.onboarding

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.fsck.k9.activity.K9Activity
import com.fsck.k9.ui.R

/**
 *  最开始欢迎界面
 *
 */


class OnboardingActivity : K9Activity() {   // *** K9Activity()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLayout(R.layout.activity_onboarding)

        initializeActionBar()
    }

    private fun initializeActionBar() {
        val appBarConfiguration = AppBarConfiguration(topLevelDestinationIds = setOf(R.id.welcomeScreen))

        navController = findNavController(R.id.nav_host_fragment)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    companion object {
        @JvmStatic fun launch(activity: Activity) {
            Log.d("debugLog", "message")
            Log.d("debugLog", "message")
            Log.d("debugLog", "message")
            Log.d("debugLog", "message")
            Log.d("debugLog", "message")
            val intent = Intent(activity, OnboardingActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            activity.startActivity(intent)

        }
    }
}
