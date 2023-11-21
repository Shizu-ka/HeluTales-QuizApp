package myid.shizuka.rpl.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import myid.shizuka.rpl.R
import myid.shizuka.rpl.activities.LoginActivity
import myid.shizuka.rpl.activities.MainActivity
import myid.shizuka.rpl.activities.ProfileActivity

object DrawerUtils {
    fun setupNavigationDrawer(activity: AppCompatActivity, appBar: MaterialToolbar, mainDrawer: DrawerLayout, navigationView: NavigationView, currentPage: String) {
        activity.setSupportActionBar(appBar)

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            activity,
            mainDrawer,
            R.string.app_name,
            R.string.app_name
        )
        actionBarDrawerToggle.syncState()

        val menu = navigationView.menu
        navigationView.setNavigationItemSelectedListener { menuItem ->
            if (menuItem.itemId == R.id.profilePage && currentPage == "profilePage") {
                val highlightedItem = menu.findItem(R.id.profilePage)
                highlightedItem.isChecked = true
            } else if(menuItem.itemId == R.id.mainPage && currentPage == "mainPage") {
                val highlightedItem = menu.findItem(R.id.mainPage)
                highlightedItem.isChecked = true
            }
            else {
                when (menuItem.itemId) {
                    R.id.mainPage -> {
                        val intent = Intent(activity, MainActivity::class.java)
                        activity.startActivity(intent)
                    }
                    R.id.profilePage -> {
                        val intent = Intent(activity, ProfileActivity::class.java)
                        activity.startActivity(intent)
                    }
//                    R.id.followUs -> {
//                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.shizuka.my.id"))
//                        activity.startActivity(browserIntent)
//                    }
//                    R.id.rateUs -> {
//                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.shizuka.my.id"))
//                        activity.startActivity(browserIntent)
//                    }
                    R.id.logOut -> {
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(activity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        activity.startActivity(intent)
                        activity.finish()
                    }
                }
            }
            ColorPicker.resetColorIndex()
            IconPicker.resetIconIndex()
            mainDrawer.closeDrawers()
            true
        }

        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}

