package com.lorenzon.desafio_02.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_categoria")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String descricao;

    @OneToMany(mappedBy = "categoria")
    private List<Atividade> atividades = new ArrayList<>();
}
