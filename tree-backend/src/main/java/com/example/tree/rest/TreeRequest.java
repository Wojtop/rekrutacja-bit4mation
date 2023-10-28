package com.example.tree.rest;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

/**
 * @param nodeId   ID of node associated with request.
 *                 <b>NULL</b> if request is new node creation.
 * @param value    Value of edited/created node
 * @param parentId ID of node parent node.
 *                 <b>NULL</b> if node is new root.
 */
public record TreeRequest(@Nullable Integer nodeId, @NotNull Integer value,@Nullable Integer parentId) {
}
