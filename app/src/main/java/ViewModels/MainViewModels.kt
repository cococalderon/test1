package ViewModels

import Interface.IonClick
import Library.*
import Models.BindableString
import Models.Client
import Services.ClientServices
import Services.StorageServices
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.quantusti.clubw.R
import com.quantusti.clubw.SignInActivity
import com.quantusti.clubw.databinding.ActivityMainBinding

class MainViewModels(activity: Activity, binding: ActivityMainBinding
) : ViewModel(),
    IonClick {
    private var _activity : Activity? = null
    private var memoryData: MemoryData? = null

    private var _permissions: Permissions? = null
    private var _multimedia: Multimedia? = null
    private val REQUEST_CODE_CROP_IMAGE = 1
    private val REQUEST_CODE_TAKE_PHOTO = 0
    private val REQUEST_CODE_PHOTO = 2
    private val REQUEST_CODE_GALLERY = 3
    private val TEMP_PHOTO_FILE = "temporary_img100.png"
    private var imageUri: Uri? = null
    private var isNewPhoto = false

    var emailUI = BindableString()
    var fullnameUI = BindableString()
    var _client = Client()

    //Create our firebase authentication variable
    private lateinit var mAuth: FirebaseAuth

    companion object {
        private var _binding: ActivityMainBinding? = null
    }
    init{
        _activity = activity
        _binding = binding
        startFirebase()
        observeClient(memoryData!!.getData("GLOBAL_CLIENT_ID").toString())
    }

    fun startFirebase(){
        _permissions = Permissions(_activity!!)
        _multimedia = Multimedia(_activity!!)
        memoryData = MemoryData.getInstance(_activity!!)
        mAuth = FirebaseAuth.getInstance()
    }

    /***************************observe DB*********************************/
    private fun observeClient(uid: String) {
        if (Networks(_activity!!).verifyNetworks()) {
            val bdclient = Ref.databaseSpecificClient(uid)
            bdclient.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val client  = dataSnapshot.getValue(Client::class.java)
                    if (client  != null) {
                        _client  = client
                        setData()
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                }
            })
        }else{
            Snackbar.make(
                _binding!!.buttonLogout,
                R.string.networks, Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun setData(){
        val MEGABYTE: Int = 1024 * 1024 * 12
        val imagesRef = Ref.storageSpecificClient(_client!!.client_id.toString())
        imagesRef.getBytes(MEGABYTE.toLong()).addOnSuccessListener { bytes ->
            val _selectedImage: Bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            _binding!!.userImageview.setImageBitmap(_selectedImage)
            _binding!!.userImageview.scaleType = ImageView.ScaleType.CENTER_CROP
        }

        _binding!!.fullnameEditText.setText(_client!!.client_username!!)
        _binding!!.emailEditText.setText(_client!!.client_email!!)
        _binding!!.emailTextInput.isEnabled = false
    }

    private fun validateFields() : Boolean {
        _binding!!.fullnameEditText.error = null
        _binding!!.emailEditText.error = null

        if(TextUtils.isEmpty(fullnameUI.getValue())){
            _binding!!.fullnameEditText.error = _activity!!.getString(R.string.error_field_required)
            _binding!!.fullnameEditText.requestFocus()
            return false
        }

        if(TextUtils.isEmpty(emailUI.getValue())){
            _binding!!.emailEditText.error = _activity!!.getString(R.string.error_field_required)
            _binding!!.emailEditText.requestFocus()
            return false
        } else if (!Validate.isEmail(emailUI.getValue())){
            _binding!!.emailEditText.error = _activity!!.getString(R.string.error_invalid_email)
            _binding!!.emailEditText.requestFocus()
            return false
        }

        if (Networks(_activity!!).verifyNetworks()){
        }else{
            Snackbar.make(
                _binding!!.emailEditText,
                R.string.networks, Snackbar.LENGTH_LONG
            ).show()
            return false
        }
        return true
    }

    private fun Save(){
        if (!validateFields()) { return }
        val uid = memoryData!!.getData("GLOBAL_CLIENT_ID").toString()

        if(isNewPhoto) {
            val imageData: ByteArray? = _multimedia?.ImageByte(_binding!!.userImageview)
            StorageServices().savePhotoClient(imageData, _client!!.client_id!!)
        }
        ClientServices().updateClient(uid, fullnameUI.getValue())
        Toast.makeText(_activity,_activity!!.getString(R.string.success_save),Toast.LENGTH_SHORT).show()
    }

    /***************************Actions*********************************/
    override fun onClick(view: View) {
        when (view.id){
            R.id.buttonSave -> Save()
            R.id.buttonLogout -> Logout()
            R.id.user_imageview -> if(_permissions!!.STORAGE()){
                _multimedia!!.getImage(3)
            }
        }
    }

    fun Logout(){
        FirebaseAuth.getInstance().signOut()
        memoryData!!.saveData("GLOBAL_CLIENT_ID","")
        _activity!!.startActivity(
            Intent(_activity, SignInActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    /***************************Photo*********************************/
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        if (resultCode === Activity.RESULT_OK) {
            when (requestCode){
                REQUEST_CODE_CROP_IMAGE -> {

                    var imageCrop: Bitmap? = data?.extras?.get("data") as Bitmap?
                    if(imageCrop == null) {
                        val filePath: String = _activity!!.getExternalFilesDir(null)!!
                            .absolutePath + "/" + TEMP_PHOTO_FILE
                        imageCrop = BitmapFactory.decodeFile(filePath)
                    }

                    imageCrop = BitmapFactory.decodeFile(data?.data.toString())
                    _binding!!.userImageview.setImageBitmap(imageCrop)
                    _binding!!.userImageview.scaleType = ImageView.ScaleType.CENTER_CROP
                }

                REQUEST_CODE_PHOTO -> {
                    if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PHOTO && data != null){
                        _binding!!.userImageview.setImageBitmap(data.extras?.get("data") as Bitmap)
                        isNewPhoto = true
                    }
                }

                REQUEST_CODE_GALLERY -> {
                    if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_GALLERY && data != null){
                        imageUri = data.data
                        _binding!!.userImageview.setImageURI(imageUri)
                        isNewPhoto = true
                    }
                }
            }
        }
    }
}