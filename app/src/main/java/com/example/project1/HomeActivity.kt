package com.example.project1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.project1.databinding.ActivityHomeBinding

private const val TAG_HOME = "home_fragment"
private const val TAG_CALENDER = "calender_fragment"
private const val TAG_MY_PAGE = "my_page_fragment"

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFragment(TAG_HOME, HomeFragment().apply {
            arguments = Bundle().apply {
                putString("username", intent.getStringExtra("username"))
            }
        })

        binding.navigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.calenderFragment -> setFragment(TAG_CALENDER, CalenderFragment().apply {
                    arguments = Bundle().apply {
                        putString("username", intent.getStringExtra("username"))
                    }
                })
                R.id.homeFragment -> setFragment(TAG_HOME, HomeFragment().apply {
                    arguments = Bundle().apply {
                        putString("username", intent.getStringExtra("username"))
                    }
                })
                R.id.myPageFragment -> setFragment(TAG_MY_PAGE, MyPageFragment().apply {
                    arguments = Bundle().apply {
                        putString("username", intent.getStringExtra("username"))
                    }
                })
            }
            true
        }
    }

    private fun setFragment(tag: String, fragment: Fragment) {
        val manager: FragmentManager = supportFragmentManager
        val fragTransaction = manager.beginTransaction()

        if (manager.findFragmentByTag(tag) == null) {
            fragTransaction.add(R.id.mainFrameLayout, fragment, tag)
        }

        val calender = manager.findFragmentByTag(TAG_CALENDER)
        val home = manager.findFragmentByTag(TAG_HOME)
        val myPage = manager.findFragmentByTag(TAG_MY_PAGE)

        if (calender != null) {
            fragTransaction.hide(calender)
        }

        if (home != null) {
            fragTransaction.hide(home)
        }

        if (myPage != null) {
            fragTransaction.hide(myPage)
        }

        if (tag == TAG_CALENDER) {
            if (calender != null) {
                fragTransaction.show(calender)
            }
        } else if (tag == TAG_HOME) {
            if (home != null) {
                fragTransaction.show(home)
            }
        } else if (tag == TAG_MY_PAGE) {
            if (myPage != null){
                fragTransaction.show(myPage)
            }
        }

        fragTransaction.commitAllowingStateLoss()
    }
}