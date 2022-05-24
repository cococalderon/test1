package Library

import android.content.Context
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class Ref {
    companion object {
        const val SOFTWARE_VERSION = "Android 2.0"

        private lateinit var databaseRoot: FirebaseDatabase
        private var _storage: FirebaseStorage? = null
        private var _storageRef: StorageReference? = null

        private const val STORAGE_CLIENT = "client"
        private const val REF_CLIENT = "tb_clients"

        private lateinit var context: Context
        fun setContext(con: Context) {
            context=con
        }

        init{
            databaseRoot = FirebaseDatabase.getInstance()
            _storage = FirebaseStorage.getInstance()
            _storageRef = _storage!!.reference
        }

        /*************************CLIENT*********************************/
        fun databaseClients(): DatabaseReference {
            return this.databaseRoot.reference.child(REF_CLIENT)
        }

        fun databaseSpecificClient(uid: String): DatabaseReference {
            return databaseClients().child(uid)
        }

        /*************************STORAGE*********************************/
        fun storageClient(): StorageReference {
            return _storageRef!!.child(STORAGE_CLIENT)
        }

        fun storageSpecificClient(uid: String): StorageReference {
            return storageClient().child(uid)
        }
    }
}