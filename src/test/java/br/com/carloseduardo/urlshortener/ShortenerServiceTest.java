package br.com.carloseduardo.urlshortener;

import br.com.carloseduardo.urlshortener.model.Shortener;
import br.com.carloseduardo.urlshortener.service.ShortenerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ShortenerServiceTest {

    @Mock
    private ShortenerService shortenerService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("Testando metodo save")
    @Test
    void saveSuccess() {
        Shortener objectSaved = Shortener.builder().id(1L).build();

        when(shortenerService.save(any(Shortener.class))).thenReturn(objectSaved);

        Shortener vo = Shortener.builder()
                .encodedUrl("aHR0cHM6Ly93d3cuYmFuZGFiLmNvbS5ici8=")
                .quantidadeDeAcessos(0)
                .originalUrl("https://www.bandab.com.br/")
                .shortUrl("bS5ici8=")
                .build();

        Shortener register = shortenerService.save(vo);
        Assertions.assertEquals(objectSaved.getId(), register.getId());
    }

    @DisplayName("Testando metodo update")
    @Test
    void updateSuccess() {
        Shortener objectSaved = Shortener.builder().quantidadeDeAcessos(1).build();

        when(shortenerService.update(any(Shortener.class))).thenReturn(objectSaved);

        Shortener vo = Shortener.builder()
                .encodedUrl("aHR0cHM6Ly93d3cuYmFuZGFiLmNvbS5ici8=")
                .quantidadeDeAcessos(1)
                .originalUrl("https://www.bandab.com.br/")
                .shortUrl("bS5ici8=")
                .build();

        Shortener register = shortenerService.update(vo);
        Assertions.assertEquals(objectSaved.getQuantidadeDeAcessos(), register.getQuantidadeDeAcessos());
    }

    @DisplayName("Testando metodo validateURL")
    @Test
    void validateURLSuccess() {
        shortenerService = mock(ShortenerService.class);
        when(shortenerService.validateURL(anyString())).thenCallRealMethod();
        Assertions.assertTrue(shortenerService.validateURL("https://www.bandab.com.br/"));
    }
}
