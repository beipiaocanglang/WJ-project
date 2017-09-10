package study.project.order.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import study.project.domain.TbOrder;
import study.project.domain.TbOrderItem;
import study.project.domain.TbOrderShipping;
import study.project.mapper.TbOrderItemMapper;
import study.project.mapper.TbOrderMapper;
import study.project.mapper.TbOrderShippingMapper;
import study.project.order.pojo.OrderInfo;
import study.project.order.redis.dao.JedisDao;
import study.project.order.service.OrderService;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 订到操作的实现类
 * Created by panhusun on 2017/9/10.
 */
@Service
public class OrderServiceImpl implements OrderService {

    //保存三张表数据
    @Resource
    private TbOrderMapper orderMapper;

    @Resource
    private TbOrderItemMapper orderItemMapper;

    @Resource
    private TbOrderShippingMapper orderShippingMapper;

    //存入redis服务器中的订单号的key
    @Value("${ORDER_NUM}")
    private String ORDER_NUM;

    //存入redis服务器中的订单号的可以对应的初始值
    @Value("${START_ORDER_NUM}")
    private String START_ORDER_NUM;

    //订单商品id，在redis中自增1时是1
    @Value("${ORDER_DETAIL_ID}")
    private String ORDER_DETAIL_ID;


    @Resource
    private JedisDao jedisDao;

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
    @Override
    public String orderCreate(OrderInfo orderInfo) {

        //****************************保存order对象***************start*************
        //获取order对象
        TbOrder order = orderInfo.getOrder();
        //设计orderId
        //从redis中获取orderId
        String orderId = jedisDao.get(ORDER_NUM);

        //判断redis中orderId是否存在
        if (StringUtils.isBlank(orderId)) {//为空
            //设置一个初始值
            jedisDao.set(ORDER_NUM, START_ORDER_NUM);
        }

        //新增订单时redis中的orderId自增1
        orderId = jedisDao.incr(ORDER_NUM).toString();

        //不全参数 - orderId
        order.setOrderId(orderId);
        //邮费
        order.setPostFee("0");
        //订单状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭
        order.setStatus(1);
        //修改日期
        order.setUpdateTime(new Date());
        //创建日期
        order.setCreateTime(new Date());

        //保存
        orderMapper.insert(order);
        //****************************保存order对象***************end*************

        //****************************保存订单商品明细对象***************start*************
        //获取OrderItem对象
        List<TbOrderItem> orderItemList = orderInfo.getOrderItems();

        for (TbOrderItem orderItem : orderItemList) {

            //意思是从redis中获取，没有就直接设置值为1，以后每次自增1
            Long orderItemId = jedisDao.incr(ORDER_DETAIL_ID);

            //设置订单商品明细ID
            orderItem.setId(orderItemId.toString());
            //设置订单id
            orderItem.setOrderId(orderId);

            //保存
            orderItemMapper.insert(orderItem);
        }
        //****************************保存订单商品明细对象***************end*************

        //****************************保存收货地址对象***************start*************
        //获取收货地址对象
        TbOrderShipping orderShipping = orderInfo.getOrderShipping();

        orderShipping.setOrderId(orderId);
        orderShipping.setCreated(new Date());
        orderShipping.setUpdated(new Date());

        orderShippingMapper.insert(orderShipping);
        //****************************保存收货地址对象***************end*************

        return orderId;
    }
}
