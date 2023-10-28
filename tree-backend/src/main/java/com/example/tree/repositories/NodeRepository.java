package com.example.tree.repositories;

import com.example.tree.entieties.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodeRepository extends JpaRepository<Node, Integer> {
    @Query("SELECT n FROM Node n WHERE n.id not in (SELECT c.child.id from Connection c)")
    public List<Node>findAllRoots();
}
