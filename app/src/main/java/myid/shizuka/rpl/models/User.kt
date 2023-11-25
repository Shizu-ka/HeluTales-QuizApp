package myid.shizuka.rpl.models

    class User {
        private var email: String = ""
        private var password: String = ""
        private var isLoggedIn: Boolean = false

        fun getEmail(): String {
            return email
        }

        fun setEmail(email: String) {
            this.email = email
        }

        fun getPassword(): String {
            return password
        }

        fun setPassword(password: String) {
            this.password = password
        }

        fun getIsLoggedIn(): Boolean {
            return isLoggedIn
        }

        fun setIsLoggedIn(isLoggedIn: Boolean) {
            this.isLoggedIn = isLoggedIn
        }
    }