package com.focuszone.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.focuszone.R
import com.focuszone.domain.UserAuthManager

class WelcomeFragment : Fragment(R.layout.fragment_welcome) {

    private lateinit var userAuthManager: UserAuthManager

    private fun showErrorDialog(message: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userAuthManager = UserAuthManager(requireContext())

        val editPinLogin = view.findViewById<EditText>(R.id.editTextNumber)
        val bttnLogin = view.findViewById<Button>(R.id.bttnLogin)

        bttnLogin.setOnClickListener {
            val inputPin = editPinLogin.text.toString()
            if (userAuthManager.authenticateUser(inputPin)) {
                findNavController().navigate(R.id.homeFragment)
            } else {
                showErrorDialog("Invalid PIN. Try again.")
            }
        }
    }
}
