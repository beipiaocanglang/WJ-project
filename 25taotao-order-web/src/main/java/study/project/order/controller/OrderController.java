package study.project.order.controller;

import org.joda.time.DateTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import study.project.order.pojo.OrderInfo;
import study.project.order.service.OrderService;

import javax.annotation.Resource;

/**
 * 订单操作
 * Created by panhusun on 2017/9/10.
 */
@Controller
public class OrderController {

    @Resource
    private OrderService orderService;

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
    @RequestMapping("/order/create")
    public String createOrder(OrderInfo orderInfo, Model model) {

        //订单号
        String orderId = orderService.orderCreate(orderInfo);

        //订单号
        model.addAttribute("orderId", orderId);
        //支付金额
        model.addAttribute("payment", orderInfo.getOrder().getPayment());
        //当前时间往后延长三天
        DateTime dateTime = new DateTime();
        dateTime.plusDays(3);

        model.addAttribute("date", dateTime);

        return "success";
    }

}
