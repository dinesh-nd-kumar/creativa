package com.dineshdk.creativa

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import com.dineshdk.creativa.databinding.FragmentLoginBinding
import com.dineshdk.creativa.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth


class SignUpFragment : Fragment() {

    private lateinit var binding : FragmentSignUpBinding
    private lateinit var fireBaseAuth : FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(layoutInflater,container,false)
        fireBaseAuth = FirebaseAuth.getInstance()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navGraph = Navigation.findNavController(binding.root)

        binding.back.setOnClickListener {
            navGraph.navigate(R.id.action_signUpFragment_to_loginFragment)
        }

        binding.btnSignIn.setOnClickListener {
            navGraph.navigate(R.id.action_signUpFragment_to_loginFragment)
        }

        binding.registerBtn.setOnClickListener {
            val userName = binding.userName.text.toString()
            val email = binding.email.text.toString()
            val pass = binding.editTextPassword.text.toString()
            val confirmPass = binding.editTextTextPasswordConfirm.text.toString()

            if (userName.isNotEmpty() && email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()){

                if (pass == confirmPass){
                    fireBaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener{
                        if (it.isSuccessful){
                            val intent = Intent(context,HomeActivity::class.java)
                            startActivity(intent)
                            activity?.finish()



                        }else{
                            Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }



                }else{
                    Toast.makeText(context, "password mismatch", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(context, "Empty Not Allowed", Toast.LENGTH_SHORT).show()
            }


        }









    }
}