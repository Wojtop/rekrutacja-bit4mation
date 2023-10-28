package com.example.tree.rest;

import com.example.tree.entieties.Connection;
import com.example.tree.entieties.Node;
import com.example.tree.service.TreeService;
import jdk.jfr.ContentType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;


@RestController
@RequestMapping("tree")
@Slf4j
public class TreeController {
    private final TreeService service;

    public TreeController(@Autowired TreeService service) {
        this.service = service;
    }

    @GetMapping("/test")
    public void test() {
//        Node parent = Node.builder().value(10).build();
//        parent = service.save(parent);
//
//        Node child = Node.builder().value(11).build();
//        child = service.save(child);
//
//        Connection conn = new Connection();
//        conn.setChild(child);
//        conn.setParent(parent);
//        conn = service.save(conn);
//
//
//        log.info("Child before save: {}", child.toString());
//        child = service.save(child);
//        log.info("Child after save: {}", child.toString());
//
//        log.info("Connection after save: {}", conn.toString());
        log.info("Children of 3: {}", service.findAllChildren(3).toString());

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
//                TODO obsluga bledu brak rodzica
            });

        }
        return new ResponseEntity<>(buildResponse(), HttpStatus.CREATED);
    }

    @PostMapping(value = "/editNode", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TreeResponse> editNode(@RequestBody TreeRequest request) {
        Node requestNode = Node.builder().value(request.value()).id(request.nodeId()).build();

//        todo - łapanie błędu gdy node już nie istnieje
        requestNode = service.save(requestNode);

        if (request.parentId() == null){

            service.makeNodeRoot(request.nodeId());
        }
        else{
            Optional<Connection> relation = service.findChildByChildId(request.nodeId());
            relation.ifPresentOrElse(
                    (connection -> {
                        if (!Objects.equals(connection.getParent().getId(), request.parentId())) {
                            Optional<Node> newParent = service.findById(request.parentId());
                            newParent.ifPresentOrElse(parent -> {
                                connection.setParent(parent);
                                service.save(connection);
                            }, ()->{
//                                todo brak nowego rodzica
                            });


                        }
                    }), () -> {
//                    TODO - brak rodzicaconnection
                    }
            );
        }
        return new ResponseEntity<>(buildResponse(), HttpStatus.OK);
    }
    @DeleteMapping("/deleteNode/{id}")
    public ResponseEntity<TreeResponse> deleteNode(@PathVariable int id){

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

//    @GetMapping("/deleteNode/{id}")
//    public Set<Node> deleteNode(@PathVariable("id") int id) {
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
