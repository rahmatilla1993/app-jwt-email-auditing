package com.example.appjwtemailauditing.repository;

import com.example.appjwtemailauditing.entity.Roles;
import com.example.appjwtemailauditing.entity.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Roles,Integer> {

    Roles findByName(RoleName name);
}
