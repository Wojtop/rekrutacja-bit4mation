package com.example.tree.rest;

import com.example.tree.entieties.Node;
import com.example.tree.service.TreeService;
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

    @GetMapping(value = "getTree", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TreeResponse> getTree() {
        return new ResponseEntity<>(buildResponse(), HttpStatus.OK);
    }

    @PostMapping(value = "/addNode", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin
    public ResponseEntity<TreeResponse> addNode(@RequestBody TreeRequest request) {
        Node newNode = service.addNode(request);
        return new ResponseEntity<>(buildResponse(newNode), HttpStatus.CREATED);
    }

    @PutMapping(value = "/editNode", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin
    public ResponseEntity<TreeResponse> editNode(@RequestBody TreeRequest request) {
        Node modified = service.editNode(request);
        return new ResponseEntity<>(buildResponse(modified), HttpStatus.OK);
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
                .modified(null)
                .build();
    }
    private TreeResponse buildResponse(Node modified){
        return TreeResponse.builder()
                .references(service.findAll())
                .relations(service.getAllRelations())
                .leafs(service.sumAllPaths())
                .modified(modified)
                .build();
    }
}
