package com.example.recipe;

import java.awt.Desktop;
import java.net.URI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class RecipeApplication {

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        SpringApplication.run(RecipeApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void openBrowser() {
        String url = "http://localhost:8080/recipes";
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", url).start();
            }
        } catch (Exception ignored) {
            System.out.println("ブラウザ自動起動をスキップしました。手動で " + url + " を開いてください。");
        }
    }

// H2 Console を強制的に有効化するサーブレット登録
    @org.springframework.context.annotation.Bean
    public org.springframework.boot.web.servlet.ServletRegistrationBean<org.h2.server.web.JakartaWebServlet> h2servletRegistration() {
        org.springframework.boot.web.servlet.ServletRegistrationBean<org.h2.server.web.JakartaWebServlet> registration =
                new org.springframework.boot.web.servlet.ServletRegistrationBean<>(new org.h2.server.web.JakartaWebServlet());
        registration.addUrlMappings("/h2-console/*");
        return registration;
    }



}