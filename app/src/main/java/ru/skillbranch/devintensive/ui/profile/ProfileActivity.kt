package ru.skillbranch.devintensive.ui.profile

import android.content.Context
import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_profile.*
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.models.Profile
import ru.skillbranch.devintensive.ui.BaseActivity
import ru.skillbranch.devintensive.ui.custom.BitmapBuilder
import ru.skillbranch.devintensive.utils.Utils
import ru.skillbranch.devintensive.viewmodels.ProfileViewModel
import kotlin.math.roundToInt

class ProfileActivity : BaseActivity(){

    companion object {
        const val IS_EDIT_MODE = "IS_EDIT_MODE"
    }

    private lateinit var profileViewModel: ProfileViewModel
    var isEditMode = false

    lateinit var viewFields : Map<String, TextView>
    private var userInitials: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        initViews(savedInstanceState)
        initViewModel()
        Log.d("M_ProfileActivity","onCreate")
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.idle, R.anim.bottom_down)
        super.onBackPressed()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(IS_EDIT_MODE, isEditMode)
    }

    override fun initViewModel(){
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        profileViewModel = viewModel as ProfileViewModel
        super.initViewModel()

        profileViewModel.getProfileData().observe(this, Observer { updateUI(it) })
        profileViewModel.getRepositoryError().observe(this, Observer { updateRepoError(it) })
        profileViewModel.getIsRepoError().observe(this, Observer { updateRepository(it) })
    }

    private fun updateRepository(isError: Boolean) {
        if (isError) et_repository.text.clear()
    }

    private fun updateRepoError(isError: Boolean) {
        wr_repository.isErrorEnabled = isError
        wr_repository.error = if (isError) "Невалидный адрес репозитория" else null
    }

    //private fun updateTheme(mode: Int) {
    //    Log.d("M_ProfileActivity","updateTheme")
    //    delegate.setLocalNightMode(mode)
    //}

    private fun updateUI(profile: Profile) {
        profile.toMap().also {
            for ((k,v) in viewFields){
                v.text = it[k].toString()
            }
        }
        updateAvatar(profile)
    }

    private fun updateAvatar(profile: Profile) {
        Utils.toInitials(profile.firstName, profile.lastName)?.let {
            if (it != userInitials)
                setInitials(it)
        } ?: iv_avatar.setImageResource(R.drawable.avatar_default)
    }

    private fun setInitials(initials: String){
        val avatar = getAvatarBitmap(initials)
        iv_avatar.setImageBitmap(avatar)
    }

    private fun getAvatarBitmap(text: String): Bitmap {
        val color = TypedValue()
        theme.resolveAttribute(R.attr.colorAccent, color, true)

        return BitmapBuilder(iv_avatar.layoutParams.width, iv_avatar.layoutParams.height)
            .setBackgroundColor(color.data)
            .setText(text)
            .setTextSize(Utils.convertSpToPx(this, 48))
            .setTextColor(Color.WHITE)
            .build()
    }

        //TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp.toFloat(), context.resources.displayMetrics).roundToInt()


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
            profileViewModel.onRepoEditCompleted(wr_repository.isErrorEnabled)

            if (isEditMode) saveProfileInfo()
            isEditMode = !isEditMode
            showCurrentMode(isEditMode)
        }

        btn_switch_theme.setOnClickListener {
            profileViewModel.switchTheme()
        }

        et_repository.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable?) {
                profileViewModel.onRepositoryChanged(s.toString())
            }
        })
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
            profileViewModel.saveProfileData(this)
        }
    }
}
