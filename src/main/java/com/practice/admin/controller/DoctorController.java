package com.practice.admin.controller;

import com.practice.admin.entity.User;
import com.practice.admin.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class DoctorController {

    @Autowired
    private UserRepository userRepository;

    // ================= PAGE =================
    @GetMapping("/doctors")
    public String doctors(
            Model model,
            HttpSession session) {

        // check login
        if (session.getAttribute("admin") == null) {

            return "redirect:/login";
        }

        // chỉ lấy doctor
        model.addAttribute(
                "doctors",
                userRepository.findByRole("DOCTOR"));

        return "doctors";
    }

    // ================= SAVE =================
    @PostMapping("/doctors/save")
    public String saveDoctor(User user) {

        user.setRole("DOCTOR");

        userRepository.save(user);

        return "redirect:/doctors";
    }

    // ================= DELETE =================
    @GetMapping("/doctors/delete/{id}")
    public String deleteDoctor(
            @PathVariable Long id) {

        userRepository.deleteById(id);

        return "redirect:/doctors";
    }

    // ================= UPDATE =================
    @PostMapping("/doctors/update")
    public String updateDoctor(User user) {

        User existingDoctor = userRepository.findById(user.getId())
                .orElse(null);

        if (existingDoctor != null) {

            existingDoctor.setName(
                    user.getName());

            existingDoctor.setEmail(
                    user.getEmail());

            existingDoctor.setRole("DOCTOR");

            // password
            if (user.getPassword() != null &&
                    !user.getPassword().isEmpty()) {

                existingDoctor.setPassword(
                        user.getPassword());
            }

            userRepository.save(existingDoctor);
        }

        return "redirect:/doctors";
    }
}