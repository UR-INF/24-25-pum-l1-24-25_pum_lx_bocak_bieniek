package com.focuszone.ui.fragments

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.focuszone.ui.viewmodels.ManageSiteViewModel
import com.focuszone.R

class ManageSite : Fragment() {

    companion object {
        fun newInstance() = ManageSite()
    }

    private val viewModel: ManageSiteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_manage_site, container, false)
    }
}