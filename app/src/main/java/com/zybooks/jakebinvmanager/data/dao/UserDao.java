package com.zybooks.jakebinvmanager.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.zybooks.jakebinvmanager.data.model.User;

import java.util.List;

@Dao
public interface UserDao {

    // Insert a new user
    @Insert
    void insertUser(User user);

    // Update an existing user
    @Update
    void updateUser(User user);

    // Delete a user
    @Delete
    void deleteUser(User user);

    // Get a user by ID
    @Query("SELECT * FROM users WHERE id = :userId")
    User getUserById(int userId);

    // Get all users
    @Query("SELECT * FROM users")
    List<User> getAllUsers();

    // Get a user by username
    @Query("SELECT * FROM users WHERE username = :username")
    User getUserByUsername(String username);

    // Get users by role
    @Query("SELECT * FROM users WHERE role = :role")
    List<User> getUsersByRole(String role);
}