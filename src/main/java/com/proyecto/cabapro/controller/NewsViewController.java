package com.proyecto.cabapro.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.proyecto.cabapro.model.NewsArticle;
import com.proyecto.cabapro.service.NoticiasProvider;

@Controller
public class NewsViewController {

    private final NoticiasProvider noticiasApi;   // implementacion real
    private final NoticiasProvider noticiasMock;  // implementacion de prueba

    // Inyectamos las implementaciones concretas usando @Qualifier
    public NewsViewController(
            @Qualifier("noticiasApiProvider") NoticiasProvider noticiasApi,
            @Qualifier("noticiasMockProvider") NoticiasProvider noticiasMock) {
        this.noticiasApi = noticiasApi;
        this.noticiasMock = noticiasMock;
    }

    // Mostrar noticias reales
    @GetMapping("/noticias/api")
    public String showNewsApi(Model model) {
        List<NewsArticle> articles = noticiasApi.obtenerNoticias();
        model.addAttribute("articles", articles);
        return "news"; // Thymeleaf buscará news.html
    }

    // Mostrar noticias mock
    @GetMapping("/noticias/mock")
    public String showNewsMock(Model model) {
        List<NewsArticle> articles = noticiasMock.obtenerNoticias();
        model.addAttribute("articles", articles);
        return "news"; 
    }
}
