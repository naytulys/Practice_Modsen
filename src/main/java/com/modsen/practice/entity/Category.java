package com.modsen.practice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "categories")
@Builder
<<<<<<< HEAD
@EqualsAndHashCode
=======
>>>>>>> dece4ab01480537a5bddce8c9ace31e617fe5ea6
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "category")
    private Set<Product> categoryProducts;

}
