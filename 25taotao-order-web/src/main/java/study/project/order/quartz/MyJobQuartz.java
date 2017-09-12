package study.project.order.quartz;

/**
 * 定时器测试
 * Created by canglang on 2017/9/12.
 */
public class MyJobQuartz {

    public void clearInvalidOrders(){
        //每一天定时检查设备是否异常，一旦发现异常每天定时(晚上十二点)给运维或者系统维护人员发送邮件
        // 定时清空无效订单
        // 定时清空无效图片
        // 定时清空无效静态页面（商品下架）

        System.out.println("自定义定时器每5秒钟执行一次！！！！！！！！！！！！！！！！！！");
    }
}
