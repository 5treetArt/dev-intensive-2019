package ru.skillbranch.devintensive.ui.profile

import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_profile.*
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.models.Profile
import ru.skillbranch.devintensive.viewmodels.ProfileViewModel

class ProfileActivity : AppCompatActivity(){

    /* TODO SplashTheme
Необходимо реализовать тему, отображаемую при загрузке приложения до момента создания Activity

Реализуй SplashTheme в соответствии с макетами (@style/SplahTheme).
Необходимо реализовать ее отображение при запуске приложения до момента создания Activity.
Как только Activity будет создана, необходимо установить AppTheme
*/

    /* TODO Text Input Layout error
    Необходимо реализовать вадидацию вводимых пользователем данных
    в поле @id/et_repository на соответствие url валидному github аккаунту

    Реализуй валидацию
    (валидация должна происходить в процессе ввода данных)
    вводимых пользователем данных в поле @id/et_repository на соответствие url валидному github аккаунту,
    вводимое значение может быть пустой строкой или должно содержать
    домен github.com (https://, www, https://www) и аккаунт пользователя
    (пути для исключения прикреплены в ресурсах урока).
    Если URL невалиден, выводить сообщение "Невалидный адрес репозитория"
    в TextInputLayout (wr_repository.error(message)) и запрещать
    сохранение невалидного значения в SharedPreferences
    (при попытке сохранить невалидное поле очищать et_repository при нажатии @id/btn_edit)

    Пример:
    https://github.com/johnDoe //валиден
    https://www.github.com/johnDoe //валиден
    www.github.com/johnDoe //валиден
    github.com/johnDoe //валиден
    https://anyDomain.github.com/johnDoe //невалиден
    https://github.com/ //невалиден
    https://github.com //невалиден
    https://github.com/johnDoe/tree //невалиден
    https://github.com/johnDoe/tree/something //невалиден
    https://github.com/enterprise //невалиден
    https://github.com/pricing //невалиден
    https://github.com/join //невалиден


    Пути:
    enterprise

    features

    topics

    collections

    trending

    events

    marketplace

    pricing

    nonprofit

    customer-stories

    security

    login

    join
*/

    /*TODO Преобразование Инициалов в Drawable
    Необходимо реализовать программное преобразование
    инициалов пользователя в Drawable с цветным фоном и буквами

    Реализуй программное преобразование инициалов пользователя
    (если доступны - заполнено хотя бы одно поле) в Drawable
    с фоном colorAccent (c учетом темы) и буквами инициалов (colorWhite)
    и установи полученное изображение как изображение по умолчанию для профиля пользователя*/

    companion object {
        const val IS_EDIT_MODE = "IS_EDIT_MODE"
    }

    private lateinit var viewModel: ProfileViewModel
    var isEditMode = false

    lateinit var viewFields : Map<String, TextView>


    override fun onCreate(savedInstanceState: Bundle?) {
        //TODO set custom Theme here before super and setContentView

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        initViews(savedInstanceState)
        initViewModel()
        Log.d("M_ProfileActivity","onCreate")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(IS_EDIT_MODE, isEditMode)
    }

    private fun initViewModel(){
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        viewModel.getProfileData().observe(this, Observer { updateUI(it) })
        viewModel.getTheme().observe(this, Observer { updateTheme(it) })
    }

    private fun updateTheme(mode: Int) {
        Log.d("M_ProfileActivity","updateTheme")
        delegate.setLocalNightMode(mode)
    }

    private fun updateUI(profile: Profile) {
        profile.toMap().also {
            for ((k,v) in viewFields){
                v.text = it[k].toString()
            }
        }
    }


    private fun initViews(savedInstanceState: Bundle?) {
        viewFields = mapOf(
            "nickName" to tv_nick_name,
            "rank" to tv_rank,
            "firstName" to et_first_name,
            "lastName" to et_last_name,
            "about" to et_about,
            "repository" to et_repository,
            "rating" to tv_rating,
            "respect" to tv_respect
        )

        isEditMode = savedInstanceState?.getBoolean(IS_EDIT_MODE, false) ?: false
        showCurrentMode(isEditMode)


        btn_edit.setOnClickListener {
            if (isEditMode) saveProfileInfo()
            isEditMode = !isEditMode
            showCurrentMode(isEditMode)
        }

        btn_switch_theme.setOnClickListener {
            viewModel.switchTheme()
        }
    }

    private fun showCurrentMode(isEdit: Boolean) {
        val info = viewFields.filter { setOf("firstName", "lastName", "about", "repository").contains(it.key) }
        for((_,v) in info) {
            v as EditText
            v.isFocusable = isEdit
            v.isFocusableInTouchMode = isEdit
            v.isEnabled = isEdit
            v.background.alpha = if(isEdit) 255 else 0
        }

        ic_eye.visibility = if (isEdit) View.GONE else View.VISIBLE

        wr_about.isCounterEnabled = isEdit

        with(btn_edit){
            val filter: ColorFilter? = if(isEdit){
                PorterDuffColorFilter(
                    resources.getColor(R.color.color_accent, theme),
                    PorterDuff.Mode.SRC_IN
                )
            }else{
                null
            }

            val icon = if (isEdit){
                resources.getDrawable(R.drawable.ic_save_black_24dp, theme)
            }else{
                resources.getDrawable(R.drawable.ic_edit_black_24dp, theme)
            }

            background.colorFilter = filter

            setImageDrawable(icon)
        }
    }

    private fun saveProfileInfo() {
        Profile(
            firstName = et_first_name.text.toString(),
            lastName = et_last_name.text.toString(),
            about = et_about.text.toString(),
            repository = et_repository.text.toString()
        ).apply {
            viewModel.saveProfileData(this)
        }
    }
}
