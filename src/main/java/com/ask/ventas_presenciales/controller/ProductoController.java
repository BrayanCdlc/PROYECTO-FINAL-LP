package com.ask.ventas_presenciales.controller;

import com.ask.ventas_presenciales.model.Categoria;
import com.ask.ventas_presenciales.model.DetalleBoleta;
import com.ask.ventas_presenciales.model.Producto;
import com.ask.ventas_presenciales.service.CategoriaService;
import com.ask.ventas_presenciales.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private static final String UPLOAD_DIR = System.getProperty("user.dir") +"/src/main/resources/static/uploads/";

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public String listarProductos(Model model) {
        List<Producto> productos = productoService.findAll();
        model.addAttribute("productos", productos);
        return "productos/listar";
    }

    @GetMapping("/catalogo")
    public String obtenerProductos(Model model) {
        List<Producto> productos = productoService.findAll();
        
        for(Producto producto : productos) {
        	System.out.println(producto.getImagen());
        }
        
        model.addAttribute("productos", productos);
        return "productos/catalogo";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioCrearProducto(Model model) {
        model.addAttribute("producto", new Producto());
        List<Categoria> categorias = categoriaService.findAll();
        model.addAttribute("categorias", categorias);

        return "productos/crear";
    }

    @PostMapping
    public String crearProducto(@ModelAttribute Producto producto, @RequestParam Long categoriaId, @RequestParam("file") MultipartFile file) {
        Categoria categoria = categoriaService.findById(categoriaId).orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + categoriaId));
        producto.setCategoria(categoria);

        if (!file.isEmpty()) {
            try {
                Files.createDirectories(Paths.get(UPLOAD_DIR));
                String imagenNombre = producto.getNombre().replaceAll("[^a-zA-Z0-9]", "_") + ".png";
                Path path = Paths.get(UPLOAD_DIR + imagenNombre);

                file.transferTo(path.toFile());
                producto.setImagen(imagenNombre);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        productoService.save(producto);
        return "redirect:/productos";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable("id") Long id, Model model) {
        Optional<Producto> optionalProducto = productoService.findById(id);
        if (optionalProducto.isPresent()) {
            Producto producto = optionalProducto.get();
            List<Categoria> categorias = categoriaService.findAll();
            model.addAttribute("producto", producto);
            model.addAttribute("categorias", categorias);
            return "productos/editar";
        } else {
            return "redirect:/productos";
        }
    }

    @PostMapping("/editar/{id}")
    public String actualizarProducto(@PathVariable Long id, @ModelAttribute Producto producto,
                                     @RequestParam("file") MultipartFile file,
                                     @RequestParam(required = false) List<DetalleBoleta> detalles) {
        Producto productoExistente = productoService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        String imagenAnterior = productoExistente.getImagen();

        if (!file.isEmpty()) {
            try {
                Files.createDirectories(Paths.get(UPLOAD_DIR));
                if (imagenAnterior != null && !imagenAnterior.isEmpty()) {
                    Path pathImagenAnterior = Paths.get(UPLOAD_DIR + imagenAnterior);
                    Files.deleteIfExists(pathImagenAnterior);
                }

                String imagenNombre = producto.getNombre().replaceAll("[^a-zA-Z0-9]", "_") + ".png";
                Path path = Paths.get(UPLOAD_DIR + imagenNombre);
                file.transferTo(path.toFile());
                producto.setImagen(imagenNombre);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            producto.setImagen(imagenAnterior);
        }

        producto.setProductoId(id);
        
        // Establecer detalles de boleta
        if (detalles != null) {
            for (DetalleBoleta detalle : detalles) {
                detalle.setProducto(producto); // Establecer la referencia del producto en detalle
            }
            producto.setDetallesBoleta(detalles);
        } else {
            producto.setDetallesBoleta(new ArrayList<>()); // Inicializar con una lista vacía si no hay detalles
        }

        productoService.save(producto);
        return "redirect:/productos";
    }



    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id) {
        productoService.deleteById(id);
        return "redirect:/productos";
    }
}
