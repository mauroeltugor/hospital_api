package com.example.myapp.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.myapp.entity.User;
import com.example.myapp.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void updateUserStatus(Integer id, boolean isEnabled) throws Exception {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setIsActivated(isEnabled);
            userRepository.save(user);
        } else {
            throw new Exception("Usuario no encontrado");
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
