package com.example.nodez

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class SignInFragment : Fragment(){
    private lateinit var signInButton: SignInButton
    private val RC_SIGN_IN = 9001
    private lateinit var googleSignInClient: com.google.android.gms.auth.api.signin.GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Google Sign-In button
        signInButton = view.findViewById(R.id.sign_in_button)
        signInButton.setOnClickListener { signIn() }

        // Initialize Google Sign-In options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id)) // Replace with your actual client ID
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
    }

    private fun signIn() {
        val signInIntent = GoogleSignIn.getClient(requireContext(), GoogleSignInOptions.DEFAULT_SIGN_IN).signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Handle Google Sign-In result
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }else{
            Toast.makeText(requireContext(), "OnActivity failed!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            // Signed in successfully, show authenticated UI
            val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)
            val userName=account.displayName
            val personEmail = account.email
            val userId = account.id
            if(userName != null){
                val sharedPref = activity?.getSharedPreferences("UserPrefs", AppCompatActivity.MODE_PRIVATE)
                val editor = sharedPref?.edit()
                // Save user details in SharedPreferences
                editor?.putString("USER_ID", userId)
                editor?.putString("USER_NAME", userName)  // Or you can save any other details like user email, etc.
                editor?.putBoolean("IS_LOGGED_IN", true)
                editor?.apply()
            }
            Toast.makeText(requireContext(), "Logged In successfully!", Toast.LENGTH_SHORT).show()
            val intent = Intent(activity, MainActivity::class.java) // Replace with your target activity
            intent.putExtra("USER_NAME", userName)  // Pass additional info if needed
            intent.putExtra("USER_EMAIL", personEmail)
            startActivity(intent)
            activity?.finish()

        } catch (e: ApiException) {
            // Sign-in failed
//            updateUI(null)
            Toast.makeText(requireContext(), "Sign in failed!", Toast.LENGTH_SHORT).show()

        }
    }
}