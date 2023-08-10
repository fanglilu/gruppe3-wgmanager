package msp.gruppe3.wgmanager

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavHost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import msp.gruppe3.wgmanager.common.TokenUtil
import msp.gruppe3.wgmanager.databinding.ActivityMainBinding
import msp.gruppe3.wgmanager.models.dtos.UserDto
import msp.gruppe3.wgmanager.services.AlarmReceiver
import msp.gruppe3.wgmanager.services.UserService
import java.util.*


private const val TAG = "MAIN Activity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toolbar: Toolbar
    private lateinit var navHost: NavHost
    private lateinit var mainViewModel: MainViewModel
    private lateinit var alarmManager: AlarmManager


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onResume() {
        super.onResume()
//        findMe(navHost.navController)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun findMe(navController: NavController) {
        val token = TokenUtil.getTokenByActivity(this)
        val userService = UserService(token)
        CoroutineScope(Dispatchers.IO).launch {
            val response = userService.findMe()
            handleFindMeResponse(response, navController, token)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun handleFindMeResponse(
        response: UserDto?,
        navController: NavController,
        token: String
    ) {
        Log.e(TAG, "findMe $response")

        if (response == null || response.email.isBlank()) {
            Log.e(TAG, "User not logged in")
            runOnUiThread {
                navController.navigate(R.id.loginFragment)
            }
        } else {
            mainViewModel.setUser(response)
            startAlarmReceiver(response.id)
            if (response.wgList.isNotEmpty()) {
                Log.e(TAG, "wg list is not empty ${response.wgList}")
                mainViewModel.wgList.postValue(response.wgList)
                mainViewModel.setWg(
                    response.wgList.sortedBy { it.joinedAt }.map { it.id }.last(),
                    token
                )
                if (response.wgList.size == 1) {
                    runOnUiThread {
                        navController.navigate(R.id.homeFragment)
                    }
                } else {
                    runOnUiThread {
                        navController.navigate(R.id.wgRootFragment)
                    }
                }
            } else {
                Log.e(TAG, "wg list is empty ${response.wgList}")
                runOnUiThread {
                    navController.navigate(R.id.wgRootFragment)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        alarmManager = this.getSystemService(ALARM_SERVICE) as AlarmManager
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.e(TAG, "App Started")
        navHost =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHost
        findMe(navHost.navController)

        initToolbar(navHost)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun logout() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to logout?")
            .setCancelable(true)
            .setPositiveButton("Yes") { dialog, id ->
                // Remove token
                getSharedPreferences("wgManagerPref", Context.MODE_PRIVATE)?.edit()
                    ?.putString("token", "")?.apply()
                // Unset current user
                this.mainViewModel.userCurrent.value = null

                val intentCancel = Intent(this, AlarmReceiver::class.java)

                // Create the broadcast intent
                val pendingIntent =
                    PendingIntent.getBroadcast(this, 0, intentCancel,  PendingIntent.FLAG_MUTABLE)
                alarmManager.cancel(pendingIntent)

                // Navigate to login
                finish()
                startActivity(intent)
            }
            .setNegativeButton("No") { dialog, id ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
  }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun initToolbar(navHost: NavHost) {

        toolbar = binding.myToolbar

        toolbar.title = ""


        val homeButton = ImageButton(this)
        val lHome: Toolbar.LayoutParams =
            Toolbar.LayoutParams(
                Toolbar.LayoutParams.WRAP_CONTENT,
                Toolbar.LayoutParams.WRAP_CONTENT
            )
        lHome.gravity = Gravity.START
        homeButton.layoutParams = lHome
        homeButton.background = null
        homeButton.setImageResource(R.drawable.ic_home_white_24dp)
        homeButton.setOnClickListener {
            navHost.navController.navigate(R.id.homeFragment)
        }
        val customTitle = toolbar.findViewById<TextView>(R.id.custom_title)
        val parent = customTitle.parent as ViewGroup
        parent.addView(homeButton, parent.indexOfChild(customTitle))


        customTitle.setOnClickListener{
            navHost.navController.navigate(R.id.homeFragment)
        }

        val wgRootButton = ImageButton(this)
        val l1: Toolbar.LayoutParams =
            Toolbar.LayoutParams(
                Toolbar.LayoutParams.WRAP_CONTENT,
                Toolbar.LayoutParams.WRAP_CONTENT
            )
        l1.gravity = Gravity.END
        l1.rightMargin = 30
        wgRootButton.layoutParams = l1
        wgRootButton.background = null
        wgRootButton.setImageResource(R.drawable.ic_settings_white_24dp)
        wgRootButton.setOnClickListener {
            navHost.navController.navigate(R.id.wgRootFragment)
        }
        toolbar.addView(wgRootButton)

        setSupportActionBar(toolbar)

    }

    // Creates and start the alarm receiver
    @RequiresApi(Build.VERSION_CODES.S)
    fun startAlarmReceiver(userId: UUID) {

        // Creates the channel on the device, so notifications can be displayed
        createNotificationChannel()

        // Creates the intent to create the alarm manager
        val intent = Intent(this, AlarmReceiver::class.java)
        // Sets the action so the alarm receiver can catch this and execute a specified method
        intent.action = "GET_NOTIFICATION"
        val userString = userId.toString()
        intent.putExtra("key", userString)

        //set the alarm time to check with the server every 10 minutes
        val alarmTime = System.currentTimeMillis()
        val timeInterval = 600000L

        // Create the broadcast intent
        val pendingIntent =
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE)

        // Sets the alarm manager, so that the created intent is send every 10 minutes
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime, timeInterval, pendingIntent)
    }

    // Creates the NotificationChannel on the device
    private fun createNotificationChannel() {
        val name = "Notification"
        val descriptionText = "Notification"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("Notification", name, importance).apply {
            description = descriptionText
        }

        val notificationManager =
            this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

}