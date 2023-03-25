package com.kubaokleja.springbootangular.user;

import lombok.*;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "authorities")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "authorities")
    private Collection<Role> roles;

}