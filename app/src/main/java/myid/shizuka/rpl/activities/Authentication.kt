package myid.shizuka.rpl.activities

abstract class Authentication {
    abstract fun authUser()
    abstract fun redirect(destination: String)
}