package com.lorenzon.desafio_01;

import com.lorenzon.desafio_01.entities.Order;
import com.lorenzon.desafio_01.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Locale;

@SpringBootApplication
public class Desafio01Application implements CommandLineRunner {

    @Autowired
    private OrderService orderService;

    public static void main(String[] args) {
        SpringApplication.run(Desafio01Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Locale.setDefault(Locale.US);

        Order order1 = new Order(1034, 150.00, 20.0);
        Order order2 = new Order(2282, 800.00, 10.0);
        Order order3 = new Order(1309, 95.90, 0.0);

        System.out.println("Exemplo 1");
        System.out.printf("Pedido código: %d\n", order1.getCode());
        System.out.printf("Valor total: R$ %.2f\n", orderService.total(order1));
        System.out.println("------------------------------------------------------");
        System.out.println("Exemplo 2");
        System.out.printf("Pedido código: %d\n", order2.getCode());
        System.out.printf("Valor total: R$ %.2f\n", orderService.total(order2));
        System.out.println("------------------------------------------------------");
        System.out.println("Exemplo 3");
        System.out.printf("Pedido código: %d\n", order3.getCode());
        System.out.printf("Valor total: R$ %.2f\n", orderService.total(order3));
    }
}
