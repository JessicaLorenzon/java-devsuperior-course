package com.lorenzon.desafio_01.services;

import com.lorenzon.desafio_01.entities.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private ShippingService shippingService;

    public Double total(Order order) {
        Double discountValue = order.getBasic() * (order.getDiscount() / 100);
        Double partialTotal = order.getBasic() - discountValue;
        Double total =  partialTotal + shippingService.shipment(order);
        return total;
    }
}
