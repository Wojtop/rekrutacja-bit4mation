package com.example.tree.entieties;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "connections")
@Data
@NoArgsConstructor
public class Connection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
//    @JoinColumn( name = "id",nullable = true)
    private Node parent = null;
    @OneToOne(cascade =CascadeType.REMOVE, orphanRemoval = true)
    private Node child = null;

    public Connection(Node parent, Node child){
        this.parent = parent;
        this.child = child;
    }
}
