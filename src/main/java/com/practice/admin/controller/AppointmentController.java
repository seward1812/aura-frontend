package com.practice.admin.controller;

import com.practice.admin.entity.Appointment;
import com.practice.admin.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AppointmentController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    // ================= LIST =================
    @GetMapping("/appointments")
    public String appointments(Model model) {

        model.addAttribute(
                "appointments",
                appointmentRepository.findAll()
        );

        return "appointments";
    }

    // ================= SAVE =================
    @PostMapping("/appointments/save")
    public String saveAppointment(Appointment appointment) {

        appointment.setStatus("Pending");

        appointmentRepository.save(appointment);

        return "redirect:/appointments";
    }

    // ================= DELETE =================
    @GetMapping("/appointments/delete/{id}")
    public String deleteAppointment(@PathVariable Long id) {

        appointmentRepository.deleteById(id);

        return "redirect:/appointments";
    }

    // ================= APPROVE =================
    @GetMapping("/appointments/approve/{id}")
    public String approveAppointment(@PathVariable Long id) {

        Appointment appointment =
                appointmentRepository.findById(id).orElse(null);

        if (appointment != null) {

            appointment.setStatus("Approved");

            appointmentRepository.save(appointment);
        }

        return "redirect:/appointments";
    }

    // ================= CANCEL =================
    @GetMapping("/appointments/cancel/{id}")
    public String cancelAppointment(@PathVariable Long id) {

        Appointment appointment =
                appointmentRepository.findById(id).orElse(null);

        if (appointment != null) {

            appointment.setStatus("Cancelled");

            appointmentRepository.save(appointment);
        }

        return "redirect:/appointments";
    }
}