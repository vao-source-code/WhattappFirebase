package com.sosolution.socialtelecomunication.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sosolution.socialtelecomunication.R
import com.sosolution.socialtelecomunication.fragments.ChatsFragment
import com.sosolution.socialtelecomunication.fragments.FiltersFragment
import com.sosolution.socialtelecomunication.fragments.HomeFragment
import com.sosolution.socialtelecomunication.fragments.ProfileFragment


lateinit var bottomNavigation: BottomNavigationView

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        inicializarVista()
    }

    private fun inicializarVista() {
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        openFragment(HomeFragment.newInstance("", ""))


    }

    fun openFragment(fragment: Fragment?) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (fragment != null) {
            transaction.replace(R.id.container, fragment)
        }
        transaction.addToBackStack(null)
        transaction.commit()
    }

    var navigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->


        when (item.itemId) {
            R.id.itemHome -> {
                openFragment(HomeFragment.newInstance("", ""))
                return@OnNavigationItemSelectedListener true
            }
            R.id.itemChats-> {
                openFragment(ChatsFragment.newInstance("", ""))
                return@OnNavigationItemSelectedListener true
            }
            R.id.itemFilters -> {
                openFragment(FiltersFragment.newInstance("", ""))
                return@OnNavigationItemSelectedListener true
            }

            R.id.itemProfile -> {
                openFragment(ProfileFragment.newInstance("", ""))
                return@OnNavigationItemSelectedListener true
            }
        }
        true
    }
}