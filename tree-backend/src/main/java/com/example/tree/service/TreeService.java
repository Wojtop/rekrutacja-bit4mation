package com.example.tree.service;

import com.example.tree.entieties.Connection;
import com.example.tree.entieties.Node;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface TreeService {
    public Node save(Node node);
    public Connection save(Connection connection);
    public Optional<Node> findById(int nodeId);
    public Iterable<Node> findAll();
    public Map<Integer, Iterable<Integer>> getAllRelations();
    public Optional<Connection> findChildByChildId(int childId);
//    public Connection updateParent(Connection updatedConnection, int newParentId);
    Optional<Node> findNodeById(int id);
    void makeNodeRoot(int nodeId);
    void deleteNode(int nodeId);

    /**
     * @param parentId ID of parent node
     * @return Set of all connections where parent is pointed node
     */
    public Set<Connection> findAllChildren(int parentId);
    public Map<Integer, Integer> sumAllPaths();

}
