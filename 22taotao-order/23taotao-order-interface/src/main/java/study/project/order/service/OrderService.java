package study.project.order.service;

import study.project.order.pojo.OrderInfo;

/**
 * 提交订单
 * Created by panhusun on 2017/9/10.
 */
public interface OrderService {
    /**
     * 功能26：
     *      提交订单
     * URL:
     *      /order/create.html
     * param：
     *
     * return：
     *      页面回显数据:
     *          orderId
     *          时间
     *          金额(页面传递)
     *      success成功页面
     * 订单号设置：
     *      1、sql查询获取初始值，每次加1(缺点：加重的数据库的读写压力)
     *      2、redis自增方法，每一次生成一个订单，自动加1
     *          redis订单号设计：
     *              key:ORDER_NUM
     *              value:初始值(START_ORDER_NUM)
     * 业务流程;
     *      需要提交三张表的数据，所以需要封装一个包装类对象来封装三张表的数据
     */
    String orderCreate(OrderInfo orderInfo);
}
