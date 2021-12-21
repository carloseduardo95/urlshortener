package br.com.carloseduardo.urlshortener.controller;

import br.com.carloseduardo.urlshortener.dto.ShortenerSimpleDTO;
import br.com.carloseduardo.urlshortener.model.Shortener;
import br.com.carloseduardo.urlshortener.service.ShortenerService;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping({"/urlshortener"})
public class UrlShortenerController {

    private final ShortenerService shortenerService;
    private static final Gson gson = new Gson();
    private static final String LOCAL_HOST = "http://localhost:8080/";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    UrlShortenerController(ShortenerService shortenerService) {
        this.shortenerService = shortenerService;
    }

    @PostMapping
    public ResponseEntity<String> encurtarUrl(@RequestBody @Valid ShortenerSimpleDTO shortenerSimpleDTO) {
        String encodedUrl = Base64.getUrlEncoder().encodeToString(shortenerSimpleDTO.getUrl().getBytes(StandardCharsets.UTF_8));
        String shortUrl = encodedUrl.substring(encodedUrl.length() - 8);
        Map<String, String> result = new HashMap<>();
        try {
            Shortener shortener = Shortener.builder()
                    .shortUrl(shortUrl)
                    .encodedUrl(encodedUrl)
                    .originalUrl(shortenerSimpleDTO.getUrl())
                    .quantidadeDeAcessos(0)
                    .build();
            Shortener resp = shortenerService.save(shortener);
            result.put("urlEncurtada", LOCAL_HOST + resp.getShortUrl());
            result.put("shortUrl", resp.getShortUrl());
        } catch (Exception e) {
            logger.error("Erro -> " + e.getMessage());
            result.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(gson.toJson(result));
        }
        return ResponseEntity.ok().body(gson.toJson(result));
    }

    @PutMapping
    public ResponseEntity<String> atualizarStatisticas(@RequestBody @Valid ShortenerSimpleDTO shortenerSimpleDTO) {
        ResponseEntity<String> result = ResponseEntity.notFound().build();
        Map<String, String> response = new HashMap<>();
        try {
            String encodedUrl = Base64.getUrlEncoder().encodeToString(shortenerSimpleDTO.getUrl().getBytes());
            String shortUrl = encodedUrl.substring(encodedUrl.length() - 8);
            Shortener shortener = shortenerService.findByShortUrl(shortUrl);
            if (shortener != null) {
                shortener.setQuantidadeDeAcessos(shortener.getQuantidadeDeAcessos() + 1);
                Shortener resp = shortenerService.update(shortener);
                if (resp == null) {
                    response.put("erro", "Não foi possível atualizar a quantidade de acessos da url");
                    result = ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(gson.toJson(response));
                } else {
                    response.put("quantidadeDeAcessos", resp.getQuantidadeDeAcessos().toString());
                    response.put("urlVisitada", resp.getOriginalUrl());
                    result = ResponseEntity.ok().body(gson.toJson(response));
                }
            }
        } catch (Exception e) {
            logger.error("Erro -> " + e.getMessage());
            response.put("erro", e.getMessage());
            result = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(gson.toJson(response));
        }
        return result;
    }
}
