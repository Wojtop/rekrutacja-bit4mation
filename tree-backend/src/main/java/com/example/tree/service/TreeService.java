package com.example.tree.service;

import com.example.tree.entieties.Node;
import com.example.tree.rest.TreeRequest;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TreeService {
    /**
     * Creates new node.
     * Null parentId makes it new root.
     * If parentId is not null parent node must exist. Otherwise {@link com.example.tree.exceptions.TreeException} is thrown.
     * If value is null the same exception is thrown
     * @param request New node data
     * @return Inserted node with its ID
     * @see TreeRequest
     */
    Node addNode(TreeRequest request);

    /**
     * Edits node passed in parameter. If node was child and parentId is passed as NULL, node is set as new root. It's child nodes became unmodified (still remain node children).
     * Node with given ID must exist. Otherwise {@link com.example.tree.exceptions.TreeException} is thrown.
     * If value is null the same exception is thrown
     * @param request Node with new values.
     * @return Edited node
     */
    Node editNode(TreeRequest request);

    /**
     * Delete node with given ID and all it's children (children of children are removed recursively)
     * @param nodeId ID of node to delete
     */
    void deleteNode(int nodeId);

    /**
     * Return all nodes
     * @return List of all nodes in all trees
     */
    public List<Node> findAll();

    /**
     * Return map of parents and their child nodes. Note that <b>if some root has not children it will not be visible here</b>.
     * @return Map holding ids of parent and child nodes. <br>{@code Map<parentId, Set of children ids>}
     */
    public Map<Integer, Set<Integer>> getAllRelations();

    /**
     * For each tree return sum of nodes for each path. Single root node without children is also considered as a tree,
     * @return Map in format {@code Map<ID of last node in path, sum of nodes in that path>}
     */
    public Map<Integer, Integer> sumAllPaths();

}
