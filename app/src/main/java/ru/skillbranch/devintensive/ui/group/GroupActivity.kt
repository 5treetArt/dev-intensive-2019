package ru.skillbranch.devintensive.ui.group

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.children
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.activity_group.*
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.models.data.UserItem
import ru.skillbranch.devintensive.ui.BaseActivity
import ru.skillbranch.devintensive.ui.custom.MaterialDividerItemDecorator
import ru.skillbranch.devintensive.ui.adapters.UserAdapter
import ru.skillbranch.devintensive.viewmodels.GroupViewModel

class GroupActivity : BaseActivity() {

    private lateinit var userAdapter: UserAdapter
    private lateinit var groupViewModel: GroupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)
        initToolbar()
        initViews()
        initViewModel()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        searchView.queryHint = getString(R.string.group_search_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                groupViewModel.handleSearchQuery(query)
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                groupViewModel.handleSearchQuery(query)
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == android.R.id.home){
            finish()
            overridePendingTransition(R.anim.idle, R.anim.bottom_down)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initViews() {
        userAdapter = UserAdapter { groupViewModel.handleSelectedItem(it.id) }

        val dividerColor = TypedValue()
        theme.resolveAttribute(R.attr.colorDivider, dividerColor, true)
        val backgroundColor = TypedValue()
        theme.resolveAttribute(R.attr.colorItemBackground, backgroundColor, true)

        val divider = MaterialDividerItemDecorator(this@GroupActivity, dividerColor.data, backgroundColor.data)
        with(rv_user_list){
            adapter = userAdapter
            layoutManager = LinearLayoutManager(this@GroupActivity)
            addItemDecoration(divider)
        }

        fab.setOnClickListener{
            groupViewModel.handleCreateGroup()
            finish()
            overridePendingTransition(R.anim.idle, R.anim.bottom_down)
        }
    }

    private fun initViewModel() {
        groupViewModel = ViewModelProviders.of(this).get(GroupViewModel::class.java)
        super.initViewModel(this, groupViewModel)
        groupViewModel.getUserData().observe(this, Observer { userAdapter.updateData(it) })
        groupViewModel.getSelectedData().observe(this, Observer {
            updateChips(it)
            toggleFab(it.size > 1)
        })
    }

    private fun toggleFab(isShow: Boolean) {
        if (isShow) fab.show()
        else fab.hide()
    }

    private fun addChipToGroup(user: UserItem){

        val color = TypedValue()
        theme.resolveAttribute(R.attr.colorChip, color, true)

        val chip = Chip(this).apply {
            text = user.fullName
            chipIcon = resources.getDrawable(R.drawable.avatar_default, theme)
            isCloseIconVisible = true
            tag = user.id
            isClickable = true
            closeIconTint = ColorStateList.valueOf(Color.WHITE)
            chipBackgroundColor = ColorStateList.valueOf(color.data)
            setTextColor(Color.WHITE)
        }
        chip.setOnCloseIconClickListener{groupViewModel.handleRemoveChip(it.tag.toString())}
        chip_group.addView(chip)
    }

    private fun updateChips(listUsers : List<UserItem>){
        chip_group.visibility = if(listUsers.isEmpty()) View.GONE else View.VISIBLE

        val users = listUsers
            .associate { user-> user.id to user }
            .toMutableMap()

        val views = chip_group.children.associate { view -> view.tag to view }

        for((k,v) in views){
            if(!users.containsKey(k)) chip_group.removeView(v)
            else users.remove(k)
        }

        users.forEach{(_,v)->addChipToGroup(v)}
    }
}
