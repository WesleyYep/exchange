package net.sorted.exchange.orders.repository;

import java.util.List;
import net.sorted.exchange.orders.domain.Order;
import net.sorted.exchange.orders.domain.OrderFill;
import org.springframework.data.repository.CrudRepository;

public interface OrderFillRepository extends CrudRepository<OrderFill, Long> {
    List<OrderFill> findByOrderId(long orderId);
}
