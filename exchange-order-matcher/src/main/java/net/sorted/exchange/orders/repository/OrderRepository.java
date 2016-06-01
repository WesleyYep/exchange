package net.sorted.exchange.orders.repository;

import java.util.List;
import net.sorted.exchange.orders.domain.Order;
import net.sorted.exchange.orders.domain.OrderStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends CrudRepository<Order, Long> {

    @Query("SELECT o FROM Order o where o.instrumentId = :instrumentId " +
            "and (o.status = net.sorted.exchange.orders.domain.OrderStatus.OPEN or o.status = net.sorted.exchange.orders.domain.OrderStatus.PARTIAL_FILL)")
    List<Order> findUnfilledByInstrumentId(@Param("instrumentId") String instrumentId);

    @Query("SELECT o FROM Order o where o.orderSubmitter = :submitter "+
            "and (o.status = net.sorted.exchange.orders.domain.OrderStatus.OPEN or o.status = net.sorted.exchange.orders.domain.OrderStatus.PARTIAL_FILL)")
    List<Order> findUnfilledBySubmitter(@Param("submitter") String submitter);

    @Query("SELECT o FROM Order o where o.orderSubmitter = :submitter and o.instrumentId = :instrumentId "+
            "and (o.status = net.sorted.exchange.orders.domain.OrderStatus.OPEN or o.status = net.sorted.exchange.orders.domain.OrderStatus.PARTIAL_FILL)")
    List<Order> findUnfilledBySubmitterAndInstrumentId(@Param("submitter") String submitter, @Param("instrumentId") String instrumentId);

    @Query("SELECT o FROM Order o where o.orderSubmitter = :submitter and o.instrumentId = :instrumentId "+
            "and o.submittedMs >= :fromMillis and o.submittedMs <= :toMillis")
    List<Order> findBySubmitterAndInstrumentIdInTimeRange(@Param("submitter") String submitter, @Param("instrumentId") String instrumentId,
                                                          @Param("fromMillis") long fromMillis, @Param("toMillis") long toMillis);

    @Query("SELECT o FROM Order o where o.orderSubmitter = :submitter "+
            "and o.submittedMs >= :fromMillis and o.submittedMs <= :toMillis")
    List<Order> findBySubmitterInTimeRange(@Param("submitter") String submitter, @Param("fromMillis") long fromMillis, @Param("toMillis") long toMillis);

    @Query("SELECT o FROM Order o where o.instrumentId = :instrumentId "+
            "and o.submittedMs >= :fromMillis and o.submittedMs <= :toMillis")
    List<Order> findByInstrumentIdInTimeRange(@Param("instrumentId") String instrumentId, @Param("fromMillis") long fromMillis, @Param("toMillis") long toMillis);

}
