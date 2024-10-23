package com.ask.ventas_presenciales.service;

import com.ask.ventas_presenciales.model.DetalleBoleta;
import com.ask.ventas_presenciales.repository.DetalleBoletaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DetalleBoletaService {

    @Autowired
    private DetalleBoletaRepository detalleBoletaRepository;

    public List<DetalleBoleta> findAll() {
        return detalleBoletaRepository.findAll();
    }

    public Optional<DetalleBoleta> findById(Long id) {
        return detalleBoletaRepository.findById(id);
    }

    public DetalleBoleta save(DetalleBoleta detalleBoleta) {
        return detalleBoletaRepository.save(detalleBoleta);
    }

    public void deleteById(Long id) {
        detalleBoletaRepository.deleteById(id);
    }
}
