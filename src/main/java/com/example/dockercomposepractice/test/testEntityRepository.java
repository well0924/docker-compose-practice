package com.example.dockercomposepractice.test;

import org.springframework.data.jpa.repository.JpaRepository;

public interface testEntityRepository extends JpaRepository<testEntity,Long> {

    testEntity findByName(String name);
}
