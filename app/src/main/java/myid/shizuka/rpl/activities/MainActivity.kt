package myid.shizuka.rpl.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import myid.shizuka.rpl.R
import myid.shizuka.rpl.adapters.FirebaseHelper
import myid.shizuka.rpl.adapters.QuizAdapter
import myid.shizuka.rpl.models.Quiz
import myid.shizuka.rpl.utils.DrawerUtils

class MainActivity : AppCompatActivity() {
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    lateinit var adapter: QuizAdapter
    private var quizList = mutableListOf<Quiz>()
    lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpViews()
    }
    fun setUpViews() {
        fetchDataFromFirestore()
        setUpDrawerLayout()
        setUpRecycleView()
    }
    private fun fetchDataFromFirestore() {
        FirebaseHelper.fetchQuizzes(this) { quizzes ->
            quizList.clear()
            quizList.addAll(quizzes)
            adapter.notifyDataSetChanged()
        }
    }


    private fun setUpRecycleView() {
        adapter = QuizAdapter(this,quizList)
        val quizRecyclerView = findViewById<RecyclerView>(R.id.quizRecyclerView)
        quizRecyclerView.layoutManager = GridLayoutManager(this, 2)
        quizRecyclerView.adapter = adapter
    }

    fun setUpDrawerLayout() {
        val currentPage = "mainPage"
        val appBar = findViewById<MaterialToolbar>(R.id.appBar)
        val mainDrawer = findViewById<DrawerLayout>(R.id.mainDrawer)
        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        setSupportActionBar(appBar)
        actionBarDrawerToggle = ActionBarDrawerToggle(this, mainDrawer,
            R.string.app_name,
            R.string.app_name
        )
        actionBarDrawerToggle.syncState()
        val menu = navigationView.menu
        val highlightedItem = menu.findItem(R.id.mainPage)
        highlightedItem.isChecked = true
        DrawerUtils.setupNavigationDrawer(this, appBar, mainDrawer, navigationView,currentPage)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        actionBarDrawerToggle.syncState()
    }

}