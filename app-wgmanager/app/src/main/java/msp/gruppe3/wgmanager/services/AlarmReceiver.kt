package msp.gruppe3.wgmanager.services


import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import msp.gruppe3.wgmanager.models.Notification
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import msp.gruppe3.wgmanager.MainActivity
import msp.gruppe3.wgmanager.R
import msp.gruppe3.wgmanager.ui.features.calendar.fragments.CalendarHomeFragment
import retrofit2.HttpException
import java.util.*

private const val TAG = "Alarm Receiver"

class AlarmReceiver : BroadcastReceiver() {

    // This is the name of the NotificationChannel defined in MainActivity
    private val channelID: String = "Notification"
    // Running variable to continuously increment with every notification
    var notificationsSend = 0


    // Is triggered when alarm goes off, i.e. receiving a system broadcast
    override fun onReceive(context: Context, intent: Intent) {
        // Catches the GET_NOTIFICATION intent
        if (intent.action == "GET_NOTIFICATION") {
            val extras = intent.extras?.getString("key")
            checkWithServer(context, UUID.fromString(extras.toString()))
        }
    }

    // Creates a notification and sends it to the user
    private fun sendNotification(context: Context, notification: Notification, notificationId: Int){

        // Creates the intent to manage the on click action
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        // Builds the notification itself with the provided name and content
        var builder = NotificationCompat.Builder(context, this.channelID)
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setContentTitle(notification.title)
            .setContentText(notification.content)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(notification.content))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // Shows the notification
        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }

    // Sends a request to the server for the notifications for this user
    private fun checkWithServer(context: Context, userID: UUID){
        val notificationService = NotificationService()

        CoroutineScope(Dispatchers.IO).launch {
            val response = notificationService.getNotifications(userID)
            withContext(Dispatchers.Main) {
                try {
                    Log.e(TAG, "Response: $response")
                    if (response != null) {

                        // Loop over all the entries in the response creating and sending a notification for each
                        for (notification in response){
                            // Needs to be incremented every notification
                            notificationsSend += 1
                            sendNotification(context, notification, notificationsSend)
                        }

                    } else {
                        Toast.makeText(
                            context,
                            "Sorry something went wrong.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: HttpException) {
                    Log.e("HTTPException", "Exception ${e.message}")
                } catch (e: Throwable) {
                    Log.e("Throwable", e.toString())
                }
            }
        }
    }
}
