package com.pos.increff.scheduler;

import com.pos.increff.api.DaySalesApi;
import com.pos.increff.api.OrdersApi;
import com.pos.increff.api.OrderItemsApi;
import com.pos.increff.pojo.DaySalesPojo;
import com.pos.increff.pojo.OrdersPojo;
import com.pos.increff.pojo.OrderItemsPojo;
import com.pos.increff.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.List;
import com.pos.increff.api.ApiException;
import java.time.ZonedDateTime;

@Component
public class DailySalesScheduler {

    @Autowired
    private DaySalesApi daySalesService;

    @Autowired
    private OrdersApi ordersService;

    @Autowired
    private OrderItemsApi orderItemsService;

    // Run at 23:59 UTC every day
    @Scheduled(cron = "0 59 23 * * *", zone = "UTC")
    public void updateDailySales() {
        try {
            ZonedDateTime todayZoned = DateTimeUtils.getStartOfDay(DateTimeUtils.getCurrentUtcDate());
            Date today = DateTimeUtils.toDate(todayZoned);

            DaySalesPojo daySales = daySalesService.getByDate(today);
            if (daySales == null) {
                daySales = new DaySalesPojo();
                daySales.setDate(today);
                daySales.setOrderCount(0);
                daySales.setProductCount(0);
                daySales.setRevenue(0.0);
            }

            // Get all orders for today
            List<OrdersPojo> todayOrders = ordersService.getOrdersByDate(today);
            
            daySales.setOrderCount(todayOrders.size());
            double totalRevenue = 0.0;
            int totalProducts = 0;

            for (OrdersPojo order : todayOrders) {
                try {
                    totalRevenue += order.getTotalPrice();
                    List<OrderItemsPojo> orderItems = orderItemsService.getByOrderId(order.getId());
                    totalProducts += orderItems.stream()
                        .mapToInt(OrderItemsPojo::getQuantity)
                        .sum();
                } catch (ApiException e) {
                }
            }

            daySales.setRevenue(totalRevenue);
            daySales.setProductCount(totalProducts);

            if (daySales.getId() == null) {
                daySalesService.insert(daySales);
            } else {
                daySalesService.update(daySales);
            }
        } catch (ApiException e) {
        }
    }
} 