package Services

import Library.Ref
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.UploadTask

class StorageServices {

    fun savePhotoClient(imageData: ByteArray?, uid: String) {

        val imagesRef = Ref.storageSpecificClient( uid )
        val uploadTask = imagesRef.putBytes(imageData!!)

        uploadTask.addOnSuccessListener {
            imagesRef.getDownloadUrl().addOnSuccessListener(OnSuccessListener<Any> { uri ->
                val url = uri.toString()
                ClientServices().updateClientPhoto(uid, url)
            })
        }
    }

    fun deletePhotoClient(uid: String) {
        Ref.storageSpecificClient( uid ).delete()
    }
}