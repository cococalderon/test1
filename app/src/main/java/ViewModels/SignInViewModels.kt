package ViewModels

import Interface.IonClick
import Library.MemoryData
import Library.Networks
import Library.Ref
import Library.Validate
import Library.Validate.Companion.isPasswordValid
import Models.BindableString
import Models.Client
import Services.ClientServices
import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.quantusti.clubw.ForgotPasswordActivity
import com.quantusti.clubw.MainActivity
import com.quantusti.clubw.R
import com.quantusti.clubw.SignUpActivity
import com.quantusti.clubw.databinding.ActivitySignInBinding

class SignInViewModels(activity: Activity,
                       bindingSignIn: ActivitySignInBinding
) : ViewModel(),
    IonClick {
    private var _activity : Activity? = null
    var emailUI = BindableString()
    var passwordUI = BindableString()

    //Create our firebase authentication variable
    private lateinit var mAuth: FirebaseAuth

    private var memoryData: MemoryData? = null

    companion object {
        private var _bindingSignIn: ActivitySignInBinding? = null
    }
    init{
        _activity = activity
        _bindingSignIn = bindingSignIn
        mAuth = FirebaseAuth.getInstance()
    }

    override fun onClick(view: View) {
        when(view.id){
            R.id.email_sign_in_button -> login()
            R.id.signup_text -> signUp()
            R.id.forgotpassword_text -> forgotPassword()
        }
    }

    private fun validateFields() : Boolean {
        _bindingSignIn!!.emailEditText.error = null
        _bindingSignIn!!.passwordEditText.error = null
        if(TextUtils.isEmpty(emailUI.getValue())){
            _bindingSignIn!!.emailEditText.error = _activity!!.getString(R.string.error_field_required)
            _bindingSignIn!!.emailEditText.requestFocus()
            return false
        } else if (!Validate.isEmail(emailUI.getValue())){
            _bindingSignIn!!.emailEditText.error = _activity!!.getString(R.string.error_invalid_email)
            _bindingSignIn!!.emailEditText.requestFocus()
            return false
        }
        if (TextUtils.isEmpty(passwordUI.getValue())){
            _bindingSignIn!!.passwordEditText.error = _activity!!.getString(R.string.error_field_required)
            return false
        } else if (!isPasswordValid(passwordUI.getValue())){
            _bindingSignIn!!.passwordEditText.error = _activity!!.getString(R.string.error_invalid_password)
            return false
        }
        return true
    }

    /***************************Actions*********************************/
    private fun login(){

        if (validateFields()) if (Networks(_activity!!).verifyNetworks()){

            mAuth!!.signInWithEmailAndPassword(emailUI.getValue(),passwordUI.getValue())
                .addOnCompleteListener(_activity!!) { task ->

                    if (task.isSuccessful){
                        val user: FirebaseUser?=mAuth.currentUser

                        memoryData = MemoryData.getInstance(_activity!!)
                        memoryData!!.saveData("GLOBAL_USER", emailUI.getValue())

                        getGlobalData(user!!.uid)
                    }else{
                        Snackbar.make(
                            _bindingSignIn!!.passwordEditText,
                            R.string.invalid_credentials, Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
        }else{
            Snackbar.make(
                _bindingSignIn!!.passwordEditText,
                R.string.networks, Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun signUp() {
        val intent = Intent(_activity, SignUpActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        _activity!!.startActivity(intent)
    }

    private fun forgotPassword() {
        val intent = Intent(_activity, ForgotPasswordActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        _activity!!.startActivity(intent)
    }

    private fun getGlobalData(uid: String){
        var _client = Client()

        val bdclient = Ref.databaseSpecificClient( uid )
        bdclient.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val client = dataSnapshot.getValue(Client::class.java)
                if (client != null) {
                    _client = client
                    memoryData!!.saveData("GLOBAL_CLIENT_ID", _client.client_id.toString())

                    val movaccess = _client.client_movaccess!! + 1
                    memoryData!!.saveData("GLOBAL_CLIENTMOVACCESS", movaccess.toString())
                    ClientServices().updateClientLastAccess(_client.client_id.toString(), movaccess)

                    val intent = Intent(_activity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    _activity!!.startActivity(intent)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
            }
        })
    }
}