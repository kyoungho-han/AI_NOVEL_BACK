package com.daelim.daelim_hackathon.author.service;

import com.daelim.daelim_hackathon.author.domain.Role;
import com.daelim.daelim_hackathon.author.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.stream.IntStream;

@SpringBootTest
public class AuthorServiceTests {

    @Autowired
    private AuthorService authorService;

    @Test
    public void 유저_추가() {
        IntStream.rangeClosed(1, 10).forEach(i -> {
            authorService.saveUser(new User(
                    null,
                    "username" + i,
                    "password" + i,
                    "name" + i,
                    new ArrayList<>())
            );
        });
    }

    @Test
    public void 롤_추가() {
        authorService.saveRole(new Role(null, "ROLE_USER"));
//        authorService.saveRole(new Role(null, "ROLE_MANAGER"));
    }

    @Test
    public void 유저_롤_추가() {
        IntStream.rangeClosed(1, 10).forEach(i -> {
//            User user = authorService.getUser("username" + i);
//            if (user.isCounselor()) userService.addRoleToUser("username" + i,"ROLE_MANAGER");
            authorService.addRoleToUser("username" + i,"ROLE_USER");
        });
    }
}
