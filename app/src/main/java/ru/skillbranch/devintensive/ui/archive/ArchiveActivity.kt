package ru.skillbranch.devintensive.ui.archive

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_archive.*
import kotlinx.android.synthetic.main.activity_main.toolbar
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.extensions.setBackgroundDrawable
import ru.skillbranch.devintensive.extensions.setTextColor
import ru.skillbranch.devintensive.ui.BaseActivity
import ru.skillbranch.devintensive.ui.adapters.ChatAdapter
import ru.skillbranch.devintensive.ui.adapters.ChatItemTouchHelperCallback
import ru.skillbranch.devintensive.ui.adapters.IconType
import ru.skillbranch.devintensive.ui.custom.MaterialDividerItemDecorator
import ru.skillbranch.devintensive.viewmodels.ArchiveViewModel

class ArchiveActivity : BaseActivity() {

    private lateinit var chatAdapter: ChatAdapter
    private lateinit var archiveViewModel: ArchiveViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_archive)
        initToolbar()
        initViews()
        initViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        searchView.queryHint = getString(R.string.main_search_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                archiveViewModel.handleSearchQuery(query)
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                archiveViewModel.handleSearchQuery(query)
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

        chatAdapter = ChatAdapter{
            Snackbar.make(rv_archive_list, "Click on ${it.title}", Snackbar.LENGTH_LONG).show()
        }

        val dividerColor = TypedValue()
        theme.resolveAttribute(R.attr.colorDivider, dividerColor, true)
        val backgroundColor = TypedValue()
        theme.resolveAttribute(R.attr.colorItemBackground, backgroundColor, true)

        val divider = MaterialDividerItemDecorator(this@ArchiveActivity, dividerColor.data, backgroundColor.data)

        val touchCallback = ChatItemTouchHelperCallback(chatAdapter, IconType.ARCHIVE_OUT){ chatIt ->
            archiveViewModel.restoreFromArchive(chatIt.id)

            val textColor = TypedValue()
            theme.resolveAttribute(R.attr.colorSnackbarText, textColor, true)

            //TODO в ресурсы
            Snackbar.make(rv_archive_list, "Восстановить чат с ${chatIt.title} из архива?", Snackbar.LENGTH_LONG)
                .setBackgroundDrawable(R.drawable.bg_snackbar)
                .setTextColor(textColor.data)
                .setAction(getString(R.string.main_snackbar_cancel)){ archiveViewModel.addToArchive(chatIt.id) }
                .show()
        }

        val touchHelper = ItemTouchHelper(touchCallback)
        touchHelper.attachToRecyclerView(rv_archive_list)

        with(rv_archive_list){
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(this@ArchiveActivity)
            addItemDecoration(divider)
        }
    }

    override fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(ArchiveViewModel::class.java)
        archiveViewModel = viewModel as ArchiveViewModel
        super.initViewModel()
        archiveViewModel.getChatData().observe(this, Observer { chatAdapter.updateData(it) })
    }


}
