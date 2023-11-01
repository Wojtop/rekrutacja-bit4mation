package com.example.tree.service;

import com.example.tree.entieties.Connection;
import com.example.tree.entieties.Node;
import com.example.tree.exceptions.TreeException;
import com.example.tree.repositories.ConnectionRepository;
import com.example.tree.repositories.NodeRepository;
import com.example.tree.rest.TreeRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class TreeServiceImpl implements TreeService {
    private final NodeRepository nodeRepository;
    private final ConnectionRepository connectionRepository;

    public TreeServiceImpl(@Autowired NodeRepository rep, @Autowired ConnectionRepository connRep) {
        nodeRepository = rep;
        connectionRepository = connRep;
    }

    @Override
    @Transactional
    public Node addNode(TreeRequest request) {
        if(request.nodeId() != null){
            throw new TreeException("New node can not have assigned ID");
        }
        if(request.value() == null){
            throw new TreeException("Value can not be null");
        }

        Node newNode = Node.builder().value(request.value()).build();
        newNode = nodeRepository.save(newNode);

        if (request.parentId() != null) {
            Node finalNewNode = newNode;
            nodeRepository.findById(request.parentId()).ifPresentOrElse((parentNode -> {
                Connection newConnection = new Connection(parentNode, finalNewNode);
                newConnection = connectionRepository.save(newConnection);
                log.info("New node inserted. Node: {}. Connection: {}", finalNewNode, newConnection);
            }), () -> {
                throw new TreeException("Parent node does not exists. Check your tree on refreshed values");
            });

        }
        return newNode;
    }

    @Override
    @Transactional
    public Node editNode(TreeRequest request) {
        if(request.nodeId() == null){
            throw new TreeException("Edited node ID can not be null");
        }
        if(request.value() == null){
            throw new TreeException("Value can not be null");
        }
        Node requestNode = Node.builder().value(request.value()).id(request.nodeId()).build();

        // check if node still exists
        nodeRepository.findById(request.nodeId()).orElseThrow(() -> {
            log.error("No such node: {}", request.nodeId());
            return new TreeException("Node no longer exists. Check refreshed tree");
        });
        requestNode = nodeRepository.save(requestNode);
        log.info("Node {} updated", requestNode);

        if (request.parentId() == null) {
            makeNodeRoot(request.nodeId());
            log.info("Node {} made root", request.nodeId());
        } else {
            Optional<Connection> relation = findChildByChildId(request.nodeId());
            Optional<Node> newParent = nodeRepository.findById(request.parentId());
            Node finalRequestNode = requestNode;
            newParent.ifPresentOrElse(parent -> {
                relation.ifPresentOrElse(rel -> {
                    rel.setParent(parent);
                    connectionRepository.save(rel);
                    log.info("Node {} parent updated in connection: {}", request.nodeId(), rel);
                }, () -> {
                    Connection newConnection = new Connection(parent, finalRequestNode);
                    connectionRepository.save(newConnection);
                });
            }, () -> {
                throw new TreeException("New parent node does not exists. Check refreshed tree");
            });
        }
        return requestNode;
    }

    @Override
    @Transactional
    public void deleteNode(int nodeId) {
        Set<Connection> nodeChildren = connectionRepository.findAllByParentId(nodeId);
        if (nodeChildren.isEmpty()) {
            connectionRepository.deleteByChildId(nodeId);
            nodeRepository.deleteById(nodeId);
            log.info("Node {} deleted", nodeId);
            return;
        }

        for (Connection child : nodeChildren) {
            deleteNode(child.getChild().getId());
        }
        connectionRepository.deleteByChildId(nodeId);
        nodeRepository.deleteById(nodeId);
        log.info("Node {}: children deleted, node deleted", nodeId);
    }

    @Override
    public Map<Integer, Set<Integer>> getAllRelations() {
        Iterable<Connection> connections = connectionRepository.findAll();
        Map<Integer, Set<Integer>> connectionsMap = new HashMap<>();
        for (Connection connection : connections) {
            Set<Integer> nodeChildren = connectionsMap.computeIfAbsent(connection.getParent().getId(), k -> new HashSet<>());
            nodeChildren.add(connection.getChild().getId());
        }
        return connectionsMap;
    }

    private Optional<Connection> findChildByChildId(int childId) {
        return connectionRepository.findByChildId(childId);
    }


    private void makeNodeRoot(int nodeId) {
        connectionRepository.deleteByChildId(nodeId);
    }

    @Override
    public List<Node> findAll() {
        return nodeRepository.findAll();
    }

    //    @Override
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
            for (Leaf l : sums) {
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

}
