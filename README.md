(Created for the final project of CS 360)

# Summary of the app requirements and audience

This app was given a list of needs, primarily to be able to create, read, update, and delete items in a stored inventory list from a smartphone. The app also needed to be able to allow user sign-up and sign-in to access the system. I took this a step further by implementing role-based permissions, where Users can just view the inventory, and Admins can add items, update inventory counts, delete inventory items, and more. The target audience of such an app would be business managers looking to make their warehouse and inventory operations more efficient.

# Necessarry screens to provide the needs of the user experience

Given the main point is the quick adjustment of inventory, my main UI design was to make the complete inventory front and center. I considered that adjusting counts would be the most important task of those managing inventory, and thusly made it so there are convenient + and - buttons on each item object that dynamically adjusts that item's count. I decided to make the items in a recyclerView list using a card view. This makes each item separate in a readable and easy-to-follow manner and allows for ample space for the controls of the items. I designed a fixed button tool bar to be at the bottom of the device screen, where the user can quickly decide to Logout, Add a new item, and change their app settings. All of these were designed as Fragments to ensure the user needs not to remember too many navigation routes and can quickly access the most important functions of the app.

# How did I approach designing the app?

When designing programs, I start with sorting out the user needs, firstly the main functions translated from these needs and the subject of the program. Then, I look into additional context for any high-level functions that would need to be included. For this projecet, the needs given for a subject of "inventory" was straightforward: we NEED a user system, and we NEED to be able to add and remove items, and update their counts and be able to view them as well. The additional context need here was the inclusion of low inventory notifications. This was translated technically into a SMS message that sends to the device when any item is lower than 10 in their count. Additionally, I noted this need has another context to include the check of SMS permissions for Android.
With needs in mind, I then make crude but effficient graphs or diagrams for each code "Object" that will maintain and operate the codebase. I typically start with the Model objects so I know the foundations of the data we are working with. Then, we move into related functionality. This process moves next to Views, Controllers, etc.

# How did I test the code to make sure it runs

While I would have liked to be more advanced and write useful JUnit tests and more for both data operations and UI operations, I simply went with the traditional "build and run" method, reading logs as needed when errors arose. I made good use of the Android Logcat system to help me troubleshoot whether or not my own functions were even ever starting. After many hours of this trial and error, I ended up with a usable app.

# Where did you innovate to overcome a challenge?

I would consider this personal innovation, as it is not something I did that would be new in the industry, but going with the option of incorporating the Room library for local database methods and writing a background thread Executor for this library was sosmething I had dedcided on to help me achieve the database results I wanted to achieve with this app.

# What specific component demonstrated your expertise?

I believe I showcased my experience with developing object-oriented data systems and controllers well with this app. Connected directly to a recyclerView object and using an adapter for the items, I am particularly proud of this string of objects in the codebase and my execution of them. 
