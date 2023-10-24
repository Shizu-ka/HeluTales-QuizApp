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
import com.google.firebase.firestore.FirebaseFirestore
import myid.shizuka.rpl.R
import myid.shizuka.rpl.adapters.QuizAdapter
import myid.shizuka.rpl.models.Quiz

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
        setUpFirestore()
        setUpDrawerLayout()
        setUpRecycleView()
    }

    private fun setUpFirestore() {
        firestore = FirebaseFirestore.getInstance()
        val collectionReference = firestore.collection("quizzes")
        collectionReference.addSnapshotListener { value, error ->
            if(value == null || error != null){
                Toast.makeText(this, "Error fetching data", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            Log.d("DATA", value.toObjects(Quiz::class.java).toString())
            quizList.clear()
            quizList.addAll(value.toObjects(Quiz::class.java))
            adapter.notifyDataSetChanged()
        }
    }

    private fun populateDummyData() {
//        quizList.add(Quiz("11-12-2023","12-10-2023"))
//        quizList.add(Quiz("13-10-2023","13-10-2023"))
//        quizList.add(Quiz("14-10-2023","14-10-2023"))
//        quizList.add(Quiz("15-10-2023","15-10-2023"))
//        quizList.add(Quiz("16-10-2023","16-10-2023"))
    }

    private fun setUpRecycleView() {
        adapter = QuizAdapter(this,quizList)
        val quizRecyclerView = findViewById<RecyclerView>(R.id.quizRecyclerView)
        quizRecyclerView.layoutManager = GridLayoutManager(this, 2)
        quizRecyclerView.adapter = adapter
    }

    fun setUpDrawerLayout() {
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

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.profilePage -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                }
                R.id.followUs -> {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.shizuka.my.id"))
                    startActivity(browserIntent)
                }
                R.id.rateUs -> {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.shizuka.my.id"))
                    startActivity(browserIntent)
                }
            }
            mainDrawer.closeDrawers()
            true
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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