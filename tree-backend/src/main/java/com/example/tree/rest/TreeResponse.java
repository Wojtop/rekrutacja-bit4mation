package com.example.tree.rest;

import com.example.tree.entieties.Node;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @param references List of all nodes in tree
 * @param relations  Map holding ids of parent and child nodes. <br>{@code Map<parentId, Set of children ids>}. Note that <b>if some root has not children it will not be visible here</b>.
 * @param leafs      Map holding values to put in leafs. Leafs are not stored in DB directly. They are calculated with every request.<br>
 *                   {@code Map<ID of last child node in branch, sum of all values in branch from root>}
 * @param modified   Node modified or created in update or add operation. Default <b>NULL</b>
 */
@Builder
public record TreeResponse(Iterable<Node> references, Map<Integer, Set<Integer>> relations, Map<Integer, Integer> leafs, Node modified) {
}
