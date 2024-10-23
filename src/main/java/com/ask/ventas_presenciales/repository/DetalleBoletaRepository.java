package com.ask.ventas_presenciales.repository;

import com.ask.ventas_presenciales.model.Boleta;
import com.ask.ventas_presenciales.model.DetalleBoleta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleBoletaRepository extends JpaRepository<DetalleBoleta, Long> {
    List<DetalleBoleta> findByBoleta(Boleta boleta);
}
