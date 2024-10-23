package com.ask.ventas_presenciales.controller;

import com.ask.ventas_presenciales.model.Producto;
import com.ask.ventas_presenciales.repository.ProductoRepository;
import com.ask.ventas_presenciales.utils.Cart;
import com.ask.ventas_presenciales.utils.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/carrito")
@SessionAttributes("cart")
public class CartController {

    @Autowired
    private ProductoRepository productoRepository;

    @ModelAttribute("cart")
    public Cart cart() {
        return new Cart();
    }

    @GetMapping
    public String showCart(Model model) {
        return "carrito/carrito";
    }

    @PostMapping("/agregarCarrito/{id}")
    public String agregarAlCarrito(@PathVariable("id") Long productoId,
                                   @RequestParam(value = "cantidad", defaultValue = "1") Integer cantidad,
                                   @ModelAttribute("cart") Cart cart,
                                   RedirectAttributes redirectAttributes) {
        Optional<Producto> productoOpt = productoRepository.findById(productoId);
        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();
            if (producto.getStock() >= cantidad) {
                cart.addItem(new CartItem(producto, cantidad));
                redirectAttributes.addFlashAttribute("success", "Producto agregado al carrito.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Stock insuficiente.");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Producto no encontrado.");
        }
        return "redirect:/productos/catalogo";
    }

    @GetMapping("/remove/{productoId}")
    public String removeFromCart(@PathVariable Long productoId,
                                 @ModelAttribute("cart") Cart cart,
                                 RedirectAttributes redirectAttributes) {
        cart.removeItem(productoId);
        redirectAttributes.addFlashAttribute("success", "Producto eliminado del carrito.");
        return "redirect:/productos/catalogo";
    }

    @GetMapping("/clear")
    public String clearCart(@ModelAttribute("cart") Cart cart, RedirectAttributes redirectAttributes) {
        cart.clear();
        redirectAttributes.addFlashAttribute("success", "Carrito vacío.");
        return "redirect:/carrito";
    }
}
