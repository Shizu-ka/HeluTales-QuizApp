package myid.shizuka.rpl.adapters

abstract class Authentication {
    abstract fun authUser()
    abstract fun redirect(destination: String)
}