package com.example.tree.service;

import com.example.tree.entieties.Node;
import jakarta.websocket.OnClose;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TreeServiceImpl implements TreeService {
    private TreeRepository repository;

    public TreeServiceImpl(@Autowired TreeRepository rep) {
        repository = rep;
    }

    public Node save(Node node) {
        return repository.save(node);
    }

    @Override
    public Iterable<Node> findAll() {
        return repository.findAll();
    }

    @Override
    public Set<Node> findAllTrees() {
        return repository.findNodesByParentIsNull();
    }

    @Override
    public Node update(Node n) {
        Optional<Node> founded = repository.findById(n.getId());
        founded.ifPresent((f) -> {
            f = n;
            f = repository.save(f);
        });
        return founded.orElse(null);
    }

    @Override
    public Optional<Node> getParent(Node child) {
        if (child.getParent() != null) {
            return repository.findById(child.getParent().getId());
        }
        return Optional.empty();
    }

    @Override
    public void deleteById(int id) {
        repository.deleteById(id);
    }

}
