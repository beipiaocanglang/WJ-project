var TTCart = {
	load : function(){ // 加载购物车数据
		
	},
	itemNumChange : function(){
		$(".increment").click(function(){//＋
            //获取当前点击的a标签的兄弟标签input标签
			var _thisInput = $(this).siblings("input");
			//获取input标签的value值(就是显示的数量)加1，再回显到页面
			_thisInput.val(eval(_thisInput.val()) + 1);
			//发请求
			$.post("/cart/update/num/"+_thisInput.attr("itemId")+"/"+_thisInput.val() + ".do",function(data){
			    //返回数据后刷新页面数量
				TTCart.refreshTotalPrice();
			});
		});
		$(".decrement").click(function(){//-
            //获取当前点击的a标签的兄弟标签input标签
			var _thisInput = $(this).siblings("input");
			//如果当前要改变的值已经等于1，就不让操作直接返回
			if(eval(_thisInput.val()) == 1){
				return ;
			}
            //获取input标签的value值(就是显示的数量)加1，再回显到页面
			_thisInput.val(eval(_thisInput.val()) - 1);
			//发请求
			$.post("/cart/update/num/"+_thisInput.attr("itemId")+"/"+_thisInput.val() + ".do",function(data){
                //返回数据后刷新页面数量
				TTCart.refreshTotalPrice();
			});
		});
		$(".quantity-form .quantity-text").rnumber(1);//限制只能输入数字
		$(".quantity-form .quantity-text").change(function(){
			var _thisInput = $(this);
			$.post("/service/cart/update/num/"+_thisInput.attr("itemId")+"/"+_thisInput.val(),function(data){
				TTCart.refreshTotalPrice();
			});
		});
	},
    //刷新页面要改变的值
	refreshTotalPrice : function(){ //重新计算总价
		var total = 0;
		$(".quantity-form .quantity-text").each(function(i,e){
			var _this = $(e);
			total += (eval(_this.attr("itemPrice")) * 10000 * eval(_this.val())) / 10000;
		});
		$(".totalSkuPrice").html(new Number(total/100).toFixed(2)).priceFormat({ //价格格式化插件
			 prefix: '￥',
			 thousandsSeparator: ',',
			 centsLimit: 2
		});
	}
};

$(function(){
	TTCart.load();
	TTCart.itemNumChange();
});