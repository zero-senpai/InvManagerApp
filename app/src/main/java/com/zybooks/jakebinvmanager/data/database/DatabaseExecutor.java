package com.zybooks.jakebinvmanager.data.database;

import android.content.Context;
import android.util.Log;

import com.zybooks.jakebinvmanager.data.dao.ItemDao;
import com.zybooks.jakebinvmanager.data.dao.UserDao;
import com.zybooks.jakebinvmanager.data.model.Item;
import com.zybooks.jakebinvmanager.data.model.User;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DatabaseExecutor {

    private static DatabaseExecutor instance;  // Singleton instance
    private static final Executor executor = Executors.newFixedThreadPool(4);  // Executor to run tasks in background
    private final UserDao userDao;
    private final ItemDao itemDao;

    // Private constructor to prevent direct instantiation
    public DatabaseExecutor(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        this.userDao = database.userDao();
        this.itemDao = database.itemDao();
    }

    // Public method to get the singleton instance of DatabaseExecutor
    public static synchronized DatabaseExecutor getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseExecutor(context.getApplicationContext());  // Using applicationContext to avoid memory leaks
        }
        return instance;
    }

    // Method to query user by username
    public void getUserByUsername(final String username, final UserCallback callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                User user = userDao.getUserByUsername(username);  // Get the user from the database by username
                if (user != null) {
                    callback.onUserFetched(user);
                } else {
                    callback.onUserFetchedFailed();
                }
            }
        });
    }

    // Sign-up functionality
    public void signUpUser(final User user, final SignUpCallback callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    userDao.insertUser(user);
                    callback.onSignUpSuccess();
                } catch (Exception e) {
                    callback.onSignUpFailed();
                }
            }
        });
    }

    // Login functionality
    public void loginUser(final String username, final String password, final LoginCallback callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                // Query the user by username
                User user = userDao.getUserByUsername(username);

                Log.d("Login", "Checking user: " + username);

                if (user != null) {
                    Log.d("Login", "User found: " + user.getUsername());
                    // Check if password matches
                    if (user.getPassword().equals(password)) {
                        Log.d("Login", "Password matches.");
                        // Correct username and password, login success
                        callback.onLoginSuccess(user);
                    } else {
                        Log.d("Login", "Password does not match.");
                        // Incorrect password, login failed
                        callback.onLoginFailed();
                    }
                } else {
                    Log.d("Login", "User not found.");
                    // User not found, login failed
                    callback.onLoginFailed();
                }
            }
        });
    }

    // Create a new user
    public void createUser(final User user, final TestUserCallback callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // Insert the user into the database (Assuming you have a DAO method for this)
                    userDao.insertUser(user);
                    callback.onUserCreated();  // Notify success
                } catch (Exception e) {
                    callback.onUserCreationFailed();  // Notify failure
                }
            }
        });
    }

    // Fetch all items for display
    public void getItems(final ItemCallback callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.onItemsFetched(itemDao.getAllItems());
                } catch (Exception e) {
                    callback.onItemsFetchedFailed();
                }
            }
        });
    }

    // Method to create an item
    public void createItem(Item item, ItemCreationCallback callback) {
        executor.execute(() -> {
            try {
                itemDao.insertItem(item); // Ensure this method matches your DAO implementation
                Log.d("DatabaseExecutor", "Item inserted: " + item.toString());
                callback.onItemCreated();
            } catch (Exception e) {
                Log.e("DatabaseExecutor", "Error inserting item", e);
                callback.onItemCreationFailed();
            }
        });
    }



    // Callback interfaces
    public interface SignUpCallback {
        void onSignUpSuccess();
        void onSignUpFailed();
    }

    public interface LoginCallback {
        void onLoginSuccess(User user);
        void onLoginFailed();
    }

    public interface ItemCallback {
        void onItemsFetched(List<Item> items);
        void onItemsFetchedFailed();
    }

    // Callback interface for item creation
    public interface ItemCreationCallback {
        void onItemCreated();
        void onItemCreationFailed();
    }

    public interface UserCallback {
        void onUserFetched(User user);
        void onUserFetchedFailed();
    }

    public interface TestUserCallback {
        void onUserCreated();
        void onUserCreationFailed();
    }
}
