package myid.shizuka.rpl.models

class Quiz {
    private var id: String = ""
    private var title: String = ""
    private var questions: MutableMap<String, Question> = mutableMapOf()

    fun getId(): String{
        return id
    }

    fun setId(id: String){
        this.id = id
    }

    fun getTitle(): String{
        return title
    }

    fun setTitle(title: String){
        this.title = title
    }

    fun getQuestions(): MutableMap<String, Question> {
        return questions
    }

    fun setQuestions(questions: MutableMap<String, Question> = mutableMapOf()){
        this.questions = questions
    }

//    var id: String = ""
//        get() = field
//        set(value) {
//            field = value
//        }
//
//    var title: String = ""
//        get() = field
//        set(value) {
//            field = value
//        }
//
//    var questions: MutableMap<String, Question> = mutableMapOf()
//        get() = field
//        set(value) {
//            field = value
//        }
}