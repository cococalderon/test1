package Models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Client(
    var client_id: String? = "",
    var client_username: String? = "",
    var client_email: String? = "",
    var client_ImageUrl: String? = "",
    var client_profile: String? = "",
    var client_externalaccount: String? = "",
    var client_birthday: Double? = 0.0,
    var client_sex: String? = "",
    var client_phone: String? = "",
    var client_country: String? = "",
    var client_state: String? = "",
    var client_town: String? = "",
    var client_lastmovaccess: Double? = 0.0,
    var client_lastmovver: String? = "",
    var client_movaccess: Int? = 0,
    var client_status: String? = "",
    var client_credate: Double? = 0.0,
    var client_moddate: Double? = 0.0,
    var image: ByteArray? = null
)
