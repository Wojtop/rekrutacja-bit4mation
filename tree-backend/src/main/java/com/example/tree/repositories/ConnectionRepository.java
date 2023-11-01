package com.example.tree.repositories;

import com.example.tree.entieties.Connection;
import com.example.tree.entieties.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, Integer> {
    /**
     * All connections where {@code parentId} is a parent
     * @param parentId ID of parent node
     * @return All connections where {@code parentId} is a parent
     */
    Set<Connection> findAllByParentId(int parentId);

    /**
     * Connection where child has {@code childId}
     * @param childId ID of searched child
     * @return Connection where child has {@code childId}
     */
    Optional<Connection> findByChildId(int childId);

    /**
     * Delete connection with given {@code childId}
     * @param childId ID of child whose connection should be deleted
     */
    void deleteByChildId(int childId);
}
