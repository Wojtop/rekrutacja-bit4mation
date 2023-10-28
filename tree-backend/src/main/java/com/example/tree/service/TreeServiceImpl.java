package com.example.tree.service;

import com.example.tree.entieties.Connection;
import com.example.tree.entieties.Node;
import com.example.tree.repositories.ConnectionRepository;
import com.example.tree.repositories.NodeRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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

    @Override
    public Optional<Node> findById(int nodeId) {
        return nodeRepository.findById(nodeId);
    }

    @Override
    public Iterable<Node> findAll() {
//        Set extends Collection which extends Iterable - safe casting
        return nodeRepository.findAll();
    }

    @Override
    public Map<Integer, Iterable<Integer>> getAllRelations() {
        Iterable<Connection> connections = connectionRepository.findAll();
        Map<Integer, Set<Integer>> connectionsMap = new HashMap<>();
        for (Connection connection : connections) {
            Set<Integer> nodeChildren = connectionsMap.computeIfAbsent(connection.getParent().getId(), k -> new HashSet<>());
            nodeChildren.add(connection.getChild().getId());
        }
        Map<Integer, Iterable<Integer>> converted = new HashMap<>();
        for (Integer k : connectionsMap.keySet()) {
            converted.put(k, (Iterable<Integer>) connectionsMap.get(k));
        }
        return converted;
    }

    @Override
    public Set<Connection> findAllChildren(int parentId) {
        return connectionRepository.findAllByParentId(parentId);
    }

    @AllArgsConstructor
    @Getter
    private static class Leaf {
        private final int lastChildId;
        private int sum = 0;

        public void incrementSum(int value) {
            sum += value;
        }
    }

    @Override
    public Map<Integer, Integer> sumAllPaths() {
        List<Node> roots = nodeRepository.findAllRoots();
        Map<Integer, Integer> result = new HashMap<>();
        for (Node root : roots) {
            List<Leaf> sums = sumChildren(root);
            for(Leaf l : sums){
                result.put(l.lastChildId, l.sum);
            }
        }
        return result;
    }

    private List<Leaf> sumChildren(Node node) {
        Set<Connection> children = findAllChildren(node.getId());
        List<Leaf> result = null;
        if (children.isEmpty()) {
            result = new ArrayList<>(1);
            result.add(new Leaf(node.getId(), node.getValue()));
            return result;
        } else {
            result = new ArrayList<>();
            for (Connection child : children) {
                List<Leaf> childSums = sumChildren(child.getChild());
                for (Leaf l : childSums) {
                    l.incrementSum(node.getValue());
                }
                result.addAll(childSums);
            }
            return result;
        }
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
