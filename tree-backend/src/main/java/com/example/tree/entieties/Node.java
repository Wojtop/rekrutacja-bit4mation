package com.example.tree.entieties;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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

    @NotNull
    private Integer value = 0;

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", value=" + value +
                '}';
    }
}
