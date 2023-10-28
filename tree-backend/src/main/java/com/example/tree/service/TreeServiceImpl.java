package com.example.tree.service;

import com.example.tree.entieties.Connection;
import com.example.tree.entieties.Node;
import com.example.tree.repositories.ConnectionRepository;
import com.example.tree.repositories.NodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TreeServiceImpl implements TreeService {
    private final NodeRepository nodeRepository;
    private final ConnectionRepository connectionRepository;

    public TreeServiceImpl(@Autowired NodeRepository rep, @Autowired ConnectionRepository connRep) {
        nodeRepository = rep;
        connectionRepository = connRep;
    }

    public Node save(Node node) {
        return nodeRepository.save(node);
    }

    @Override
    public Connection save(Connection connection) {
        return connectionRepository.save(connection);
    }
//
//    @Override
//    public Iterable<Node> findAll() {
//        return repository.findAll();
//    }
//
//    @Override
//    public Set<Node> findAllTrees() {
//        return repository.findNodesByParentIsNull();
//    }
//
//    @Override
//    public Node update(Node n) {
//        Optional<Node> founded = repository.findById(n.getId());
//        founded.ifPresent((f) -> {
//            f = n;
//            f = repository.save(f);
//        });
//        return founded.orElse(null);
//    }
//
//    @Override
//    public Optional<Node> getParent(Node child) {
//        if (child.getParent() != null) {
//            return repository.findById(child.getParent().getId());
//        }
//        return Optional.empty();
//    }
//
//    @Override
//    public void deleteById(int id) {
//        repository.deleteById(id);
//    }

}
