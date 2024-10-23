package com.ask.ventas_presenciales.controller;

import com.ask.ventas_presenciales.model.Boleta;
import com.ask.ventas_presenciales.service.BoletaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/boletas")
public class BoletaController {

    @Autowired
    private BoletaService boletaService;

    @GetMapping
    public String listBoletas(Model model) {
        model.addAttribute("boletas", boletaService.findAll());
        return "boletas/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("boleta", new Boleta());
        return "boletas/create";
    }

    @PostMapping
    public String createBoleta(@ModelAttribute Boleta boleta) {
        boletaService.save(boleta);
        return "redirect:/boletas";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Boleta> boleta = boletaService.findById(id);
        if (boleta.isPresent()) {
            model.addAttribute("boleta", boleta.get());
            return "boletas/edit";
        }
        return "redirect:/boletas";
    }

    @PostMapping("/{id}")
    public String updateBoleta(@PathVariable Long id, @ModelAttribute Boleta boleta) {
        boleta.setBoletaId(id);
        boletaService.save(boleta);
        return "redirect:/boletas";
    }

    @GetMapping("/{id}/delete")
    public String deleteBoleta(@PathVariable Long id) {
        boletaService.deleteById(id);
        return "redirect:/boletas";
    }
}
