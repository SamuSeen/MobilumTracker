package com.example.mobilumtracker

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.mobilumtracker.databinding.ActivityMainBinding
import com.example.mobilumtracker.db.Running
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch{
            Running.init(applicationContext, lifecycleScope)
            // Mileage bar
            val initialMileage = Running.getMileage()
            binding.appBarMain.editTextMileage.setText(initialMileage.toString())
            binding.appBarMain.editTextMileage.setOnFocusChangeListener { view, hasFocus ->
                if (!hasFocus) {
                    updateMileage(view)
                }
            }
            binding.appBarMain.editTextMileage.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    updateMileage(v)
                    v.clearFocus() // Remove focus from EditText
                    true
                } else {
                    false
                }
            }


        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        // Floating button click listener
        binding.appBarMain.fab.setOnClickListener { view ->
            if (!navController.navigateUp()) {
                onBackPressedDispatcher.onBackPressed()
            }
        }

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top-level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_event, R.id.nav_calendar, R.id.nav_add), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        // Custom menu item click listener - because the default one breaks on selected Event
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {// Handle menu item clicks
                R.id.nav_event -> {
                    navController.navigate(R.id.nav_event)
                }
                R.id.nav_calendar -> {
                    navController.navigate(R.id.nav_calendar)
                }
                R.id.nav_add -> {
                    navController.navigate(R.id.nav_add)
                }
                else -> {
                    Log.e("MainActivity", "Invalid menu item clicked")
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)// Close the drawer
            true
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    /**
     * Menu item click listener
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return when (item.itemId) {
            R.id.action_sort_id -> {
                Running.setSortMode(0)
                refreshCurrentFragment(navController)
                true
            }
            R.id.action_sort_time -> {
                Running.setSortMode(1)
                refreshCurrentFragment(navController)
                true
            }
            R.id.action_sort_distance -> {
                Running.setSortMode(2)
                refreshCurrentFragment(navController)
                true
            }
            R.id.action_sort_alphabet -> {
                Running.setSortMode(3)
                refreshCurrentFragment(navController)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    /**
     * Update mileage
     */
    private fun updateMileage(view: View) {
        val newMileage = binding.appBarMain.editTextMileage.text.toString().toIntOrNull() ?: 0
        Running.setMileage(newMileage)
        binding.appBarMain.editTextMileage.setText(newMileage.toString())
        // Hide the keyboard
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        // Restart the activity to update the mileage
        /*finish()
        startActivity(intent)*/
        // Restart the fragment instead
        refreshCurrentFragment(findNavController(R.id.nav_host_fragment_content_main))
    }

    /**
     * Refresh the current fragment
     */
    private fun refreshCurrentFragment(navController: NavController) {
        val currentDestination = navController.currentDestination
        currentDestination?.let {
            navController.navigate(it.id)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SSUtils.initializeNotifications(this, lifecycleScope)
    }
}
