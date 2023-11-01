package com.example.tree.rest;

import com.example.tree.entieties.Node;
import com.example.tree.service.TreeService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("tree")
@Slf4j
public class TreeController {
    private final TreeService service;

    public TreeController(@Autowired TreeService service) {
        this.service = service;
    }

    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Data changed"),
                    @ApiResponse(code = 500, message = "Unexpected server error")
            }
    )
    @Operation(summary = "Get trees", description = "Return all trees accessible in application")
    @GetMapping(value = "getTree", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TreeResponse> getTree() {
        return new ResponseEntity<>(buildResponse(), HttpStatus.OK);
    }

    @Operation(summary = "Create new node",
            description = "Operation allows to create new node. As node is created and not exists in DB yet, its ID in request must be null"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Data changed"),

                    @ApiResponse(code = 400, message = "Error in request data"),
                    @ApiResponse(code = 500, message = "Unexpected server error")
            }
    )
    @PostMapping(value = "/addNode", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TreeResponse> addNode(
            @Parameter(required = true, description = "New node data")
            @RequestBody TreeRequest request) {
        Node newNode = service.addNode(request);
        return new ResponseEntity<>(buildResponse(newNode), HttpStatus.CREATED);
    }

    @Operation(summary = "Change node", description = "Operation allows to change node value or parent. Setting parentId as null makes node a new root. Change do not affect child nodes")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Data changed"),

                    @ApiResponse(code = 400, message = "Error in request data"),
                    @ApiResponse(code = 500, message = "Unexpected server error")
            }
    )
    @PutMapping(value = "/editNode", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TreeResponse> editNode(
            @Parameter(required = true, description = "Edited node data")
            @RequestBody TreeRequest request) {
        Node modified = service.editNode(request);
        return new ResponseEntity<>(buildResponse(modified), HttpStatus.OK);
    }

    @Operation(summary = "Delete node", description = "Operation deletes node and all it's children")
    @DeleteMapping("/deleteNode/{id}")
    @ApiResponse(code = 200, message = "Node deleted or no data to delete")
    public ResponseEntity<TreeResponse> deleteNode(
            @Parameter(description = "ID of node to delete", required = true)
            @PathVariable
            int id) {
        service.deleteNode(id);
        return new ResponseEntity<>(buildResponse(), HttpStatus.OK);
    }

    private TreeResponse buildResponse() {
        return TreeResponse.builder()
                .references(service.findAll())
                .relations(service.getAllRelations())
                .leafs(service.sumAllPaths())
                .modified(null)
                .build();
    }

    private TreeResponse buildResponse(Node modified) {
        return TreeResponse.builder()
                .references(service.findAll())
                .relations(service.getAllRelations())
                .leafs(service.sumAllPaths())
                .modified(modified)
                .build();
    }
}
