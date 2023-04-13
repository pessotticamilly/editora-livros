package br.senai.sc.editoralivros.model.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "arquivos")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Getter @Setter
public class Arquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NonNull
    private String nome;
    @NonNull
    private String tipo;
    @NonNull
    @Lob
    private byte[] dados;


}
