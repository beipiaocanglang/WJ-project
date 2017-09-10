package study.project.pojo;

import study.project.domain.TbOrder;
import study.project.domain.TbOrderItem;
import study.project.domain.TbOrderShipping;

import java.util.List;

/**
 * 提交订单的包装类对象
 * Created by panhusun on 2017/9/10.
 */
public class OrderInfo {
    //变量名称必须和页面保持一样才能封装成功
    //封装订单对象
    private TbOrder order;
    //封装订单明细对象
    private List<TbOrderItem> orderItems;
    //封装收货地址对象
    private TbOrderShipping orderShipping;

    public TbOrder getOrder() {
        return order;
    }

    public void setOrder(TbOrder order) {
        this.order = order;
    }

    public List<TbOrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<TbOrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public TbOrderShipping getOrderShipping() {
        return orderShipping;
    }

    public void setOrderShipping(TbOrderShipping orderShipping) {
        this.orderShipping = orderShipping;
    }

    @Override
    public String toString() {
        return "OrderInfo{" + "order=" + order + ", orderItems=" + orderItems + ", orderShipping=" + orderShipping + '}';
    }
}
