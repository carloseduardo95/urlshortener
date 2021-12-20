package br.com.carloseduardo.urlshortener.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Shortener {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "shortUrl  é obrigatório")
    private String shortUrl;

    @NotBlank(message = "encodedUrl  é obrigatório")
    private String encodedUrl;

    @NotBlank(message = "URL original  é obrigatório")
    private String originalUrl;

    private Integer quantidadeDeAcessos = 0;
}
