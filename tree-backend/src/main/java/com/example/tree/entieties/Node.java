package com.example.tree.entieties;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Entity
@Table(name="nodes")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Node{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id = null;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Node> children = new HashSet<>();

//    @Column(name = "parent_id", nullable = true)
//    private Integer parentId;

    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name="id")
    private Node parent = null;

//        @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name="id")
//    private Integer parent = null;
    @Getter
    private Integer value = null;


    public Set<Node> getChildren() {
        if (this.children == null){
            this.children = new HashSet<>();
        }

        return this.children;
    }

    public boolean isRoot() {
        return this.parent == null;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

//    @Override
//    public String toString() {
//        return "Node{" +
//                "id=" + id +
//                ", children=" + children +
//                ", parent=" + parent +
//                ", value=" + value +
//                '}';
//    }
}
