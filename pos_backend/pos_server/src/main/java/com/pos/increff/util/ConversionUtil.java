package com.pos.increff.util;

import com.pos.increff.model.data.ClientData;
import com.pos.increff.model.data.ProductData;
import com.pos.increff.model.data.UserData;
import com.pos.increff.model.data.InventoryData;
import com.pos.increff.model.form.ClientForm;
import com.pos.increff.model.form.ProductForm;
import com.pos.increff.model.form.UserForm;
import com.pos.increff.model.form.InventoryForm;
import com.pos.increff.model.data.DaySalesData;
import com.pos.increff.model.form.CombinedOrdersForm;
import com.pos.increff.model.form.OrderItemsForm;
import com.pos.increff.pojo.ClientPojo;
import com.pos.increff.pojo.ProductPojo;
import com.pos.increff.pojo.UserPojo;
import com.pos.increff.pojo.InventoryPojo;
import com.pos.increff.pojo.OrderItemsPojo;
import com.pos.increff.pojo.OrdersPojo;
import com.pos.increff.pojo.DaySalesPojo;
import com.pos.commons.OrderItemsData;
import com.pos.commons.OrdersData;
import com.pos.increff.api.ApiException;
import com.pos.increff.api.ClientApi;
import com.pos.increff.api.ProductApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import com.pos.commons.OrderStatus;

@Component
public class ConversionUtil {

    @Autowired
    private ProductApi productApi;

    // Client conversions
    public static ClientPojo convert(ClientForm form) {
        ClientPojo pojo = new ClientPojo();
        pojo.setName(form.getName());
        pojo.setEmail(form.getEmail());
        pojo.setContactNo(form.getContactNo());
        return pojo;
    }

    public static ClientData convert(ClientPojo pojo) {
        ClientData data = new ClientData();
        data.setId(pojo.getId().intValue());
        data.setName(pojo.getName());
        data.setCreatedDate(DateTimeUtils.toDate(pojo.getCreatedDate()));
        data.setUpdatedDate(DateTimeUtils.toDate(pojo.getUpdatedDate()));
        data.setContactNo(pojo.getContactNo());
        data.setEmail(pojo.getEmail());
        return data;
    }

    // Product conversions
    public static ProductPojo convert(ProductForm form, ClientApi clientApi) throws ApiException {
        ProductPojo pojo = new ProductPojo();
        pojo.setName(form.getName());
        pojo.setPrice(form.getPrice());
        pojo.setBarcode(form.getBarcode());
        ClientPojo client = clientApi.getByName(form.getClientName());
        pojo.setClientId(client.getId());
        return pojo;
    }

    public static ProductData convert(ProductPojo pojo) {
        ProductData data = new ProductData();
        data.setId(pojo.getId());
        data.setName(pojo.getName());
        data.setPrice(pojo.getPrice());
        data.setBarcode(pojo.getBarcode());
        data.setClientId(pojo.getClientId());
        return data;
    }

    // Inventory conversions
    public static InventoryPojo convert(InventoryForm form) {
        InventoryPojo pojo = new InventoryPojo();
        pojo.setProductId(form.getProductId());
        pojo.setTotalQuantity(form.getTotalQuantity());
        return pojo;
    }

    public static InventoryData convert(InventoryPojo pojo) {
        InventoryData data = new InventoryData();
        data.setId(pojo.getId());
        data.setProductId(pojo.getProductId());
        data.setTotalQuantity(pojo.getTotalQuantity());
        return data;
    }

    public OrderItemsData convert(OrderItemsPojo item) throws ApiException {
        OrderItemsData data = new OrderItemsData();
        data.setId(item.getId());
        data.setOrderId(item.getOrderId());
        data.setQuantity(item.getQuantity());
        data.setPrice(item.getTotalPrice());
        ProductPojo product = productApi.get(item.getProductId());
        data.setProductName(product.getName());
        return data;
    }

    public static OrdersData convert(OrdersPojo order) {
        OrdersData data = new OrdersData();
        data.setId(order.getId());
        data.setUserEmail(order.getUserEmail());
        data.setTotalPrice(order.getTotalPrice());
        data.setUpdatedDate(DateTimeUtils.toDate(order.getUpdatedDate()));
        data.setStatus(order.getStatus());
        data.setInvoicePath(order.getInvoicePath());
        return data;
    }

    public static UserData convert(UserPojo p) {
        UserData d = new UserData();
        d.setEmail(p.getEmail());
        d.setRole(p.getRole());
        return d;
    }

    public static UserPojo convert(UserForm form) {
        UserPojo p = new UserPojo();
        p.setEmail(form.getEmail());
        return p;
    }

    public DaySalesData convertDaySales(DaySalesPojo pojo) {
        DaySalesData data = new DaySalesData();
        data.setId(pojo.getId());
        data.setDate(pojo.getDate());
        data.setOrderCount(pojo.getOrderCount());
        data.setProductCount(pojo.getProductCount());
        data.setRevenue(pojo.getRevenue());
        return data;
    }

    public OrdersPojo convert(CombinedOrdersForm form) {
        OrdersPojo order = new OrdersPojo();
        order.setUserEmail(form.getUserEmail());
        order.setTotalPrice(form.getTotalPrice());
        order.setStatus(OrderStatus.PENDING_INVOICE);
        return order;
    }

    public List<OrderItemsPojo> convertItems(List<OrderItemsForm> forms, int orderId) {
        if (forms == null) {
            return new ArrayList<>();
        }
        
        List<OrderItemsPojo> items = new ArrayList<>();
        for (OrderItemsForm form : forms) {
            if (form != null) {
                System.out.println(form.getProductId() + "price" + form.getPrice()) ;
                OrderItemsPojo item = new OrderItemsPojo();
                item.setOrderId(orderId);
                item.setProductId(form.getProductId());
                item.setQuantity(form.getQuantity());
                item.setTotalPrice(form.getPrice());
                items.add(item);
            }
        }
        return items;
    }
} 