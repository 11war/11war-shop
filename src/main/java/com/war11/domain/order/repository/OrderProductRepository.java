package com.war11.domain.order.repository;

import com.war11.domain.order.entity.OrderProduct;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {

  List<OrderProduct> findByOrderId(Long orderId);
}
