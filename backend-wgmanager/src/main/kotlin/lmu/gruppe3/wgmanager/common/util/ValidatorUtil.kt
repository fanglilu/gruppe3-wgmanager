package lmu.gruppe3.wgmanager.common.util

import org.apache.commons.validator.routines.EmailValidator

class ValidatorUtil {
    companion object {
        fun isValidEmail(email: String): Boolean {
            return EmailValidator.getInstance().isValid(email)
        }
    }
}