package net.sorted.exchange.orders.orderprocessor;

import java.util.List;
import net.sorted.exchange.orders.domain.OrderFill;
import net.sorted.exchange.orders.repository.OrderFillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderFillService {

    private OrderFillRepository orderFillRepository;

    @Autowired
    public OrderFillService(OrderFillRepository orderFillRepository) {
        this.orderFillRepository = orderFillRepository;
    }



    @Transactional
    public void saveAll(List<OrderFill> fills) {
        orderFillRepository.save(fills);
    }
}
