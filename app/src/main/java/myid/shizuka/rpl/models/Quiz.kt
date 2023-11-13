package myid.shizuka.rpl.models

class Quiz {
    var id: String = ""
        get() = field
        set(value) {
            field = value
        }

    var title: String = ""
        get() = field
        set(value) {
            field = value
        }

    var questions: MutableMap<String, Question> = mutableMapOf()
        get() = field
        set(value) {
            field = value
        }
}