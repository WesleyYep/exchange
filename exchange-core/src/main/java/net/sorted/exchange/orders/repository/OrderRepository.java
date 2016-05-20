package net.sorted.exchange.orders.repository;

import java.util.List;
import net.sorted.exchange.orders.domain.Order;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends CrudRepository<Order, Long> {

    @Query("SELECT o FROM Order o where o.instrumentId = :instrumentId and (o.status = net.sorted.exchange.orders.domain.OrderStatus.OPEN or o.status = net.sorted.exchange.orders.domain.OrderStatus.PARTIAL_FILL)")
    List<Order> findOpenByInstrumentId(@Param("instrumentId") String instrumentId);

    List<Order> findByInstrumentId(String instrumentId);
}
