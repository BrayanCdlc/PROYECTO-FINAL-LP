package com.ask.ventas_presenciales.utils;


import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<CartItem> items = new ArrayList<>();

    public void addItem(CartItem newItem) {
        for (CartItem item : items) {
            if (item.getProducto().getProductoId().equals(newItem.getProducto().getProductoId())) {
                item.setCantidad(item.getCantidad() + newItem.getCantidad());
                return;
            }
        }
        items.add(newItem);
    }

    public void removeItem(Long productoId) {
        items.removeIf(item -> item.getProducto().getProductoId().equals(productoId));
    }

    public Double getTotal() {
        return items.stream().mapToDouble(CartItem::getSubTotal).sum();
    }

    public void clear() {
        items.clear();
    }

	public List<CartItem> getItems() {
		return items;
	}

	public void setItems(List<CartItem> items) {
		this.items = items;
	}
    
    
}