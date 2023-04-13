package br.senai.sc.editoralivros.dto;

import br.senai.sc.editoralivros.model.entity.Genero;
import lombok.Data;

@Data
public class PessoaDTO {
    //    @NotBlank
    private Long cpf;
    //    @NotBlank
    private String nome;
    //    @NotBlank
    private String sobrenome;
    //    @NotBlank
    private String email;
    //    @NotBlank
    private String senha;
    //    @NotBlank
    private Genero genero;
}