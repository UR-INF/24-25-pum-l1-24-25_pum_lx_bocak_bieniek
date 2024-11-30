package com.focuszone.ui.fragments

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.focuszone.ui.viewmodels.ManageAppLimitViewModel
import com.focuszone.R

class ManageAppLimit : Fragment() {

    companion object {
        fun newInstance() = ManageAppLimit()
    }

    private val viewModel: ManageAppLimitViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_manage_app_limit, container, false)
    }
}