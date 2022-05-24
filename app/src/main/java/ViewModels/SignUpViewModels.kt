package ViewModels

import Interface.IonClick
import Library.Networks
import Library.Validate
import Library.Validate.Companion.isPasswordValid
import Models.BindableString
import Services.ClientServices
import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.quantusti.clubw.R
import com.quantusti.clubw.SignInActivity
import com.quantusti.clubw.databinding.ActivitySignUpBinding

class SignUpViewModels(activity: Activity,
                       bindingSignUp: ActivitySignUpBinding
) : ViewModel(),
    IonClick {
    private var _activity : Activity? = null
    var fullnameUI = BindableString()
    var emailUI = BindableString()
    var passwordUI = BindableString()

    //Create our firebase authentication variable
    private lateinit var mAuth: FirebaseAuth

    companion object {
        private var _bindingSignUp: ActivitySignUpBinding? = null
    }
    init{
        _activity = activity
        _bindingSignUp = bindingSignUp
        mAuth = FirebaseAuth.getInstance()
    }

    private fun validateFields() : Boolean {
        _bindingSignUp!!.fullnameEditText.error = null
        _bindingSignUp!!.emailEditText.error = null
        _bindingSignUp!!.passwordEditText.error = null

        if(TextUtils.isEmpty(fullnameUI.getValue())){
            _bindingSignUp!!.fullnameEditText.error = _activity!!.getString(R.string.error_field_required)
            _bindingSignUp!!.fullnameEditText.requestFocus()
            return false
        }

        if(TextUtils.isEmpty(emailUI.getValue())){
            _bindingSignUp!!.emailEditText.error = _activity!!.getString(R.string.error_field_required)
            _bindingSignUp!!.emailEditText.requestFocus()
            return false
        } else if (!Validate.isEmail(emailUI.getValue())){
            _bindingSignUp!!.emailEditText.error = _activity!!.getString(R.string.error_invalid_email)
            _bindingSignUp!!.emailEditText.requestFocus()
            return false
        }

        if (TextUtils.isEmpty(passwordUI.getValue())){
            _bindingSignUp!!.passwordEditText.error = _activity!!.getString(R.string.error_field_required)
            _bindingSignUp!!.passwordEditText.requestFocus()
            return false
        } else if (!isPasswordValid(passwordUI.getValue())){
            _bindingSignUp!!.passwordEditText.error = _activity!!.getString(R.string.error_invalid_password)
            _bindingSignUp!!.passwordEditText.requestFocus()
            return false
        }
        return true
    }

    /***************************Actions*********************************/
    override fun onClick(view: View) {
        when(view.id){
            R.id.email_sign_in_button -> signUP()
            R.id.signin_text -> signIn()
        }
    }

    private fun signIn() {
        val intent = Intent(_activity, SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        _activity!!.startActivity(intent)
    }

    private fun signUP() {
        if (validateFields()) if (Networks(_activity!!).verifyNetworks()){

            mAuth!!.createUserWithEmailAndPassword(emailUI.getValue(),passwordUI.getValue())
                .addOnCompleteListener(_activity!!) { task ->

                    if (task.isSuccessful){
                        val user: FirebaseUser?=mAuth.currentUser
                        verifyEmail(user)

                        ClientServices().createClient( user!!.uid,
                            fullnameUI.getValue(),
                            emailUI.getValue(),  _activity!!.getString(R.string.no),
                            _activity!!.getString(R.string.active),
                            _activity!!.getString(R.string.standar) )

                        Toast.makeText(_activity,_activity!!.getString(R.string.success_save),Toast.LENGTH_SHORT).show()

                        val intent = Intent(_activity, SignInActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        _activity!!.startActivity(intent)
                    }else{
                        Snackbar.make(
                            _bindingSignUp!!.passwordEditText,
                            R.string.fail_register, Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
        }else{
            Snackbar.make(
                _bindingSignUp!!.passwordEditText,
                R.string.networks, Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun verifyEmail(user: FirebaseUser?){
        user?.sendEmailVerification()
            ?.addOnCompleteListener(_activity!!){
                    task ->

                if (task.isComplete){
                    Toast.makeText(_activity,"send email",Toast.LENGTH_SHORT)
                } else {
                    Toast.makeText(_activity,"Error on sending email",Toast.LENGTH_SHORT)
                }
            }
    }
}