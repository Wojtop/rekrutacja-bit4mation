package com.example.tree.service;

import com.example.tree.entieties.Connection;
import com.example.tree.entieties.Node;
import com.example.tree.rest.TreeRequest;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface TreeService {
    void addNode(TreeRequest request);
    void editNode(TreeRequest request);
    void deleteNode(int nodeId);
    public Iterable<Node> findAll();
    public Map<Integer, Iterable<Integer>> getAllRelations();
    public Map<Integer, Integer> sumAllPaths();

}
