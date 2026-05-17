package com.practice.admin.controller;

import com.practice.admin.entity.Admin;
import com.practice.admin.repository.AdminRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    @Autowired
    private AdminRepository adminRepository;

    // ================= LOGIN PAGE =================
    @GetMapping("/login")
    public String loginPage() {

        return "login";
    }

    // ================= LOGIN PROCESS =================
    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model
    ) {

        Admin admin =
                adminRepository.findByUsernameAndPassword(
                        username,
                        password
                );

        if (admin != null) {

            session.setAttribute(
                    "admin",
                    admin.getUsername()
            );

            return "redirect:/";
        }

        model.addAttribute(
                "error",
                "Invalid username or password"
        );

        return "login";
    }

    // ================= LOGOUT =================
    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/login";
    }
}