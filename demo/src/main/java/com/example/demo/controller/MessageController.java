package com.example.demo.controller;

import com.example.demo.entity.Message;
import com.example.demo.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping
    public Message sendMessage(
            @RequestBody Message message
    ) {
        return messageService.sendMessage(message);
    }

    @GetMapping
    public List<Message> getMessages() {
        return messageService.getMessages();
    }
}