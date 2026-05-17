package com.practice.admin.service;

import com.practice.admin.entity.User;
import com.practice.admin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // GET ALL
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ADD USER
    public User addUser(User user) {
        return userRepository.save(user);
    }

    // DELETE USER
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    

}
