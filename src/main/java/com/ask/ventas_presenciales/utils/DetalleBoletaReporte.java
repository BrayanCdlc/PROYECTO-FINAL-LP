package com.ask.ventas_presenciales.utils;

public class DetalleBoletaReporte {
	
	 private String nombre;
	 private Integer cantidad;
	 private Double precio;
	 private Double subTotal;
	 
	public DetalleBoletaReporte(String nombre, Integer cantidad, Double precio, Double subTotal) {
		this.nombre = nombre;
		this.cantidad = cantidad;
		this.precio = precio;
		this.subTotal = subTotal;
	}
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String productoNombre) {
		this.nombre = productoNombre;
	}
	public Integer getCantidad() {
		return cantidad;
	}
	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}
	public Double getPrecio() {
		return precio;
	}
	public void setPrecio(Double precio) {
		this.precio = precio;
	}
	public Double getSubTotal() {
		return subTotal;
	}
	public void setSubTotal(Double subtotal) {
		this.subTotal = subtotal;
	}
	 
	 

}
