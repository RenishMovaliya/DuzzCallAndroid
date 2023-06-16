package com.logycraft.duzzcalll.Util

import java.util.regex.Pattern

object ValidationUtils {
    var MobilePattern = "[0-9]{10}"
    private val EMAIL_PATTERN: Pattern = Pattern.compile(
        "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$", Pattern.CASE_INSENSITIVE
    )

    private val PASSWORD_PATTERN: Pattern = Pattern.compile(
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=.!])(?=\\S+$).{8,}$"
    )

    /**
     * Validate email with regular expression
     * @param email email for validation
     * @return true valid email, false invalid email
     */
    fun isValidEmail(email: String?): Boolean {
//        email?.let {
//            return EMAIL_PATTERN.matcher(email).matches()
//        } ?: return false

        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Validate password with regular expression
     * @param password password for validation
     * @return true valid password, false invalid password
     */
    fun isValidPassword(password: String?): Boolean {
        password?.let {
            return PASSWORD_PATTERN.matcher(password).matches()
        } ?: return false
    }
    fun isValidNumber(number: String?): Boolean {
        number?.let {
            return android.util.Patterns.PHONE.matcher(number).matches();
        } ?: return false
    }
    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}