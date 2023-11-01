package com.example.tree.service;

import com.example.tree.entieties.Connection;
import com.example.tree.entieties.Node;
import com.example.tree.repositories.ConnectionRepository;
import com.example.tree.repositories.NodeRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@Slf4j
class TreeServiceImplTest {

    @Autowired
    private NodeRepository nodeRepository;
    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private TestEntityManager entityManager;

    private TreeService service;

    @BeforeEach
    private void prepareTestData() {
         /*
        Test tree (id: value)
                   1:1
                /       \
              2:2       3:3
                       /   \
                      4:4   5:5
         */
        Node one = Node.builder().value(1).build();
        one = entityManager.persist(one);
        Node two = Node.builder().value(2).build();
        two = entityManager.persist(two);
        Node three = Node.builder().value(3).build();
        three = entityManager.persist(three);
        Node four = Node.builder().value(4).build();
        four = entityManager.persist(four);
        Node five = Node.builder().value(5).build();
        five = entityManager.persist(five);

        Connection c12 = new Connection(one, two);
        entityManager.persist(c12);
        Connection c13 = new Connection(one, three);
        entityManager.persist(c13);
        Connection c34 = new Connection(three, four);
        entityManager.persist(c34);
        Connection c35 = new Connection(three, five);
        entityManager.persist(c35);

        entityManager.flush();
        service = new TreeServiceImpl(nodeRepository, connectionRepository);
        log.info("Test data prepared: {}", service.findAll().toString());

    }

    @Test
    void addNode() {
    }

    @Test
    void editNode() {
    }

    @Test
    void deleteNode() {
    }

    @Test
    void getAllRelations() {
    }

    @Test
    void findAll() {
        log.info("Find all: {}", service.findAll().toString());
    }

    @Test
    void findAllChildren() {
    }

    @Test
    void sumAllPaths() {
    }
}