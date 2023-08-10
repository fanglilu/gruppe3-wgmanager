# Retrofit 2
We use retrofit 2 in this application to communicate with our backend.

Everything you need is in the endpoints-folder.

All further information is in detail here (POST, GET, ...)
[Retrofit - Basic](https://codersee.com/retrofit-with-kotlin-the-ultimate-guide/)
and here is everything to coroutines
[Retrofit - Coroutines](https://codersee.com/retrofit-with-kotlin-coroutines-what-you-need-to-know/)

## Apis
Add all routes to the folder endpoints/apis

## Interceptors
Add all interceptors to the folder endpoints/interceptors
TODO: We have to add the user token to the Authorization-Interceptor

## Utils
RetrofitClient does the magic for us. Ther you can add interceptors or spacial converters to parse
the json stuff correctly

## How to use retrofit 2?
1. First define your api routes in endpoints/apis
2. Add a service to the services-folder. This service triggers the api calls and includes error handling
3. Use the service in the code. Notice that the suspend calls can only triggerd in a Coroutine!

## Coroutines
Use coroutines for main-safety
To specify where the coroutines should run, Kotlin provides three dispatchers that you can use:

Dispatchers.Main - Use this dispatcher to run a coroutine on the main Android thread. This should be used only for interacting with the UI and performing quick work. Examples include calling suspend functions, running Android UI framework operations, and updating LiveData objects.
Dispatchers.IO - This dispatcher is optimized to perform disk or network I/O outside of the main thread. Examples include using the Room component, reading from or writing to files, and running any network operations.
Dispatchers.Default - This dispatcher is optimized to perform CPU-intensive work outside of the main thread. Example use cases include sorting a list and parsing JSON.

Performance of withContext()
withContext() does not add extra overhead compared to an equivalent callback-based implementation. Furthermore, it's possible to optimize withContext() calls beyond an equivalent callback-based implementation in some situations. For example, if a function makes ten calls to a network, you can tell Kotlin to switch threads only once by using an outer withContext(). Then, even though the network library uses withContext() multiple times, it stays on the same dispatcher and avoids switching threads. In addition, Kotlin optimizes switching between Dispatchers.Default and Dispatchers.IO to avoid thread switches whenever possible.

### Example coroutine
```kotlin
binding.buttonSubmit.isEnabled = false

CoroutineScope(Dispatchers.IO).launch {
    val response = service.methodCallsApiRouteAndDoesErrorHandling
    withContext(Dispatchers.Main) {
        try {
            Log.e(TAG, "Response: $response")
            if (response != null) {
                val viewModel = ViewModelProvider(requireActivity())[viewModel::class.java]
                viewModel.addResponse(response)
            } else {
                binding.buttonSubmit.isEnabled = true
                Toast.makeText(
                    context,
                    "Sorry something went wrong.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: HttpException) {
            binding.buttonSubmit.isEnabled = true
            Log.e(TAG, "Exception ${e.message}")
        } catch (e: Throwable) {
            binding.buttonSubmit.isEnabled = true
            Log.e(TAG, e.toString())
        }
    }
}
```

# Notifications

## How to Test
1. Start the server
2. Log into swagger and create a list using the provided API
3. Adjust time interval to something low - frontend NotificationsFragment line 80
4. Manually set the recipient UURI you have used in the create list
5. Start the client
6. Wait for about a minute

## General working procedure

1. Whenever something on the server happens that warrants a notification -> Use the interface to create one with the recipient and title, text
2. When the client checks with the server for notifications -> Server returns all notifications for the recipient UUID provided  the UUID the client has send.
3. When the package has been send, the notifications are deleted from the servers database


# What have I done

## Marcello
1. Implementation of Retrofit2 to communicate with our backend.
   1. Added Object mapper for dates
   2. Added auth interceptor
   3. Added request interceptor for debugging
2. WGs (Frontend / Backend)
   1. WG Root
      1. Overview for all wgs the user is currently in. The behavior is changing dependent on the state (0 wg, 1 wg, n wgs)
   2. Create a wg
      1. User can choose different features, a name and the wg is crated with the creator as admin
   3. Join a wg
      1. User can join via invitation code.
      2. Invitation code is generate random by backend and got 6-digits for better readability
      3. Invitation code has a lifetime of one week for security reasons
         1. [Invitation codes expire after a defined time](src/main/kotlin/lmu/gruppe3/wgmanager/wg_root/service/WgRootService.kt)
   4. Details of a wg
      1. Shows all details from a chosen wg (Name, creation date, features,...)
      2. Ability to leave the wg
      3. Ability to delete (if admin)
      4. Ability to generate a new invitation code
   5. All information is set in the [MainViewModel](src/main/java/msp/gruppe3/wgmanager/MainViewModel.kt) and gets updated ther if any changes
      1. All Views have an observer listening on get notified at any changes
3. Finance feature (Frontend)
   1. FinanceFragment
      1. Acts like Home for finance feature and makes following accessible
      2. Shows all expenses of current period
      3. Shows all expenses ever taken if "Show all periods"-Button is clicked
      4. Ability to add a new expenses via click on floating action button
      5. Ability to edit expenses via click on the expenses
   2. Add / Edit expenses
      1. Ability to create an expense (photo, description, price)  validation included
      2. Ability to edit / delete an expense (photo, description, price)  validation included
      3. Has connection to shopping list. On end of the shopping mode you get redirected to add expenses. The number of items you shopped is set as description. You only have to enter the costs.
   3. Statistics
      1. Shows statistic of the current user
         1. Total sum
         2. Average
         3. contributed
         4. deviation
   4. Cash crash
      1. Triggers end of a period and notifies all members that they have to pay
   5. Settle up
