package myid.shizuka.rpl.models

class Question {
    private var description: String = ""
    private var option1: String = ""
    private var option2: String = ""
    private var option3: String = ""
    private var option4: String = ""
    private var answer: String = ""
    private var userAnswer: String = ""
    private var materi: String = ""

    fun getDescription(): String {
        return description
    }

    fun setDescription(description: String){
        this.description = description
    }

    fun getOption1(): String {
        return option1
    }

    fun setOption1(option1: String){
        this.option1 = option1
    }

    fun getOption2(): String {
        return option2
    }

    fun setOption2(option2: String){
        this.option2 = option2
    }

    fun getOption3(): String {
        return option3
    }

    fun setOption3(option3: String){
        this.option3 = option3
    }
    fun getOption4(): String {
        return option4
    }

    fun setOption4(option4: String){
        this.option4 = option4
    }

    fun getAnswer(): String {
        return answer
    }

    fun setAnswer(answer: String){
        this.answer = answer
    }

    fun getUserAnswer(): String {
        return userAnswer
    }

    fun setUserAnswer(userAnswer: String){
        this.userAnswer = userAnswer
    }

    fun getMateri(): String {
        return materi
    }

    fun setMateri(materi: String){
        this.materi = materi
    }

//    var description: String = ""
//        get() = field
//        set(value) {
//            field = value
//        }
//
//    var option1: String = ""
//        get() = field
//        set(value) {
//            field = value
//        }
//
//    var option2: String = ""
//        get() = field
//        set(value) {
//            field = value
//        }
//
//    var option3: String = ""
//        get() = field
//        set(value) {
//            field = value
//        }
//
//    var option4: String = ""
//        get() = field
//        set(value) {
//            field = value
//        }
//
//    var answer: String = ""
//        get() = field
//        set(value) {
//            field = value
//        }
//
//    var userAnswer: String = ""
//        get() = field
//        set(value) {
//            field = value
//        }
//
//    var materi: String = ""
//        get() = field
//        set(value) {
//            field = value
//        }
}