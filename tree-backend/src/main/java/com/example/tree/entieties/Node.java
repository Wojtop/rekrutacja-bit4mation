package com.example.tree.entieties;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "ID of node", required = true)
    private Integer id = null;

    @NotNull
    @Schema(description = "Value of node", required = true)
    private Integer value = 0;

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", value=" + value +
                '}';
    }
}
