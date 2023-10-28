package com.example.tree.service;

import com.example.tree.entieties.Node;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TreeService {
    public Node save(Node node);
    public Iterable<Node> findAll();

    public Set<Node> findAllTrees();

    Node update(Node parent);
    public Optional<Node> getParent(Node child);
    public void deleteById(int id);
}
