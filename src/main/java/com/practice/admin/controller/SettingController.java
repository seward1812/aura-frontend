package com.practice.admin.controller;

import com.practice.admin.entity.Setting;
import com.practice.admin.repository.SettingRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class SettingController {

    @Autowired
    private SettingRepository settingRepository;

    // PAGE
    @GetMapping("/settings")
    public String settings(Model model) {

        model.addAttribute(
                "settings",
                settingRepository.findAll()
        );

        return "settings";
    }

    // SAVE
    @PostMapping("/settings/save")
    public String saveSetting(Setting setting) {

        settingRepository.save(setting);

        return "redirect:/settings";
    }

    // DELETE
    @GetMapping("/settings/delete/{id}")
    public String deleteSetting(@PathVariable Long id) {

        settingRepository.deleteById(id);

        return "redirect:/settings";
    }
}