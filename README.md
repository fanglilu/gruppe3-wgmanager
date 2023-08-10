# Steckbrief (Featurelist)

## General
### 1. Project Set Up
- Spring initializer (version 2.7, kotlin, PostgreSQL)
- Docker Compose File for Database
- Backend Structure: domain, dto, enum, repository, service
- Swagger-UI for API debugging and testing
- later: Use Liquibase for importing test data

### 2. CICD - Deployment
- Script for automated Deployment
- for each Push on branch main
- Database and Backend wgmanager.service

### 3. Working Method
- Use Hackmd for [Idea Generation](https://hackmd.io/kKgb9mUyQbO-H6R229lhvg)
- Use gitlab Issues (Boards, List, Milestone) to manage project
- Merge Request in early state to ensure higher code quality and structure 

## Featurelist
### 1. API Requests
#### 1. Authenticated Requests via JWT
1. override authenticate method
    --> own CustomAuthProvider because own User Table instead of given spring boot user
2. override filter method to also set authentication context
    --> get information of currently logged in user
#### 2. Implementation of Retrofit2 to communicate with our backend.
   1. Added Object mapper for dates
   2. Added auth interceptor
   3. Added request interceptor for debugging
#### 3. LogIn
- Automated Detection whether user is logged in
    --> otherwise lead to LogIn Screen
- On logout lead to LogIn Screen
#### 4. Implementation of an AlarmReceiver for notifications
1. Once the user is logged in the AlarmReceiver is initialised
2. It is called every ~10 minutes, no matter if the app is currently active
3. The userId is send to the server which returns all the notifications for this user
    * All notifications are collected
    * Only the ones which are for the specified user and have a date_to_send which is before the current date are returned to the user
    * The returned notifications are then shown on the users device
4. When the user logs out, the AlarmReceiver is stopped as otherwise notifications for the old user are still send
### 2. WGs (Frontend / Backend)
   1. WG Root
      1. Overview for all wgs the user is currently in. The behavior is changing dependent on the state (0 wg, 1 wg, n wgs)
   2. Create a wg
      1. User can choose different features, a name and the wg is crated with the creator as admin
   3. Join a wg
      1. User can join via invitation code.
      2. Invitation code is generate random by backend and got 6-digits for better readability
      3. Invitation code has a lifetime of one week for security reasons
         [Invitation codes expire after a defined time](backend-wgmanager/app/src/main/kotlin/lmu/gruppe3/wgmanager/wg_root/service/WgRootService.kt)
   4. Details of a wg
      1. Shows all details from a chosen wg (Name, creation date, features,...)
      2. Ability to leave the wg
      3. Ability to delete (if admin)
      4. Ability to generate a new invitation code
   5. All information is set in the [MainViewModel](app-wgmanager/app/src/main/java/msp/gruppe3/wgmanager/MainViewModel.kt) and gets updated ther if any changes
      1. All Views have an observer listening on get notified at any changes

### 3. Finance
#### Finance Frontend
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

#### Finance Backend
1. General
    1. History-based Data Structure --> depending on invoicePeriod
    2. Validation: Check for permission to edit/add depending on wgId
    3. Used tables: InvoicePeriod, Expense, Abo, Debt
2. Scheduler
    1. Recurring Expenses (Abo): Weekly and Monthly
        - Automatically add recurring expenses for current invoice period
        - Weekly: triggered on Monday
        - Montly: triggered on first of Month
    3. CashCrash
        - User can manually use the cashcrash function any time
        - However there is also an automated cashcrash at the start of a new month 
        - end current invoice period, start new invoice period, calculate all debts, send notifications
3. Edit/Delete Expenses vs Recurring Expenses
    - due to optional queryParam *forFuture* for the udpate/delete requests  either a single expense is edited/deleted or all conncted recurring expense in the future
4. Debt Calculation
    - everyone should pay equal amount --> calculation of deviation from average contribution depending on own contribution in invoice period
    - calculate and create debts method (n-1)
    - FinanaceUtilTest to ensure correct calculation and tallied values

### 4. Shopping Lists (Frontend)
1. ShowShoppingListView
    1. Acts as home view for the shopping list feature
    2. Shows all the lists for the current user and the current wg
    3. Only shows the private lists for the current user
    4. Clicking on a list item will show the items which are currently on the list
    5. Long clicking on a list item will enable the delete mode 
        * Will show the delete button
        * Clicking this button will delete the list and all items on this list
        * Long clicking the list item again will disable the delete mode
    6. Clicking the "+" floating action button (bottom right) will show the "create new list" dialog
        * After entering a name and clicking the button a new list will be created in the backends database
    7. Clicking the "shopping cart" floating action button (bottom left) will show the "enter shopping mode" dialog 
        * Here the lists which are to be used in the shopping mode can be selected from the current lists
        * Clicking the button will open the shopping mode view and will only use the lists selected 
    8. Pulling down will refresh the lists
2.  Show Items
    1. Here all the item from the list which has been clicked are shown
    2. Clicking the checkbox will set this item as bought
    3. Clicking on an item will show the item details screen
    4. Long clicking on an item will enable the delete mode 
        * Will show the delete button
        * Clicking this button will delete the item
        * Long clicking the item again will disable the delete mode
    5. Clicking the "+" floating action button (bottom right) will show the "create new item" dialog
        * After entering a name and description, then clicking the button, a new item will be created in the backends database
    6. Pulling down will refresh the items
3. Show item details
    1.  Shows the details about the item which has been clicked
    2.  Here you can edit the name and description of the item
    3.  Clicking the button will save the changes made

4. Shopping mode
    1. All the items from the previously selected lists are shown here
    2. Clicking the checkbox will set this item as bought
    3. Pulling down will refresh the items
    4. When the shopping is complete, clicking the button will show the add expense screen, so the value of the items which have just been bought can be added to the Wgs expenditures


### 5. Calendar Feature
#### 1. Calendar Home View
1. Month view with current and selected date highlighted
2. A blue indicator indicates that a calendar entry exists on this day
3. A grey indicator indicates that a calendar entry spanning multiple day exists on this day
4. A view below displays the events of the selected date 
5. The user can create a new event upon hitting the floating button when there is a date selected
#### 2. Calendar Entry Creation
1. The only mandatory field is the title. The rest either can be empty or gets filled automatically
2. Changing the time and/or day happens via a date/time picker dialog in order to disallow users to write their own dates
3. Selecting a member of the wg puts it on a list of users to be notified for this event
4. After creation the user gets routed back to the calendar home view
#### 3. Calendar Detail View
1. This view is shown upon clicking on one of the events displayed for the selected date
2. There the user can see all the details
3. If the user is the creator of the event, they can choose to delete or edit the calendar entry. Otherwise the calendar entry is in read-only mode
    
#### 1. Calendar Notifications 

2. The calendar creation triggers the creation of notifications for this event
3. Editing or deleting the calendar entry also updates the notifications accordingly 
