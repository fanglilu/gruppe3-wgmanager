package lmu.gruppe3.wgmanager.common.util

class Generators {
    companion object {
        /**
         * Generate a random string with your own length.
         */
        fun generateRandomString(length: Int): String {
            if (length <= 0) {
                throw Error("Provide a length greater than 0")
            }
            // Descriptive alphabet using three CharRange objects, concatenated
            val alphabet: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

            // Build list from with length defined random samples from the alphabet,
            // and convert it to a string using "" as element separator
            return List(length) { alphabet.random() }.joinToString("")
        }
    }
}