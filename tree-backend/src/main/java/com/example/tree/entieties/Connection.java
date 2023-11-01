package com.example.tree.entieties;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    private Node parent = null;
    @OneToOne
    @NotNull
    private Node child = null;

    public Connection(Node parent, Node child){
        this.parent = parent;
        this.child = child;
    }
}
