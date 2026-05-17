package com.practice.admin.controller;

import com.practice.admin.entity.User;
import com.practice.admin.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // ================= USERS PAGE =================
    @GetMapping("/users")
    public String users(
            Model model,
            HttpSession session
    ) {

        // kiểm tra admin login
        if (session.getAttribute("admin") == null) {

            return "redirect:/login";
        }

        model.addAttribute(
                "users",
                userRepository.findAll()
        );

        return "users";
    }

    // ================= SAVE USER =================
    @PostMapping("/users/save")
    public String saveUser(
            User user,
            HttpSession session
    ) {

        // mặc định role
        if (user.getRole() == null ||
            user.getRole().isEmpty()) {

            user.setRole("PATIENT");
        }

        userRepository.save(user);

        return "redirect:/users";
    }

    // ================= DELETE USER =================
    @GetMapping("/users/delete/{id}")
    public String deleteUser(
            @PathVariable Long id
    ) {

        userRepository.deleteById(id);

        return "redirect:/users";
    }

    // ================= UPDATE USER =================
    @PostMapping("/users/update")
    public String updateUser(User user) {

        User existingUser =
                userRepository.findById(user.getId())
                        .orElse(null);

        if (existingUser != null) {

            // update name
            existingUser.setName(
                    user.getName()
            );

            // update email
            existingUser.setEmail(
                    user.getEmail()
            );

            // update role
            existingUser.setRole(
                    user.getRole()
            );

            // update password nếu có nhập
            if (user.getPassword() != null &&
                !user.getPassword().isEmpty()) {

                existingUser.setPassword(
                        user.getPassword()
                );
            }

            userRepository.save(existingUser);
        }

        return "redirect:/users";
    }
}