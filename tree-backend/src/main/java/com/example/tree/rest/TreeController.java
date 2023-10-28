package com.example.tree.rest;

import com.example.tree.entieties.Connection;
import com.example.tree.entieties.Node;
import com.example.tree.service.TreeService;
import jdk.jfr.ContentType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Set;


@RestController
@RequestMapping("tree")
@Slf4j
public class TreeController {
    private final TreeService service;

    public TreeController(@Autowired TreeService service){
        this.service = service;
    }

    @GetMapping("/test")
    public void test(){
        Node parent = Node.builder().value(10).build();
        parent = service.save(parent);

        Node child = Node.builder().value(11).build();
        child = service.save(child);

        Connection conn = new Connection();
        conn.setChild(child);
        conn.setParent(parent);
        conn = service.save(conn);


        log.info("Child before save: {}", child.toString());
        child = service.save(child);
        log.info("Child after save: {}", child.toString());

        log.info("Connection after save: {}", conn.toString());

    }
//    @PostMapping(value = "/addNode", consumes =MediaType.APPLICATION_JSON_VALUE ,produces = MediaType.APPLICATION_JSON_VALUE)
//    public Set<Node> addNode(@RequestBody Node newNode){
//        newNode = service.save(newNode);
//        log.info("Added node: {}", newNode);
//        log.info("All trees: {}", service.findAllTrees());
//        return service.findAllTrees();
//    }
//    @GetMapping("/deleteNode/{id}")
//    public Set<Node> deleteNode(@PathVariable("id") int id){
//        service.deleteById(id);
//        return service.findAllTrees();
//    }
//
//    @PostMapping("/updateNode")
//    public Set<Node> updateNode(Node updated){
//        service.update(updated);
//        return service.findAllTrees();
//    }
//    @GetMapping(value = "/getTrees", produces = MediaType.APPLICATION_JSON_VALUE)
//    public Set<Node> getTrees(){
//        return service.findAllTrees();
//    }

}
