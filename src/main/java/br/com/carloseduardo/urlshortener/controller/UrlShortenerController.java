package br.com.carloseduardo.urlshortener.controller;

import br.com.carloseduardo.urlshortener.model.Shortener;
import br.com.carloseduardo.urlshortener.service.ShortenerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

@RestController
@RequestMapping({"/urlshortener"})
public class UrlShortenerController {

    private final ShortenerService shortenerService;

    UrlShortenerController(ShortenerService shortenerService) {
        this.shortenerService = shortenerService;
    }

    @PostMapping
    public ResponseEntity<String> encurtarUrl(@RequestParam String url) {
        if (url.isEmpty()) {
            return ResponseEntity.badRequest().body("A url esta vazia");
        }

        if (shortenerService.validateURL(url) == null) {
            return ResponseEntity.badRequest().body("Url inválida");
        }

        String encodedUrl = Base64.getUrlEncoder().encodeToString(url.getBytes());
        String shortUrl = encodedUrl.substring(encodedUrl.length() - 8);
        String result = null;
        try {
            Shortener shortener = new Shortener();
            shortener.setShortUrl(shortUrl);
            shortener.setEncodedUrl(encodedUrl);
            shortener.setOriginalUrl(url);
            Shortener resp = shortenerService.save(shortener);
            if (resp == null) {
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("Não foi possível encurtar a Url informada");
            } else {
                result = "http://short.ly/" + resp.getShortUrl();
            }
        } catch (Exception e) {
            System.out.println("Erro -> " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        return ResponseEntity.ok().body(result);
    }

    @PutMapping
    public ResponseEntity<String> atualizarStatisticas(@RequestParam String url) {
        if (url.isEmpty()) {
            return ResponseEntity.badRequest().body("A url esta vazia");
        }

        ResponseEntity<String> result = null;
        try {
            String encodedUrl = Base64.getUrlEncoder().encodeToString(url.getBytes());
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
            } else {
                result = ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.out.println("Erro -> " + e.getMessage());
            result = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return result;
    }
}
