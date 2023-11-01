package com.example.tree.rest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

/**
 * @param nodeId   ID of node associated with request.
 *                 <b>NULL</b> only if request is new node creation.
 * @param value    New value of edited/created node
 * @param parentId ID of parent node.
 *                 <b>NULL</b> if node is new root.
 */
public record TreeRequest(
        @Nullable
        @Schema(example = "1", description = "ID of node associated with request.\n" +
                "NULL only if request is new node creation.", required = false)
        Integer nodeId,
        @Schema(required = true, example = "10", description = "Integer value stored in node")
        @NotNull Integer value,
        @Schema(required = false, description = "ID of parent node.\n" +
                "NULL if node is new root.", example = "1")
        @Nullable Integer parentId
) {

}
