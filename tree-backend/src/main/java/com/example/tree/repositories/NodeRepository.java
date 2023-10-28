package com.example.tree.repositories;

import com.example.tree.entieties.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NodeRepository extends CrudRepository<Node, Integer> {
//    public Set<Node> findNodesByParentIsNull();
}
