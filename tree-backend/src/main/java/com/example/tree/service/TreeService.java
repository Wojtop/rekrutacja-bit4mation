package com.example.tree.service;

import com.example.tree.entieties.Node;
import com.example.tree.rest.TreeRequest;

import java.util.Map;

public interface TreeService {
    Node addNode(TreeRequest request);
    Node editNode(TreeRequest request);
    void deleteNode(int nodeId);
    public Iterable<Node> findAll();
    public Map<Integer, Iterable<Integer>> getAllRelations();
    public Map<Integer, Integer> sumAllPaths();

}
