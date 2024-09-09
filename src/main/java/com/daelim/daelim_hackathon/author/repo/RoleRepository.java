package com.daelim.daelim_hackathon.author.repo;

import com.daelim.daelim_hackathon.author.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
