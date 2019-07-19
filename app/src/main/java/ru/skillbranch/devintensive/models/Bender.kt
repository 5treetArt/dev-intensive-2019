package ru.skillbranch.devintensive.models

class Bender(var status:Status = Status.NORMAL, var question: Question = Question.NAME) {


    fun askQuestion(): String = when(question) {
        Question.NAME -> Question.NAME.question
        Question.PROFESSION -> Question.PROFESSION.question
        Question.MATERIAL -> Question.MATERIAL.question
        Question.BDAY -> Question.BDAY.question
        Question.SERIAL -> Question.SERIAL.question
        Question.IDLE -> Question.IDLE.question
    }

    fun listenAnswer(answer:String) : Pair<String, Triple<Int, Int, Int>> {
        val error = question.validate(answer)
        if (error != null)
            return error + "\n${question.question}" to status.color

        //TODO говнокод
        if (question == Question.IDLE)
            return question.question to status.color

        return if (question.answers.contains(answer.toLowerCase())) {
            question = question.nextQuestion()
            "Отлично - ты справился\n${question.question}" to status.color
        }else{
            val prevStatus = status
            status = status.nextStatus()
            var msg = ""
            if (prevStatus.ordinal > status.ordinal){
                question = Question.NAME
                msg = ". Давай все по новой"
            }
            "Это неправильный ответ" + msg + "\n${question.question}" to status.color
        }
    }

    enum class Status(val color: Triple<Int, Int, Int>){
        NORMAL(Triple(255, 255, 255)),
        WARNING(Triple(255, 120, 0)),
        DANGER(Triple(255, 60, 60)),
        CRITICAL(Triple(255, 255, 0));

        fun nextStatus():Status {
            return if (this.ordinal < values().lastIndex){
                values()[this.ordinal + 1]
            } else{
                values()[0]
            }
        }
    }

    enum class Question(var question:String, val answers:List<String>) {
        NAME("Как меня зовут?", listOf("бендер", "bender")) {
            override fun validate(answer: String): String? =
                if (!answer.isBlank() && answer.first().isUpperCase())
                    null
                else
                    "Имя должно начинаться с заглавной буквы"

            override fun nextQuestion(): Question = PROFESSION
        },
        PROFESSION("Назови мою профессию?", listOf("сгибальщик", "bender")) {
            override fun validate(answer: String): String? =
                if (!answer.isBlank() && answer.first().isLowerCase())
                    null
                else
                    "Профессия должна начинаться со строчной буквы"

            override fun nextQuestion(): Question = MATERIAL
        },
        MATERIAL("Из чего я сделан?", listOf("металл", "дерево", "metal", "iron", "wood")) {
            override fun validate(answer: String): String? =
                if (!answer.isBlank() && !answer.matches(Regex("[0-9]+")))
                    null
                else
                    "Материал не должен содержать цифр"
                //Question.MATERIAL -> "Материал не должен содержать цифр"


            override fun nextQuestion(): Question = BDAY
        },
        BDAY("Когда меня создали?", listOf("2993")) {
            override fun validate(answer: String): String? =
                if (!answer.isBlank() && answer.toIntOrNull() != null)
                    null
                else
                    "Год моего рождения должен содержать только цифры"

            override fun nextQuestion(): Question = SERIAL
        },
        SERIAL("Мой серийный номер?", listOf("2716057")) {
            override fun validate(answer: String): String? =
                if (!answer.isBlank() && answer.length == 7 && answer.toIntOrNull() != null)
                    null
                else
                    "Серийный номер содержит только цифры, и их 7"

            override fun nextQuestion(): Question = IDLE
        },
        IDLE("На этом все, вопросов больше нет", listOf()) {
            override fun validate(answer: String): String? = null

            override fun nextQuestion(): Question = IDLE
        };

        abstract fun nextQuestion(): Question

        abstract fun validate(answer: String): String?
    }
}