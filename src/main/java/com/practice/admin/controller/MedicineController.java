package com.practice.admin.controller;

import com.practice.admin.entity.Medicine;
import com.practice.admin.repository.MedicineRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MedicineController {

    @Autowired
    private MedicineRepository medicineRepository;

    // ================= PAGE =================
    @GetMapping("/medicines")
    public String medicines(Model model) {

        model.addAttribute(
                "medicines",
                medicineRepository.findAll()
        );

        return "medicines";
    }

    // ================= SAVE =================
    @PostMapping("/medicines/save")
    public String saveMedicine(Medicine medicine) {

        medicineRepository.save(medicine);

        return "redirect:/medicines";
    }

    // ================= DELETE =================
    @GetMapping("/medicines/delete/{id}")
    public String deleteMedicine(@PathVariable Long id) {

        medicineRepository.deleteById(id);

        return "redirect:/medicines";
    }
}