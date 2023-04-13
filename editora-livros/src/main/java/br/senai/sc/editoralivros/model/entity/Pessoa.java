package br.senai.sc.editoralivros.model.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "pessoas")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Pessoa {
    @Id
    @Column(length = 11, nullable = false, unique = true)
    private Long cpf;

    @Column(length = 50, nullable = false)
    private String nome;

    @Column(length = 100, nullable = false)
    private String sobrenome;

    @Column(length = 150, nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Enumerated(value = EnumType.STRING)
    @Column(length = 15, nullable = false)
    private Genero genero;
}