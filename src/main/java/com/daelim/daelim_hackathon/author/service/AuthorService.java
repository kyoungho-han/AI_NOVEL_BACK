package com.daelim.daelim_hackathon.author.service;



import com.daelim.daelim_hackathon.author.domain.Role;
import com.daelim.daelim_hackathon.author.domain.User;
import com.daelim.daelim_hackathon.author.dto.LoginDTO;

import java.util.List;

public interface AuthorService {
    LoginDTO saveUser(User user);
    Role saveRole(Role role);
    void addRoleToUser(String username, String roleName);
    User getUser(String username);
    List<User> getUsers();
}
