package study.project.order.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import study.project.CookieUtils;
import study.project.ItemService;
import study.project.JsonUtils;
import study.project.domain.TbItem;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 购物车操作
 * Created by panhusun on 2017/9/9.
 */
@Controller
public class CartController {

    @Value("${CART_KEY}")
    private String CART_KEY;

    @Resource
    private ItemService itemService;

    /**
     * 功能21：
     *      添加购物车(未登录 添加到cookie)
     * URL:
     *      /cart/add/${item.id}/" + $("#buy-num").val() + ".html
     * param：
     *      URL模板映射
     *      itemId
     *      num
     * return：
     *      string 返回到购物车添加成功页面
     * 业务流程：
     *      先查询购物车(cookie)商品列表
     *      判断购物车中是否有当前有添加的商品
     *      有：数量相加
     *      没有：直接添加到cookie购物车
     */
    @RequestMapping("/cart/add/{itemId}/{num}")
    public String addCart(HttpServletRequest request, HttpServletResponse response, @PathVariable Long itemId, @PathVariable Long num){

        //查询购物车
        List<TbItem> itemList = this.getCookieValue(request);

        //判断购物车中是否有当前有添加的商品
        boolean flag = false;

        for (TbItem tbItem : itemList) {
            if (tbItem.getId() == itemId.longValue()) {
                //存在，数量相加
                tbItem.setNum(tbItem.getNum() + num.intValue());

                flag = true;

                break;
            }
        }

        //不存在
        if (!flag) {
            //根据itemId查询数据库
            TbItem item = itemService.findItemByID(itemId);

            //设置购买数量
            item.setNum(num.intValue());

            //放回购物车列表
            itemList.add(item);
        }

        //将购物车列表加密存入cookie
        CookieUtils.setCookie(request, response, CART_KEY, JsonUtils.objectToJson(itemList), true);

        //返回到购物车成功页面cartSuccess
        return "cartSuccess";
    }

    //查询cookie中的购物车列表
    private List<TbItem> getCookieValue(HttpServletRequest request) {

        //根据key加密查询cookie
        String cookieValue = CookieUtils.getCookieValue(request, CART_KEY, true);

        if (StringUtils.isBlank(cookieValue)) {
            //如果为空返回一个空列表
            return new ArrayList<>();
        }

        //将json转成list集合
        List<TbItem> tbItems = JsonUtils.jsonToList(cookieValue, TbItem.class);

        return tbItems;
    }
}
