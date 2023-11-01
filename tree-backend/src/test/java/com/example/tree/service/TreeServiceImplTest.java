package com.example.tree.service;

import com.example.tree.entieties.Connection;
import com.example.tree.entieties.Node;
import com.example.tree.exceptions.TreeException;
import com.example.tree.repositories.ConnectionRepository;
import com.example.tree.repositories.NodeRepository;
import com.example.tree.rest.TreeRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

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
    private void setUp() {
        service = new TreeServiceImpl(nodeRepository, connectionRepository);
    }

    @Test
    void addFirstNode() {
        TreeRequest req = new TreeRequest(null, 1, null);
        // no exception during insert
        assertDoesNotThrow(() -> {
            Node res = service.addNode(req);
            // no child with id of inserted node
            assertThrows(NoSuchElementException.class, () -> {
                connectionRepository.findByChildId(res.getId()).get();
            });
        });
    }

    @Test
    void addChildNode() {
        Node t = Node.builder().value(1).build();
        t = entityManager.persistAndFlush(t);

        TreeRequest req = new TreeRequest(null, 1, t.getId());
        // no exception during insert
        assertDoesNotThrow(() -> {
            Node res = service.addNode(req);
            // no child with id of inserted node
            assertEquals(res, connectionRepository.findByChildId(res.getId()).get().getChild());
        });
    }

    @Test
    void childNodeWithNodeId() {
        TreeRequest req = new TreeRequest(1, 1, null);
        // exception during insert
        assertThrows(TreeException.class, () -> {
            Node res = service.addNode(req);
        });
    }

    @Test
    void childNodeWithNotExistingParent() {
        TreeRequest req = new TreeRequest(null, 1, -1);
        // exception during insert
        assertThrows(TreeException.class, () -> {
            Node res = service.addNode(req);
        });
    }

    @Test
    void addNewNodeWithNullValue() {
        TreeRequest request = new TreeRequest(null, null, null);
        assertThrows(TreeException.class, () -> {
            Node res = service.addNode(request);
        });

    }

    @Test
    void editNodeValue() {
        Node dbNode = Node.builder().value(1).build();
        dbNode = entityManager.persistAndFlush(dbNode);

        TreeRequest req = new TreeRequest(dbNode.getId(), 2, null);
        // no exception during change
        assertDoesNotThrow(() -> {
            Node res = service.editNode(req);
            assertEquals(2, res.getValue());
        });

    }

    @Test
    void editNodePassNullValue() {
        Node dbNode = Node.builder().value(1).build();

        TreeRequest req = new TreeRequest(dbNode.getId(), null, null);
        // exception during insert
        assertThrows(TreeException.class, () -> {
            Node res = service.editNode(req);
        });

    }

    @Test
    void editNodeWithNullId() {
        Node dbNode = Node.builder().value(1).build();

        TreeRequest req = new TreeRequest(null, 1, null);
        // exception during change
        assertThrows(TreeException.class, () -> {
            Node res = service.editNode(req);
        });
    }

    @Test
    void editNodeThatNotExists() {

        TreeRequest req = new TreeRequest(-1, 1, null);
        // exception during change
        assertThrows(TreeException.class, () -> {
            Node res = service.editNode(req);
        });
    }

    @Test
    void editNodeParentToExistingNode() {
        Node one = Node.builder().value(1).build();
        entityManager.persist(one);
        Node two = Node.builder().value(2).build();
        entityManager.persist(two);
        Node three = Node.builder().value(3).build();
        entityManager.persist(three);
        Connection c12 = new Connection(one, two);
        entityManager.persist(c12);
        entityManager.flush();
        TreeRequest req = new TreeRequest(two.getId(), 2, three.getId());
        // no exception during change
        assertDoesNotThrow(() -> {
            Node res = service.editNode(req);
            assertIterableEquals(List.of(two.getId()), service.getAllRelations().get(three.getId()));
            assertNull(service.getAllRelations().get(one.getId()));
        });
    }

    @Test
    void editNodeParentToNotExistingNode() {
        Node one = Node.builder().value(1).build();
        entityManager.persist(one);
        Node two = Node.builder().value(2).build();
        entityManager.persist(two);
        entityManager.flush();

        TreeRequest req = new TreeRequest(two.getId(), 2, -1);
        // exception during change
        assertThrows(TreeException.class, () -> {
            Node res = service.editNode(req);
        });
    }

    @Test
    void editNodeMakeNewRoot() {
        Node one = Node.builder().value(1).build();
        entityManager.persist(one);
        Node two = Node.builder().value(2).build();
        entityManager.persist(two);
        Node three = Node.builder().value(3).build();
        entityManager.persist(three);
        Connection c12 = new Connection(one, two);
        Connection c23 = new Connection(two, three);
        entityManager.persist(c12);
        entityManager.persistAndFlush(c23);

        TreeRequest req = new TreeRequest(two.getId(), 2, null);
        // no exception during change
        assertDoesNotThrow(() -> {
//            two as a child found
            assertDoesNotThrow(() -> {
                connectionRepository.findByChildId(two.getId()).get();
            });
            Node res = service.editNode(req);
            assertIterableEquals(List.of(three.getId()), service.getAllRelations().get(two.getId()));
            assertNull(service.getAllRelations().get(one.getId()));
//           two as child not found
            assertThrows(NoSuchElementException.class, () -> {
                connectionRepository.findByChildId(two.getId()).get();
            });
        });
    }

    @Test
    void deleteSingleNode() {
        assertDoesNotThrow(() -> {
            Node one = Node.builder().value(1).build();
            entityManager.persistAndFlush(one);
            assertEquals(1, service.findAll().size());
            service.deleteNode(one.getId());
            assertEquals(0, service.findAll().size());
        });
    }

    @Test
    void deleteNotExistingNode() {
        assertDoesNotThrow(() -> {
            service.deleteNode(-1);
        });
    }

    @Test
    void deleteNodeWithChildren() {
        Node one = Node.builder().value(1).build();
        entityManager.persist(one);
        Node two = Node.builder().value(2).build();
        entityManager.persist(two);
        Node three = Node.builder().value(3).build();
        entityManager.persist(three);
        Node four = Node.builder().value(4).build();
        Node five = Node.builder().value(5).build();
        entityManager.persist(four);
        entityManager.persistAndFlush(five);

        Connection c12 = new Connection(one, two);
        Connection c23 = new Connection(two, three);
        Connection c34 = new Connection(three, four);
        Connection c35 = new Connection(three, five);
        entityManager.persist(c12);
        entityManager.persist(c23);
        entityManager.persist(c34);
        entityManager.persist(c35);
        entityManager.flush();

        assertDoesNotThrow(() -> {
            assertEquals(2, service.getAllRelations().get(three.getId()).size());
            assertEquals(2, connectionRepository.findAllByParentId(three.getId()).size());
            service.deleteNode(three.getId());
            assertNull(service.getAllRelations().get(three.getId()));
            assertEquals(0, connectionRepository.findAllByParentId(three.getId()).size());
        });
    }

    @Test
    void getAllRelations() {
        Node one = Node.builder().value(1).build();
        entityManager.persist(one);
        Node two = Node.builder().value(2).build();
        entityManager.persist(two);
        Node three = Node.builder().value(3).build();
        entityManager.persist(three);
        Node four = Node.builder().value(4).build();
        Node five = Node.builder().value(5).build();
        entityManager.persist(four);
        Node aloneRoot = Node.builder().value(6).build();
        entityManager.persist(aloneRoot);

        entityManager.persistAndFlush(five);

        Connection c12 = new Connection(one, two);
        Connection c23 = new Connection(two, three);
        Connection c34 = new Connection(three, four);
        Connection c35 = new Connection(three, five);
        entityManager.persist(c12);
        entityManager.persist(c23);
        entityManager.persist(c34);
        entityManager.persist(c35);
        entityManager.flush();

        assertDoesNotThrow(() -> {
            Map<Integer, Set<Integer>> result = service.getAllRelations();
            // no alone root in relations
            assertFalse(result.containsKey(aloneRoot.getId()));

            assertTrue(result.get(one.getId()).contains(two.getId()));
            assertTrue(result.get(two.getId()).contains(three.getId()));
            assertTrue(result.get(three.getId()).contains(four.getId()));
            assertTrue(result.get(three.getId()).contains(five.getId()));

            // not containing any of parent nodes
            assertTrue(!result.get(three.getId()).containsAll(List.of(two.getId(), one.getId(), aloneRoot.getId())));

        });

    }

    @Test
    void findAll() {
        Node one = Node.builder().value(1).build();
        entityManager.persist(one);
        Node two = Node.builder().value(2).build();
        entityManager.persist(two);
        Node three = Node.builder().value(3).build();
        entityManager.persist(three);
        Node four = Node.builder().value(4).build();
        Node five = Node.builder().value(5).build();
        entityManager.persist(four);
        entityManager.persistAndFlush(five);

        Connection c12 = new Connection(one, two);
        Connection c23 = new Connection(two, three);
        Connection c34 = new Connection(three, four);
        Connection c35 = new Connection(three, five);
        entityManager.persist(c12);
        entityManager.persist(c23);
        entityManager.persist(c34);
        entityManager.persist(c35);
        entityManager.flush();

        assertEquals(5, service.findAll().size());
        service.deleteNode(five.getId());
        assertEquals(4, service.findAll().size());
        service.deleteNode(one.getId());
        assertEquals(0, service.findAll().size());
    }

    @Test
    void sumAllPaths() {
        Node one = Node.builder().value(1).build();
        entityManager.persist(one);
        Node two = Node.builder().value(2).build();
        entityManager.persist(two);
        Node three = Node.builder().value(3).build();
        entityManager.persist(three);
        Node four = Node.builder().value(4).build();
        Node five = Node.builder().value(5).build();
        entityManager.persist(four);
        entityManager.persistAndFlush(five);

        Node aloneRoot = Node.builder().value(6).build();
        entityManager.persistAndFlush(aloneRoot);

        Connection c12 = new Connection(one, two);
        Connection c23 = new Connection(two, three);
        Connection c34 = new Connection(three, four);
        Connection c35 = new Connection(three, five);
        entityManager.persist(c12);
        entityManager.persist(c23);
        entityManager.persist(c34);
        entityManager.persist(c35);
        entityManager.flush();

        assertDoesNotThrow(() -> {
            Map<Integer, Integer> result = service.sumAllPaths();

            assertEquals(6, result.get(aloneRoot.getId()));
            assertEquals(10, result.get(four.getId()));
            assertEquals(11, result.get(five.getId()));
        });
    }
    @Test
    void sumAllPathsOnEmptyTree(){
        assertDoesNotThrow(()->{
            assertTrue(service.sumAllPaths().isEmpty());
        });
    }
}