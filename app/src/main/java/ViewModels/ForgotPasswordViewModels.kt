package ViewModels

import Interface.IonClick
import Library.Networks
import Library.Validate
import Models.BindableString
import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.quantusti.clubw.R
import com.quantusti.clubw.SignInActivity
import com.quantusti.clubw.databinding.ActivityForgotPasswordBinding

class ForgotPasswordViewModels(activity: Activity,
                               bindingForgotPassword: ActivityForgotPasswordBinding
) : ViewModel(),
    IonClick {
    private var _activity : Activity? = null
    var emailUI = BindableString()

    //create our firebase authentication variable
    private lateinit var mAuth: FirebaseAuth

    companion object {
        private var _bindingForgotPassword: ActivityForgotPasswordBinding? = null
    }
    init{
        _activity = activity
        _bindingForgotPassword = bindingForgotPassword
        mAuth = FirebaseAuth.getInstance()
    }

    override fun onClick(view: View) {
        when(view.id){
            R.id.forgot_password_in_button -> forgotPassword()
            R.id.signin_text -> signIn()
        }
    }

    private fun validateFields() : Boolean {
        _bindingForgotPassword!!.emailEditText.error = null

        if(TextUtils.isEmpty(emailUI.getValue())){
            _bindingForgotPassword!!.emailEditText.error = _activity!!.getString(R.string.error_field_required)
            _bindingForgotPassword!!.emailEditText.requestFocus()
            return false
        } else if (!Validate.isEmail(emailUI.getValue())){
            _bindingForgotPassword!!.emailEditText.error = _activity!!.getString(R.string.error_invalid_email)
            _bindingForgotPassword!!.emailEditText.requestFocus()
            return false
        }

        return true
    }

    /***************************Actions*********************************/
    private fun signIn() {
        val intent = Intent(_activity, SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        _activity!!.startActivity(intent)
    }

    private fun forgotPassword() {
        if (validateFields()) if (Networks(_activity!!).verifyNetworks()){

            mAuth!!.sendPasswordResetEmail(emailUI.getValue())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful){

                        Toast.makeText(_activity,_activity!!.getString(R.string.success_email_reset),
                            Toast.LENGTH_SHORT).show()

                        val intent = Intent(_activity, SignInActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        _activity!!.startActivity(intent)
                    }else{
                        Snackbar.make(
                            _bindingForgotPassword!!.emailEditText,
                            R.string.invalid_credentials, Snackbar.LENGTH_LONG
                        ).show()
                    }
                }

        }else{
            Snackbar.make(
                _bindingForgotPassword!!.emailEditText,
                R.string.networks, Snackbar.LENGTH_LONG
            ).show()
        }
    }
}