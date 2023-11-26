package com.example.project1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.project1.databinding.FragmentCalenderBinding

class CalenderFragment : Fragment() {
    private var mBinding: FragmentCalenderBinding? = null
    private val binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentCalenderBinding.inflate(inflater, container, false)

        return binding.root
    }
}