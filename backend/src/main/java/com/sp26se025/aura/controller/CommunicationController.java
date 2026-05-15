package com.sp26se025.aura.controller;

import com.sp26se025.aura.dto.Requests;
import com.sp26se025.aura.model.Message;
import com.sp26se025.aura.service.InMemoryStore;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
public class CommunicationController {
    private final InMemoryStore store;

    public CommunicationController(InMemoryStore store) {
        this.store = store;
    }

    @PostMapping
    public Message send(@RequestBody Requests.MessageRequest request) {
        Message message = new Message();
        message.setId("msg-" + UUID.randomUUID());
        message.setSenderId(request.senderId());
        message.setReceiverId(request.receiverId());
        message.setBody(request.body());
        return store.saveMessage(message);
    }

    @GetMapping("/thread")
    public List<Message> thread(@RequestParam String participantA, @RequestParam String participantB) {
        return store.messages().stream()
                .filter(m -> (participantA.equals(m.getSenderId()) && participantB.equals(m.getReceiverId()))
                        || (participantB.equals(m.getSenderId()) && participantA.equals(m.getReceiverId())))
                .sorted(Comparator.comparing(Message::getSentAt))
                .toList();
    }
}
