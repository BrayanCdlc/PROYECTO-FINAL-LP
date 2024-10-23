package com.ask.ventas_presenciales.controller;

import com.ask.ventas_presenciales.model.DetalleBoleta;
import com.ask.ventas_presenciales.service.DetalleBoletaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/detalle-boletas")
public class DetalleBoletaController {

    @Autowired
    private DetalleBoletaService detalleBoletaService;

    @GetMapping
    public String listDetalleBoletas(Model model) {
        model.addAttribute("detalleBoletas", detalleBoletaService.findAll());
        return "detalleBoleta/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("detalleBoleta", new DetalleBoleta());
        return "detalleBoleta/create";
    }

    @PostMapping
    public String createDetalleBoleta(@ModelAttribute DetalleBoleta detalleBoleta) {
        detalleBoletaService.save(detalleBoleta);
        return "redirect:/detalle-boletas";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<DetalleBoleta> detalleBoleta = detalleBoletaService.findById(id);
        if (detalleBoleta.isPresent()) {
            model.addAttribute("detalleBoleta", detalleBoleta.get());
            return "detalleBoleta/edit";
        }
        return "redirect:/detalle-boletas";
    }

    @PostMapping("/{id}")
    public String updateDetalleBoleta(@PathVariable Long id, @ModelAttribute DetalleBoleta detalleBoleta) {
        detalleBoleta.setDetalleBoletaId(id);
        detalleBoletaService.save(detalleBoleta);
        return "redirect:/detalle-boletas";
    }

    @GetMapping("/{id}/delete")
    public String deleteDetalleBoleta(@PathVariable Long id) {
        detalleBoletaService.deleteById(id);
        return "redirect:/detalle-boletas";
    }
}