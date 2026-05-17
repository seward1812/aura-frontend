package com.practice.admin.controller;

import com.practice.admin.repository.AIAnalysisRepository;
import com.practice.admin.repository.AppointmentRepository;
import com.practice.admin.repository.ClinicRepository;
import com.practice.admin.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private AppointmentRepository appointmentRepository;

        @Autowired
        private ClinicRepository clinicRepository;

        // BUG FIX: Đã chuyển @Autowired aiAnalysisRepository lên đúng vị trí field
        // (trước đây bị đặt sau closing brace của method)
        @Autowired
        private AIAnalysisRepository aiAnalysisRepository;

        @GetMapping("/")
        public String dashboard(
                        Model model,
                        HttpSession session) {

                // kiểm tra đăng nhập admin
                if (session.getAttribute("admin") == null) {

                        return "redirect:/login";
                }

                // tổng users
                model.addAttribute(
                                "totalUsers",
                                userRepository.count());

                // tổng appointments
                model.addAttribute(
                                "totalAppointments",
                                appointmentRepository.count());

                // tổng clinics
                model.addAttribute(
                                "totalClinics",
                                clinicRepository.count());

                // tổng AI analyses
                model.addAttribute(
                                "totalAnalyses",
                                aiAnalysisRepository.count());

                return "index";
        }
}