package model.pojo;

import java.util.List;

public class Orders {
    private List<Order> orders;
    private int total;
    private int totalToday;

    public Orders(List<Order> orders, int total, int totalToday) {
        this.orders = orders;
        this.total = total;
        this.totalToday = totalToday;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotalToday() {
        return totalToday;
    }

    public void setTotalToday(int totalToday) {
        this.totalToday = totalToday;
    }
}
