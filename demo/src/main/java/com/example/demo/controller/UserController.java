package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public User createUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    @PostMapping("/login")
    public User login(@RequestBody User user) {

        return userService.login(
                user.getEmail(),
                user.getPassword()
        );
    }

    @PutMapping("/{id}")
    public User updateUser(
            @PathVariable Long id,
            @RequestBody User user
    ) {
        return userService.updateUser(id, user);
    }

    @GetMapping("/search")
    public List<User> searchUsers(
            @RequestParam String name
    ) {
        return userService.searchUsers(name);
    }

    @GetMapping("/role")
    public ResponseEntity<List<User>> getUsersByRole(@RequestParam String role) {
        return ResponseEntity.ok(userRepository.findByRole(role));
    }

    /**
     * Cập nhật trạng thái Khóa / Mở khóa tài khoản
     * PUT http://localhost:8080/api/users/{id}/status?isActive=false
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long id, @RequestParam Boolean isActive) {
        return userRepository.findById(id).map(user -> {
            user.setIsActive(isActive);
            userRepository.save(user);
            return ResponseEntity.ok(user);
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Xóa vĩnh viễn tài khoản khỏi hệ thống CSDL
     * DELETE http://localhost:8080/api/users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            if (userRepository.existsById(id)) {
                userRepository.deleteById(id);
                return ResponseEntity.ok("Đã xóa tài khoản thành công!");
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Không thể xóa tài khoản do có ràng buộc dữ liệu: " + e.getMessage());
        }
    }
}