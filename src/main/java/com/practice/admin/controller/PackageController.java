package com.practice.admin.controller;

import com.practice.admin.entity.PackageEntity;
import com.practice.admin.repository.PackageRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PackageController {

    @Autowired
    private PackageRepository packageRepository;

    // ===== PAGE =====
    @GetMapping("/packages")
    public String packages(Model model) {

        model.addAttribute(
                "packages",
                packageRepository.findAll()
        );

        return "packages";
    }

    // ===== SAVE =====
    @PostMapping("/packages/save")
    public String savePackage(PackageEntity packageEntity) {

        packageRepository.save(packageEntity);

        return "redirect:/packages";
    }

    // ===== DELETE =====
    @GetMapping("/packages/delete/{id}")
    public String deletePackage(@PathVariable Long id) {

        packageRepository.deleteById(id);

        return "redirect:/packages";
    }
}