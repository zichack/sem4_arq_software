package br.com.mvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.mvc.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
