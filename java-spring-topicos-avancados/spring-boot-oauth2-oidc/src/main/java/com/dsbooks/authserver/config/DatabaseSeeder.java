package com.dsbooks.authserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import com.dsbooks.authserver.entities.UserEntity;
import com.dsbooks.authserver.repositories.UserRepository;

@Configuration
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }
        seedUser("a1b2c3d4-e5f6-7890-abcd-ef1234567890",
                "maria@example.com", "Maria Silva",
                "https://i.pravatar.cc/300?img=1");

        seedUser("b2c3d4e5-f6a7-8901-bcde-f12345678901",
                "alex@example.com", "Alex Smith",
                "https://i.pravatar.cc/300?img=2");
    }

    private void seedUser(String sub, String email, String name, String picture) {
        UserEntity user = new UserEntity();
        user.setSub(sub);
        user.setEmail(email);
        user.setEmailVerified(false);
        user.setPassword("{noop}12345678");
        user.setName(name);
        user.setPicture(picture);
        userRepository.save(user);
    }
}
