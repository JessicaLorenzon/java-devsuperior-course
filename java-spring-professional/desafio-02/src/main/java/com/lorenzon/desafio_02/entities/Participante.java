package com.lorenzon.desafio_02.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tb_participante")
public class Participante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nome;
    private String email;

    @ManyToMany(mappedBy = "participantes")
    private Set<Atividade> atividades = new HashSet<>();
}
