package com.ask.ventas_presenciales.controller;

import com.ask.ventas_presenciales.service.ClienteService;
import com.ask.ventas_presenciales.service.EmpleadoService;
import com.ask.ventas_presenciales.service.MetodoPagoService;
import com.ask.ventas_presenciales.utils.Cart;
import com.ask.ventas_presenciales.utils.CartItem;
import com.ask.ventas_presenciales.model.*;
import com.ask.ventas_presenciales.repository.*;
import com.ask.ventas_presenciales.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/checkout")
@SessionAttributes("cart")
public class CheckoutController {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private BoletaRepository boletaRepository;

    @Autowired
    private DetalleBoletaRepository detalleBoletaRepository;

    @Autowired
    MetodoPagoRepository metodoPagoRepository;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private EmpleadoService empleadoService;

    @Autowired
    MetodoPagoService metodoPagoService;

    @GetMapping
    public String showCheckoutForm(Model model, @ModelAttribute("cart") Cart cart) {
        if (cart.getItems().isEmpty()) {
            return "redirect:/carrito";
        }

        model.addAttribute("cliente", new Cliente());
        model.addAttribute("cliente", new Cliente());
        List<Empleado> empleados = empleadoService.findAll();
        List<MetodoPago> metodosPago = metodoPagoService.findAll();

        if (empleados == null || metodosPago == null) {
            throw new RuntimeException("No se pudo obtener empleados o métodos de pago");
        }

        model.addAttribute("empleados", empleados);
        model.addAttribute("metodosPago", metodosPago);
        return "checkout/checkout";
    }

    @GetMapping("/clienteNuevo")
    public String showCheckoutFormNuevo(Model model, @ModelAttribute("cart") Cart cart) {
        if (cart.getItems().isEmpty()) {
            return "redirect:/carrito";
        }
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("cliente", new Cliente());
        List<Empleado> empleados = empleadoService.findAll();
        List<MetodoPago> metodosPago = metodoPagoService.findAll();

        if (empleados == null || metodosPago == null) {
            throw new RuntimeException("No se pudo obtener empleados o métodos de pago");
        }

        model.addAttribute("empleados", empleados);
        model.addAttribute("metodosPago", metodosPago);
        return "checkout/checkoutClienteNuevo";
    }


    @PostMapping("/checkoutDni")
    public String checkout(@RequestParam String dni, @ModelAttribute("cart") Cart cart,
                           @RequestParam Long empleadoId, @RequestParam Long metodoPagoId,
                           RedirectAttributes redirectAttributes) {

        if (cart.getItems().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El carrito está vacío.");
            return "redirect:/carrito";
        }

        Optional<Cliente> clienteOpt = clienteRepository.findByDni(dni);

        if (!clienteOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Cliente no encontrado.");
            return "redirect:/checkout";
        }

        Cliente cliente = clienteOpt.get();
        Empleado empleado = empleadoRepository.findById(empleadoId).orElse(null);
        MetodoPago metodoPago = metodoPagoRepository.findById(metodoPagoId).orElse(null);

        if (empleado == null || metodoPago == null) {
            redirectAttributes.addFlashAttribute("error", "Empleado o Método de Pago no válido.");
            return "redirect:/checkout";
        }

        Boleta boleta = new Boleta();
        boleta.setFecha(new Date());
        boleta.setCliente(cliente);
        boleta.setEmpleado(empleado);
        boletaRepository.save(boleta);

        Venta venta = new Venta();
        venta.setFecha(new Date());
        venta.setCliente(cliente);
        venta.setEmpleado(empleado);
        venta.setMontoTotal(cart.getTotal());
        venta.setMetodoPago(metodoPago);
        venta.setBoleta(boleta);
        ventaService.guardarVenta(venta);

        Long ventaID = venta.getVentaId();

        for (CartItem item : cart.getItems()) {
            DetalleBoleta detalle = new DetalleBoleta();
            detalle.setBoleta(boleta);
            detalle.setProducto(item.getProducto());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecio(item.getProducto().getPrecio());
            detalleBoletaRepository.save(detalle);

            Producto producto = item.getProducto();
            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto);
        }

        cart.clear();

        redirectAttributes.addFlashAttribute("success", "Compra realizada con éxito.");
        return "redirect:/venta/generarPDF?ventaId=" + ventaID;
    }

    @PostMapping("/checkoutCliente")
    public String checkout(@ModelAttribute("cliente") Cliente cliente, @ModelAttribute("cart") Cart cart,
                           @RequestParam Long empleadoId, @RequestParam Long metodoPagoId,
                           RedirectAttributes redirectAttributes) {

        if (cart.getItems().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El carrito está vacío.");
            return "redirect:/carrito";
        }

        Empleado empleado = empleadoRepository.findById(empleadoId).orElse(null);
        MetodoPago metodoPago = metodoPagoRepository.findById(metodoPagoId).orElse(null);

        if (empleado == null || metodoPago == null) {
            redirectAttributes.addFlashAttribute("error", "Empleado o Método de Pago no válido.");
            return "redirect:/checkout";
        }

        Cliente nuevoCliente = clienteRepository.save(cliente);


        Boleta boleta = new Boleta();
        boleta.setFecha(new Date());
        boleta.setCliente(nuevoCliente);
        boleta.setEmpleado(empleado);
        boletaRepository.save(boleta);

        Venta venta = new Venta();
        venta.setFecha(new Date());
        venta.setCliente(nuevoCliente);
        venta.setEmpleado(empleado);
        venta.setMontoTotal(cart.getTotal());
        venta.setMetodoPago(metodoPago);
        venta.setBoleta(boleta);
        ventaService.guardarVenta(venta);

        Long ventaID = venta.getVentaId();


        for (CartItem item : cart.getItems()) {
            DetalleBoleta detalle = new DetalleBoleta();
            detalle.setBoleta(boleta);
            detalle.setProducto(item.getProducto());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecio(item.getProducto().getPrecio());
            detalleBoletaRepository.save(detalle);

            Producto producto = item.getProducto();
            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto);
        }

        cart.clear();

        redirectAttributes.addFlashAttribute("success", "Compra realizada con éxito.");
        return "redirect:/venta/generarPDF?ventaId=" + ventaID;
    }

}
