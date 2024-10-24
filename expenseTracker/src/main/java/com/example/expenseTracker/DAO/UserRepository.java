package com.example.expenseTracker.dao;

import com.example.expenseTracker.models.User;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    User findByEmailAddress(String emailAddress);
}