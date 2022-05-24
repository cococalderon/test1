package Services

import Library.Field
import Library.Function
import Library.Ref
import Models.Client
import com.google.android.gms.tasks.Task

class ClientServices {

    fun createClient(uid: String, username: String, email: String, externalaccount: String, status: String, profile: String) {

        val bd = Ref.databaseSpecificClient( uid )
        val dict = Client(
            uid,
            username,
            email,
            "",
            profile,
            externalaccount,
            0.0,
            "",
            "",
            "",
            "",
            "",
            0.0,
            Ref.SOFTWARE_VERSION,
            0,
            status,
            Function().dateNow(),
            Function().dateNow()
        )
        bd.setValue(dict).addOnCompleteListener{
                task: Task<Void> ->
            if (task.isSuccessful){
            }
        }
    }

    fun updateClient(uid: String, username: String) {
        val bd = Ref.databaseSpecificClient( uid )
        bd.child(Field.CLIENT_USERNAME).setValue(username)
    }

    fun updateClientLastAccess(uid: String, movaccess: Int) {
        val bd = Ref.databaseSpecificClient( uid )
        bd.child(Field.CLIENT_MOVACCESS).setValue(movaccess)
        bd.child(Field.CLIENT_LASTMOVACCESS).setValue(Function().dateNow())
        bd.child(Field.CLIENT_LASTMOVVER).setValue(Ref.SOFTWARE_VERSION)
    }

    fun updateClientPhoto(uid: String, imageurl: String) {

        val bd = Ref.databaseSpecificClient( uid )
        bd.child(Field.CLIENT_IMAGE_URL).setValue(imageurl)
    }

    fun deleteClient(uid: String) {
        val bd = Ref.databaseSpecificClient( uid ).removeValue()
    }

}