package br.com.carloseduardo.urlshortener.controller;

import br.com.carloseduardo.urlshortener.dto.ShortenerSimpleDTO;
import br.com.carloseduardo.urlshortener.model.Shortener;
import br.com.carloseduardo.urlshortener.service.ShortenerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
@RequestMapping({"/urlshortener"})
public class UrlShortenerController {

    private final ShortenerService shortenerService;
    private static final String LOCAL_HOST = "http://localhost:8080/";

    UrlShortenerController(ShortenerService shortenerService) {
        this.shortenerService = shortenerService;
    }

    @PostMapping
    public ResponseEntity<String> encurtarUrl(@RequestBody @Valid ShortenerSimpleDTO shortenerSimpleDTO) {
        String encodedUrl = Base64.getUrlEncoder().encodeToString(shortenerSimpleDTO.getUrl().getBytes(StandardCharsets.UTF_8));
        String shortUrl = encodedUrl.substring(encodedUrl.length() - 8);
        String result = null;
        try {
            Shortener shortener = Shortener.builder()
                    .shortUrl(shortUrl)
                    .encodedUrl(encodedUrl)
                    .originalUrl(shortenerSimpleDTO.getUrl())
                    .quantidadeDeAcessos(0)
                    .build();
            Shortener resp = shortenerService.save(shortener);
            result =  LOCAL_HOST + resp.getShortUrl();
        } catch (Exception e) {
            System.out.println("Erro -> " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return ResponseEntity.ok().body(result);
    }

    @PutMapping
    public ResponseEntity<String> atualizarStatisticas(@RequestBody @Valid ShortenerSimpleDTO shortenerSimpleDTO) {
        ResponseEntity<String> result = ResponseEntity.notFound().build();
        try {
            String encodedUrl = Base64.getUrlEncoder().encodeToString(shortenerSimpleDTO.getUrl().getBytes());
            String shortUrl = encodedUrl.substring(encodedUrl.length() - 8);
            Shortener shortener = shortenerService.findByShortUrl(shortUrl);
            if (shortener != null) {
                shortener.setQuantidadeDeAcessos(shortener.getQuantidadeDeAcessos() + 1);
                Shortener resp = shortenerService.update(shortener);
                if (resp == null) {
                    result = ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("Não foi possível atualizar a quantidade de acessos da url");
                } else {
                    result = ResponseEntity.ok().body(resp.getQuantidadeDeAcessos().toString());
                }
            }
        } catch (Exception e) {
            System.out.println("Erro -> " + e.getMessage());
            result = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return result;
    }
}
