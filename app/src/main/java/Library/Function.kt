package Library

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.text.TextUtils
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class Function {
    fun dateNow(): Double{
        val date = Date().time / 1000
        return date.toDouble()
    }

    fun dateTextFieldFormat(date: Double): String{
        if(date == 0.0 ){
            return ""
        }
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        val dateString = simpleDateFormat.format((date*1000)  )
        return dateString
    }

    fun dateDouble(date: String): Double{
        if(TextUtils.isEmpty(date)) {
            return 0.0
        }
        val format = SimpleDateFormat("dd/MM/yyyy")
        val _date: Date = format.parse(date)
        var datedouble = (_date.time / 1000)
        return datedouble.toDouble()
    }

    fun dateDoublePicker(date: String): Double{
        val format = SimpleDateFormat("dd/MM/yyyy")
        val _date: Date = format.parse(date)
        var datedouble = _date.time //* 1000
        return datedouble.toDouble()
    }

    fun dateDay(date: Double): Int{

        val simpleDateFormat = SimpleDateFormat("dd")
        val dateString = simpleDateFormat.format(date)
        return dateString.toInt()
    }

    fun dateMonth(date: Double): Int{
        val simpleDateFormat = SimpleDateFormat("MM")
        val dateString = simpleDateFormat.format(date)
        return dateString.toInt()
    }

    fun dateYear(date: Double): Int{
        val simpleDateFormat = SimpleDateFormat("yyyy")
        val dateString = simpleDateFormat.format(date)
        return dateString.toInt()
    }

    fun dateAge(date: Double): String{
        val days = dateBetweenDate(date, dateNow())
        var year = 0
        var month = 0
        if ( days > 365 ) {
            year = BigDecimal(days / 365).setScale(0, RoundingMode.DOWN).toInt()
        }
        month = BigDecimal((days - (year*365) ) / 30).setScale(0, RoundingMode.DOWN).toInt()

        val dateString = year.toString()  + "Y " + month.toString() + "M"
        return dateString
    }

    fun dateAgeMonth(date: Double): Int{
        val days = dateBetweenDate(date, dateNow())
        var year = 0
        var month = 0
        if ( days > 365 ) {
            year = BigDecimal(days / 365).setScale(0, RoundingMode.DOWN).toInt()
        }
        month = BigDecimal((days - (year*365) ) / 30).setScale(0, RoundingMode.DOWN).toInt() +
                (year * 12)
        return month
    }


    fun dateFromPicker(year: Int, month: Int, dayOfMonth: Int): String{
        val date = dayOfMonth.toString() + "/" + month.toString() + "/" + year.toString()
        return date
    }

    fun dateBetweenDate(numdatefrom: Double, numdateto: Double): Int {
        if (numdatefrom == 0.0 || numdateto == 0.0)
            return 0

        val ddatefrom: Date
        val ddateto: Date
        val dates = SimpleDateFormat("dd/MM/yyyy")

        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        val sdatefrom = simpleDateFormat.format(numdatefrom*1000)
        ddatefrom = simpleDateFormat.parse(sdatefrom)
        val sdateto = simpleDateFormat.format(numdateto*1000)
        ddateto = simpleDateFormat.parse(sdateto)

        val difference = ddateto.time - ddatefrom.time
        val days = difference / (24 * 60 * 60 * 1000)
        return days.toInt()
    }

    fun dateNowPlusDays(days: Int): Double{
        val date = (Date().time / 1000) + ( 24 * 60 * 60 * days )
        return date.toDouble()
    }

    fun locString(context: Context, resourceName: String): String? {
        try {
            //val packageName = this.getPackageName()
            val resId = context.resources.getIdentifier(resourceName, "string", context.packageName)
            return  context.getString(resId)
        }catch (ex : Exception){

        }
        return null
    }

    fun toDouble(data: String) : Double {
        return if ( data == null || data == "" ){
            0.0
        } else {
            data.toDouble()
        }
    }

    fun StringValue(data: Double) : String {
        return if ( data == null || data == 0.0 ){
            ""
        } else if ( data >= 1000.00 || data <= -1000.00 ) {
            var dnumber = data / 1000
            BigDecimal(dnumber).setScale(0, RoundingMode.HALF_EVEN).toString() + "k"
        } else {
            BigDecimal(data).setScale(0, RoundingMode.HALF_EVEN).toString()
        }
    }

    fun StringValue(data: Int) : String {
        return if ( data == null || data == 0 ){
            ""
        }  else {
            data.toString()
        }
    }

    fun doubleFormat(data: Double) : String {
        return if ( data == null || data == 0.0 ){
            ""
        } else {
            BigDecimal(data).setScale(2, RoundingMode.HALF_EVEN).toString()
        }
    }

    fun toInteger(data: String) : Int {
        return if ( data == null || data == "" ){
            0
        } else {
            data.toInt()
        }
    }
}