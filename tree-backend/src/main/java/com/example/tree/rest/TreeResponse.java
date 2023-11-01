package com.example.tree.rest;

import com.example.tree.entieties.Node;
import io.swagger.v3.oas.annotations.media.Schema;
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
public record TreeResponse(
        @Schema(description = "List of all nodes in tree",required = true)
        Iterable<Node> references,
        @Schema(description = "Map holding ids of parent and child nodes. Keys: node ID, values: set of children nodes ids. Note that if some root has not children it will not be visible here.", required = true )
        Map<Integer, Set<Integer>> relations,
        @Schema(description = "Map holding values to put in leafs. Leafs are not stored in DB directly. They are calculated with every request.<br>\n" +
                "Key: ID of last child node in branch, value: sum of all values in branch from root", required = true)
        Map<Integer, Integer> leafs,
        @Schema(description = "Value of created or modified node consistent with database state. Returned only for create and update operations", required = false)
        Node modified) {
}
