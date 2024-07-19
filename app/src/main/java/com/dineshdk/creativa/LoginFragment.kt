package com.dineshdk.creativa

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.Navigation
import com.dineshdk.creativa.databinding.FragmentLoginBinding
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider


class LoginFragment : Fragment() {

    private lateinit var binding : FragmentLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    lateinit var callbackManager:CallbackManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(layoutInflater,container,false)
        firebaseAuth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navGraph = Navigation.findNavController(view)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(),gso)

        binding.btnSignInGoogle.setOnClickListener {
            signInGoogle()
        }

        binding.btnSignInFacebook.setOnClickListener {
            signInFacebook()
        }


        binding.createAccount.setOnClickListener {
            navGraph.navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        binding.signinBtn.setOnClickListener {
            signIn()
   }

  }
    private fun signIn(){
        val email = binding.emailAsUserName.text.toString()
        val pass = binding.password.text.toString()

        if (email.isNotEmpty() && pass.isNotEmpty() ){

            firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener{
                if (it.isSuccessful){
                    val intent = Intent(context,HomeActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }else{
                    Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }

        }else{
            Toast.makeText(context, "Empty Not Allowed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signInFacebook() {

        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile"))
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    Log.d("FB LOG", "facebook:onSuccess:$loginResult")
                    handleFacebookAccessToken(loginResult.accessToken)
                }

                override fun onCancel() {
                    Log.d("FB LOG", "facebook:onCancel")
                }

                override fun onError(error: FacebookException) {
                    Log.d("FB LOG", "facebook:onError", error)
                }
            },
        )


    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d("FB LOG", "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    val intent = Intent(context,HomeActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                } else {
                     Toast.makeText(context,
                        "Authentication failed.", Toast.LENGTH_SHORT,).show()
                    updateUI(null)
                }
            }
    }

    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        lancher.launch(signInIntent)
    }
    private val lancher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
        if (result.resultCode == Activity.RESULT_OK ){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResult(task)
        }
    }

    private fun handleResult(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful){
            val account : GoogleSignInAccount? = task.result
            updateUI(account)


        }else{
            Toast.makeText(context, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful){
                val intent = Intent(context,HomeActivity::class.java)
                startActivity(intent)
                activity?.finish()


            }else{
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }


}








