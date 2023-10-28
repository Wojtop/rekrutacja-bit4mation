package com.example.tree.rest;

import com.example.tree.entieties.Connection;
import com.example.tree.entieties.Node;
import com.example.tree.service.TreeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("tree")
@Slf4j
public class TreeController {
    private final TreeService service;

    public TreeController(@Autowired TreeService service) {
        this.service = service;
    }

    @GetMapping(value = "getTree", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TreeResponse> getTree() {
        return new ResponseEntity<>(buildResponse(), HttpStatus.OK);
    }

    @PostMapping(value = "/addNode", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TreeResponse> addNode(@RequestBody TreeRequest request) {
        Node newNode = Node.builder().value(request.value()).build();
        newNode = service.save(newNode);

        if (request.parentId() != null) {
            Node finalNewNode = newNode;
            service.findById(request.parentId()).ifPresentOrElse((parentNode -> {
                Connection newConnection = new Connection(parentNode, finalNewNode);
                newConnection = service.save(newConnection);
            }), () -> {
                throw new TreeException("Parent node does not exists. Check your tree on refreshed values");
            });

        }
        return new ResponseEntity<>(buildResponse(), HttpStatus.CREATED);
    }

    @PostMapping(value = "/editNode", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TreeResponse> editNode(@RequestBody TreeRequest request) {
        Node requestNode = Node.builder().value(request.value()).id(request.nodeId()).build();

        // check if node still exists
        service.findById(request.nodeId()).orElseThrow(() -> {
            throw new TreeException("Node no longer exists. Check refreshed tree");
        });
        requestNode = service.save(requestNode);

        if (request.parentId() == null) {
            service.makeNodeRoot(request.nodeId());
        } else {
            Optional<Connection> relation = service.findChildByChildId(request.nodeId());
            relation.ifPresentOrElse(
                    (connection -> {
                        if (!Objects.equals(connection.getParent().getId(), request.parentId())) {
                            Optional<Node> newParent = service.findById(request.parentId());
                            newParent.ifPresentOrElse(parent -> {
                                connection.setParent(parent);
                                service.save(connection);
                            }, () -> {
                                throw new TreeException("New parent node does not exists. Check refreshed tree");
                            });
                        }
                    }), () -> {
                        throw new TreeException("Parent node does not exists. Check refreshed tree");
                    }
            );
        }
        return new ResponseEntity<>(buildResponse(), HttpStatus.OK);
    }

    @DeleteMapping("/deleteNode/{id}")
    public ResponseEntity<TreeResponse> deleteNode(@PathVariable int id) {

        service.deleteNode(id);

        return new ResponseEntity<>(buildResponse(), HttpStatus.OK);
    }

    private TreeResponse buildResponse() {
        return TreeResponse.builder()
                .references(service.findAll())
                .relations(service.getAllRelations())
                .leafs(service.sumAllPaths())
                .build();
    }
}
