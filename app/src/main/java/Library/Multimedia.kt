package Library

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

class Multimedia (private  val _activity: Activity) {
    private var photoURI: Uri? = null
    companion object{
        private const val REQUEST_CODE_CROP_IMAGE = 1
        private const val REQUEST_CODE_PHOTO = 2
        private const val REQUEST_CODE_GALLERY = 3
        private const val TEMP_PHOTO_FILE = "temporary_img100.png"
    }

    fun cropCapturedImage(action: Int) {

        var cropIntent: Intent? = null
        when (action){
            0 -> {
                cropIntent = Intent("com.android.camera.action.CROP")
                cropIntent.setDataAndType(photoURI, "image/*")

                //indicate the limits of our image to cut
                cropIntent.putExtra("outputX", 400)
                cropIntent.putExtra("outputY",400)
            }
            1 -> {
                cropIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                cropIntent.type = "image/*"
                cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, getTempoFile())
                cropIntent.putExtra("outputX", 400)
                cropIntent.putExtra("outputY",400)
            }
        }
        val list = _activity.packageManager.queryIntentActivities(cropIntent!!, 0)
        if( 0 == list.size){
            Toast.makeText(_activity,"Can not find image crop app", Toast.LENGTH_SHORT).show()
        } else {
            //enable the crop in this intent
            cropIntent!!.putExtra("crop", "true")
            cropIntent.putExtra("aspectX", 1)
            cropIntent.putExtra("aspectY", 1)
            cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.PNG)
            cropIntent.putExtra("scale", true)
            //True: will return the image as a bitmap, False: will return the url of the saved image
            cropIntent.putExtra("return-data", true)
            //start our activity and pass a response code
            _activity.startActivityForResult(cropIntent, REQUEST_CODE_CROP_IMAGE)
        }
    }

    fun getImage(action: Int) {

        var cropIntent: Intent? = null
        when (action){
            2 -> {
                cropIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                _activity.startActivityForResult(cropIntent, REQUEST_CODE_PHOTO)
            }
            3 -> {
                cropIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                _activity.startActivityForResult(cropIntent, REQUEST_CODE_GALLERY)
            }
        }
    }

    fun getTempoFile(): Uri?{
        return if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED ){
            val file = File(_activity!!.getExternalFilesDir(null)!!.absolutePath, TEMP_PHOTO_FILE)
            try {
                file.createNewFile()
            } catch (e: IOException){
            }
            Uri.fromFile(file)
        } else {
            null
        }
    }

    fun ImageByte(imageView: ImageView): ByteArray?{
        val bitmap = (imageView
            .drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 40, baos)
        return baos.toByteArray()
    }
}