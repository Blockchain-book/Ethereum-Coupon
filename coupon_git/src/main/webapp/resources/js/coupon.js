/**
 * Created by wxw on 2017/4/17.
 */
var couponArray = [];
const items = 4;
$(document).ready(function(){
    re_load_normal();
});
function re_load_normal(){
    $("#coupon_list").empty();
    $.ajax({
        url:"../bank/queryAllCouponMes.action",
        type:"POST",
        success:function(data){
            var result_list = eval(data.couponMes);
            couponArray = result_list;
            loadData("1");
        },
        async:"true",
        dataType:"json"
    });
}

function loadData(pager){
    pager = parseInt(pager);
    var page_number = parseInt(couponArray.length/items);
    if(couponArray.length%items>0){
        page_number += 1;
    }
    $("#pagination-coupon").empty();
    for(var i=0;i<page_number;i++){
        if(i == (pager-1)){
            $("#pagination-coupon").append('<li class="active" ><a onclick="loadData('+(i+1)+')">' + (i + 1) + '</a></li>');
        }else {
            $("#pagination-coupon").append('<li ><a onclick="loadData('+(i+1)+')">' + (i + 1) + '</a></li>');
        }
    }
    $("#coupon_list").empty();
    if(pager == page_number){
        for(var i=(pager-1)*items;i<couponArray.length;i++){
            t = '<div class="panel panel-default"><div class="panel-body">'+
                '   <div class="pull-left col-md-8">'+
                '       <p>优惠券id：<span>'+couponArray[i].id+'</span></p>';

            if (couponArray[i].status == "0") {
                t += '       <p>优惠券状态：<span>';
                t += '未发放';
            }
            else if (couponArray[i].status == "1") {
                t += '       <p>优惠券支付方：<span>' + couponArray[i].ownerAccount+ '</span></p>';
                t += '       <p>优惠券状态：<span>';
                t += '已使用';
            }
            else if (couponArray[i].status == "2") {
                t += '       <p>优惠券持有者：<span>' + couponArray[i].ownerAccount + '</span></p>';
                t += '       <p>优惠券状态：<span>';
                t += '未使用';
            }
            else if (couponArray[i].status == "3") {
                t += '       <p>优惠券持有者：<span>' + couponArray[i].ownerAccount + '</span></p>';
                t += '       <p>优惠券状态：<span>';
                t += '支付审核中';
            }
            t += '</span></p>';

            t += '      <p>优惠券发行方：<span>'+couponArray[i].name+'</span></p>';
            if("1" == couponArray[i].status) {

                t += '      <p>优惠券消费日期：<span>' + couponArray[i].consumptionDate + '</span></p>';
                t += '      <p>优惠券消费商家：<span>' + couponArray[i].consumeMerchant + '</span></p>';
            }
            else if("2" == couponArray[i].status || "3" == couponArray[i].status) {
                t += '      <p>优惠券获得日期：<span>' + couponArray[i].consumptionDate + '</span></p>';
            }
            t += '  </div>';
            t += '  <div class="col-md-4 pull-right">';
            t += '      <span style="font-size: 80px;color: #ff0000;">'+couponArray[i].couponValue+'</span>';
            t += '      <span style="font-size: 20px;">元</span>';
            t += '  </div>'
            t += '</div></div>';

            $("#coupon_list").append(t);
        }
    }else{
        for(var i=(pager-1)*items;i<pager*items;i++){
            t = '<div class="panel panel-default"><div class="panel-body">'+
                '   <div class="pull-left col-md-8">'+
                '       <p>优惠券id：<span>'+couponArray[i].id+'</span></p>';

            if (couponArray[i].status == "0") {
                t += '       <p>优惠券状态：<span>';
                t += '未发放';
            }
            else if (couponArray[i].status == "1") {
                t += '       <p>优惠券支付方：<span>' + couponArray[i].ownerAccount+ '</span></p>';
                t += '       <p>优惠券状态：<span>';
                t += '已使用';
            }
            else if (couponArray[i].status == "2") {
                t += '       <p>优惠券持有者：<span>' + couponArray[i].ownerAccount + '</span></p>';
                t += '       <p>优惠券状态：<span>';
                t += '未使用';
            }
            else if (couponArray[i].status == "3") {
                t += '       <p>优惠券持有者：<span>' + couponArray[i].ownerAccount + '</span></p>';
                t += '       <p>优惠券状态：<span>';
                t += '支付审核中';
            }
            t += '</span></p>';

            t += '      <p>优惠券发行方：<span>'+couponArray[i].name+'</span></p>';
            if("1" == couponArray[i].status) {

                t += '      <p>优惠券消费日期：<span>' + couponArray[i].consumptionDate + '</span></p>';
                t += '      <p>优惠券消费商家：<span>' + couponArray[i].consumeMerchant + '</span></p>';
            }
            else if("2" == couponArray[i].status || "3" == couponArray[i].status) {
                t += '      <p>优惠券获得日期：<span>' + couponArray[i].consumptionDate + '</span></p>';
            }
            t += '  </div>';
            t += '  <div class="col-md-4 pull-right">';
            t += '      <span style="font-size: 80px;color: #ff0000;">'+couponArray[i].couponValue+'</span>';
            t += '      <span style="font-size: 20px;">元</span>';
            t += '  </div>'
            t += '</div></div>';

            $("#coupon_list").append(t);
        }
    }
}

function search(obj){
    var merchantName = $($(obj).parent().prev().children()[0]).prop("value");
    $.ajax({
        url:"../bank/searchCoupon.action",
        type:"POST",
        async:false,
        data:{"merchantName":merchantName},
        dataType:"json",
        success:function(result){
            couponArray = result.list;
            loadData("1");
        }
    });
}