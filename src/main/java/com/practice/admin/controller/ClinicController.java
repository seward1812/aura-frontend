package com.practice.admin.controller;

import com.practice.admin.entity.Clinic;
import com.practice.admin.repository.ClinicRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ClinicController {

    @Autowired
    private ClinicRepository clinicRepository;

    // ================= PAGE =================
    @GetMapping("/clinics")
    public String clinics(Model model) {

        model.addAttribute(
                "clinics",
                clinicRepository.findAll()
        );

        return "clinics";
    }

    // ================= SAVE =================
    @PostMapping("/clinics/save")
    public String saveClinic(Clinic clinic) {

        if (clinic.getStatus() == null ||
            clinic.getStatus().isEmpty()) {

            clinic.setStatus("Active");
        }

        clinicRepository.save(clinic);

        return "redirect:/clinics";
    }

    // ================= DELETE =================
    @GetMapping("/clinics/delete/{id}")
    public String deleteClinic(@PathVariable Long id) {

        clinicRepository.deleteById(id);

        return "redirect:/clinics";
    }

    // ================= APPROVE =================
    @GetMapping("/clinics/approve/{id}")
    public String approveClinic(@PathVariable Long id) {

        Clinic clinic =
                clinicRepository.findById(id).orElse(null);

        if (clinic != null) {

            clinic.setStatus("Approved");

            clinicRepository.save(clinic);
        }

        return "redirect:/clinics";
    }

    // ================= SUSPEND =================
    @GetMapping("/clinics/suspend/{id}")
    public String suspendClinic(@PathVariable Long id) {

        Clinic clinic =
                clinicRepository.findById(id).orElse(null);

        if (clinic != null) {

            clinic.setStatus("Suspended");

            clinicRepository.save(clinic);
        }

        return "redirect:/clinics";
    }
}