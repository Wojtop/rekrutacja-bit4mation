package com.example.tree.repositories;

import com.example.tree.entieties.Connection;
import com.example.tree.entieties.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, Integer> {
    Set<Connection> findAllByParentId(int parentId);
}
