package Library

import java.util.regex.Matcher
import java.util.regex.Pattern

class Validate {
    companion object{
        var pat: Pattern? = null
        var mat: Matcher? = null

        fun isEmail(email: String) :Boolean{
            pat = Pattern.compile("^[\\w\\-\\_\\+]+(\\.[\\w\\-\\_]+)*@([A-Za-z0-0-]+\\.)+[A-Za-z]{2,4}$")
            mat = pat!!.matcher(email)
            return mat!!.find()
        }

        fun isPasswordValid(password: String) : Boolean {
            return password.length >= 8
        }
    }
}