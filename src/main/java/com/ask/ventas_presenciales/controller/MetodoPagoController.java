package com.ask.ventas_presenciales.controller;

import com.ask.ventas_presenciales.model.MetodoPago;
import com.ask.ventas_presenciales.service.MetodoPagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/metodos-pago")
public class MetodoPagoController {

    @Autowired
    private MetodoPagoService metodoPagoService;

    @GetMapping
    public String listarMetodosPago(Model model) {
        List<MetodoPago> metodosPago = metodoPagoService.findAll();
        model.addAttribute("metodosPago", metodosPago);
        return "metodos-pago/listar";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioCrearMetodoPago(Model model) {
        model.addAttribute("metodoPago", new MetodoPago());
        return "metodos-pago/crear";
    }

    @PostMapping
    public String crearMetodoPago(@ModelAttribute MetodoPago metodoPago) {
        metodoPagoService.save(metodoPago);
        return "redirect:/metodos-pago";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarMetodoPago(@PathVariable Long id, Model model) {
        MetodoPago metodoPago = metodoPagoService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid payment method Id:" + id));
        model.addAttribute("metodoPago", metodoPago);
        return "metodos-pago/editar";
    }

    @PostMapping("/editar/{id}")
    public String actualizarMetodoPago(@PathVariable Long id, @ModelAttribute MetodoPago metodoPago) {
        metodoPago.setMetodoPagoId(id);
        metodoPagoService.save(metodoPago);
        return "redirect:/metodos-pago";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarMetodoPago(@PathVariable Long id) {
        metodoPagoService.deleteById(id);
        return "redirect:/metodos-pago";
    }
}
