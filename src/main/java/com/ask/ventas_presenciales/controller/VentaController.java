package com.ask.ventas_presenciales.controller;

import com.ask.ventas_presenciales.model.DetalleBoleta;
import com.ask.ventas_presenciales.model.Venta;
import com.ask.ventas_presenciales.repository.DetalleBoletaRepository;
import com.ask.ventas_presenciales.service.DetalleBoletaService;
import com.ask.ventas_presenciales.service.VentaService;
import com.ask.ventas_presenciales.utils.DetalleBoletaReporte;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.io.IOException;

@Controller
@RequestMapping("/venta")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private DetalleBoletaRepository detalleBoletaRepository;

    @GetMapping("/listar")
    public String listarVentas(Model model) {
        List<Venta> ventas = ventaService.obtenerTodasLasVentas();
        model.addAttribute("ventas", ventas);
        return "venta/listar";
    }

    @GetMapping("/crear")
    public String mostrarFormularioCreacion(Model model) {
        model.addAttribute("venta", new Venta());
        return "venta/crear";
    }

    @PostMapping("/guardar")
    public String guardarVenta(@ModelAttribute("venta") Venta venta) {
        ventaService.guardarVenta(venta);
        return "redirect:/ventas/listar";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarVenta(@PathVariable Long id) {
        ventaService.eliminarVenta(id);
        return "redirect:/ventas/listar";
    }

    @GetMapping("/detalle/{id}")
    public String mostrarDetalleVenta(@PathVariable Long id, Model model) {
        Venta venta = ventaService.obtenerVentaPorId(id);
        model.addAttribute("venta", venta);
        return "venta/detalle";
    }


    @GetMapping("/generarPDF")
    public void generarPDF(HttpServletResponse response, @RequestParam Long ventaId) throws IOException {
    	Venta venta = ventaService.obtenerVentaPorId(ventaId);
        List<DetalleBoleta> detalles = detalleBoletaRepository.findByBoleta(venta.getBoleta());
        List<DetalleBoletaReporte> detallesBoletaReporte = new ArrayList<>();

        try {
            final File file = ResourceUtils.getFile("classpath:static/export/exportInvoice.jasper"); 
            final File imgLogo = ResourceUtils.getFile("classpath:static/export/logo.png"); 
            final JasperReport report = (JasperReport) JRLoader.loadObject(file);

            final HashMap<String, Object> parameters = new HashMap<>();
            parameters.put("imgLogo", new FileInputStream(imgLogo));
            parameters.put("nombreCliente", venta.getCliente().getNombre() + " " + venta.getCliente().getApellido());
            parameters.put("total", venta.getMontoTotal());
            parameters.put("nombreEmpleado", venta.getEmpleado().getNombre());
            parameters.put("metodoPago", venta.getMetodoPago().getNombre());
            parameters.put("fechaVenta", venta.getFecha().toString());
            
            for(DetalleBoleta detalle : detalles) {
            	String nombre = detalle.getProducto().getNombre();
            	int cantidad = detalle.getCantidad();
            	Double precio = detalle.getProducto().getPrecio();
            	Double subTotal = detalle.getPrecio();
            	
            	detallesBoletaReporte.add(new DetalleBoletaReporte(nombre, cantidad, precio, subTotal));
            }
            
            
            parameters.put("dsInvoice", new JRBeanCollectionDataSource(detallesBoletaReporte));

            JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=boleta_venta_" + ventaId + ".pdf");


            JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        
    }


}
