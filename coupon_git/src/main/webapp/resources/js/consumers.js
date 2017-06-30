var normalArray = [];
var abnormalArray = [];
var frozenArray = [];
const items = 1;
$(document).ready(function(){
    re_load_normal();
});
function re_load_normal(){
    $.ajax({
        url:"./bank/queryConsumerList.action",
        type:"POST",
        success:function(data){
            $("#consumer_list").empty();
            var result_list = eval(data.normalList);
            normalArray = result_list;
            loadData("1");
        },
        async:true,
        dataType:"json"
    });
}

function loadData(pager){
    pager = parseInt(pager);
    var page_number = parseInt(normalArray.length/items);
    if(normalArray.length%items>0){
        page_number += 1;
    }
    $("#pagination-normal").empty();
    for(var i=0;i<page_number;i++){
        if(i == (pager-1)){
            $("#pagination-normal").append('<li class="active" ><a onclick="loadData('+(i+1)+')">' + (i + 1) + '</a></li>');
        }else {
            $("#pagination-normal").append('<li ><a onclick="loadData('+(i+1)+')">' + (i + 1) + '</a></li>');
        }
    }
    $("#consumer_list").empty();
    if(pager == page_number){
        for(var i=(pager-1)*items;i<normalArray.length;i++){
            $("#consumer_list").append('<div class="panel panel-default"><div class="panel-body" ><div class="clo-md-4 pull-left">'+
                '<p>注册手机号：<span id="reg_phone">'+normalArray[i].account+'</span> </p>'+
                '<p>状态：<span id="consumer_state">'+'正常用户'+'</span></p>'+
                '</div>'+
                '<div class="clo-md-1 pull-left"><p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p></div>'+
                '<div class="clo-md-3 pull-left">'+
                '<p>持有优惠券：<span id="own_coupon">'+normalArray[i].totalOwn +' 元</span></p>'+
                '<p>消费记录：<span id="spent_coupon">'+normalArray[i].totalConsume+' 元</span></p>'+
                '<p>转赠优惠券：<span id="send_coupon">'+normalArray[i].totalSendOut+' 元</span></p>'+
                '</div>'+
                '<div class="col-md-4 pull-right">' +
                '<button type="button" class="btn btn-default pull-right w-45" data-toggle="modal" data-target="#myModal" onclick="markAbnormal(this)" id="' + normalArray[i].consumerId + '">标为异常用户</button>'+
                '</div></div></div>');
        }
    }else{
        for(var i=(pager-1)*items;i<pager*items;i++){
            $("#consumer_list").append('<div class="panel panel-default"><div class="panel-body" ><div class="clo-md-4 pull-left">'+
                '<p>注册手机号：<span id="reg_phone">'+normalArray[i].account+'</span> </p>'+
                '<p>状态：<span id="consumer_state">'+'正常用户'+'</span></p>'+
                '</div>'+
                '<div class="clo-md-1 pull-left"><p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p></div>'+
                '<div class="clo-md-3 pull-left">'+
                '<p>持有优惠券：<span id="own_coupon">'+normalArray[i].totalOwn +' 元</span></p>'+
                '<p>消费记录：<span id="spent_coupon">'+normalArray[i].totalConsume+' 元</span></p>'+
                '<p>转赠优惠券：<span id="send_coupon">'+normalArray[i].totalSendOut+' 元</span></p>'+
                '</div>'+
                '<div class="col-md-4 pull-right">' +
                '<button type="button" class="btn btn-default pull-right w-45" data-toggle="modal" data-target="#myModal" onclick="markAbnormal(this)" id="' + normalArray[i].consumerId + '">标为异常用户</button>'+
                '</div></div></div>');
        }
    }
}

function markAbnormal(obj){
    var id = $(obj).prop("id");
    $("#confirmMark").click(function(){
        $.ajax({
            url:"bank/markAbnormal.action",
            data:{"consumerId":id},
            dataType:"json",
            type:"POST",
            async:false,
            success:function(data){
                $("#"+id).prop("disabled",true);
                re_load_normal();
            }
        });});
}

function re_load_abnormal(){
    $("#abnormal_list").empty();
    $.ajax({
        url:"bank/queryAbnormalList.action",
        type:"POST",
        dataType:"json",
        async:true,
        success:function(data) {
            var result_list = eval(data.abnormalList);
            abnormalArray = result_list;
            load_abnormal("1");
        }
    });
}

function load_abnormal(pager){
    pager = parseInt(pager);
    var page_number = parseInt(abnormalArray.length/items);
    if(abnormalArray.length%items>0){
        page_number += 1;
    }
    $("#pagination-abnormal").empty();
    for(var i=0;i<page_number;i++){
        if(i == (pager-1)){
            $("#pagination-abnormal").append('<li class="active" ><a onclick="load_abnormal('+(i+1)+')">' + (i + 1) + '</a></li>');
        }else {
            $("#pagination-abnormal").append('<li ><a onclick="load_abnormal('+(i+1)+')">' + (i + 1) + '</a></li>');
        }
    }
    $("#abnormal_list").empty();
    if(pager == page_number){
        for(var i=(pager-1)*items;i<abnormalArray.length;i++){
            $("#abnormal_list").append('<div class="panel panel-default"><div class="panel-body" ><div class="clo-md-4 pull-left">'+
                '<p>注册手机号：<span>'+abnormalArray[i].account+'</span> </p>'+
                '<p>状态：<span id="consumer_state">'+'异常用户'+'</span></p>'+
                '</div>'+
                '<div class="clo-md-1 pull-left"><p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p></div>'+
                '<div class="clo-md-3 pull-left">'+
                '<p>持有优惠券：<span id="own_coupon">'+abnormalArray[i].totalOwn +' 元</span></p>'+
                '<p>消费记录：<span id="spent_coupon">'+abnormalArray[i].totalConsume+' 元</span></p>'+
                '<p>转赠优惠券：<span id="send_coupon">'+abnormalArray[i].totalSendOut+' 元</span></p>'+
                '</div>'+
                '<div class="col-md-4 pull-right">' +
                '<button name="mark_normal" type="button" class="btn btn-default pull-right w-45" data-toggle="modal" data-target="#myModal_ab" onclick="markNormal(this)" id="' + abnormalArray[i].consumerId + '">标为正常用户</button>'+
                '&nbsp;&nbsp;'+
                '<button name="freeze" type="button" class="btn btn-default pull-right w-45" data-toggle="modal" data-target="#myModal_ab" onclick="freeze(this)" id="' + abnormalArray[i].account + '">冻结</button>'+
                '</div></div></div>');
        }
    }else{
        for(var i=(pager-1)*items;i<pager*items;i++){
            $("#abnormal_list").append('<div class="panel panel-default"><div class="panel-body" ><div class="clo-md-4 pull-left">'+
                '<p>注册手机号：<span>'+abnormalArray[i].account+'</span> </p>'+
                '<p>状态：<span id="consumer_state">'+'异常用户'+'</span></p>'+
                '</div>'+
                '<div class="clo-md-1 pull-left"><p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p></div>'+
                '<div class="clo-md-3 pull-left">'+
                '<p>持有优惠券：<span id="own_coupon">'+abnormalArray[i].totalOwn +' 元</span></p>'+
                '<p>消费记录：<span id="spent_coupon">'+abnormalArray[i].totalConsume+' 元</span></p>'+
                '<p>转赠优惠券：<span id="send_coupon">'+abnormalArray[i].totalSendOut+' 元</span></p>'+
                '</div>'+
                '<div class="col-md-4 pull-right">' +
                '<button name="mark_normal" type="button" class="btn btn-default pull-right w-45" data-toggle="modal" data-target="#myModal_ab" onclick="markNormal(this)" id="' + abnormalArray[i].consumerId + '">标为正常用户</button>'+
                '&nbsp;&nbsp;'+
                '<button name="freeze" type="button" class="btn btn-default pull-right w-45" data-toggle="modal" data-target="#myModal_ab" onclick="freeze(this)" id="' + abnormalArray[i].account + '">冻结</button>'+
                '</div></div></div>');
        }
    }
}

function markNormal(obj){
    var id = $(obj).prop("id");
    document.getElementById("modal-text").innerHTML="是否确认将用户标为正常";
    $("#confirm_op").click(function(){
        $.ajax({
            url:"bank/markNormal.action",
            data:{"consumerId":id},
            dataType:"json",
            type:"POST",
            async:false,
            success:function(data){
                $("#"+id).prop("disabled",true);
                re_load_abnormal();
            }
        });}
    );
}

function freeze(obj){
    var id = $(obj).prop("id");
    document.getElementById("modal-text").innerHTML="是否确认将用户冻结";
    $("#confirm_op").click(function(){
        $.ajax({
            url:"bank/freezeExceptionAccount.action",
            data:{"account":id},
            dataType:"json",
            type:"POST",
            async:false,
            success:function(data){
                if(data.resultCode == "1"){
                    $("#"+id).prop("disabled",true);
                    re_load_abnormal();
                }else{
                    alert("处理失败！")
                }
            }
        });}
    );
}

function load_frozen_users(){
    $("#frozen_consumer_list").empty();
    $.ajax({
        url:"bank/queryFrozenConsumer.action",
        type:"POST",
        dataType:"json",
        async:false,
        success:function(data){
            var frozeList = eval(data.frozenConsumerList);
            frozenArray = frozeList;
            load_frozen("1");
        }
    });
}

function load_frozen(pager){
    var page_number = parseInt(frozenArray.length/items);
    if(frozenArray.length%items>0){
        page_number += 1;
    }
    $("#pagination-frozen").empty();
    for(var i=0;i<page_number;i++){
        if(i == (pager-1)){
            $("#pagination-frozen").append('<li class="active" ><a onclick="load_frozen('+(i+1)+')">' + (i + 1) + '</a></li>');
        }else {
            $("#pagination-frozen").append('<li ><a onclick="load_frozen('+(i+1)+')">' + (i + 1) + '</a></li>');
        }
    }
    $("#frozen_consumer_list").empty();
    if(pager == page_number){
        for(var i=(pager-1)*items;i<frozenArray.length;i++){
            $("#frozen_consumer_list").append('<div class="panel panel-default"><div class="panel-body" ><div class="clo-md-4 pull-left">'+
                '<p>注册手机号：<span>'+frozenArray[i].account+'</span> </p>'+
                '<p>状态：<span id="consumer_state">'+'异常用户'+'</span></p>'+
                '</div>'+
                '<div class="clo-md-1 pull-left"><p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p></div>'+
                '<div class="clo-md-3 pull-left">'+
                '<p>持有优惠券：<span id="own_coupon">'+frozenArray[i].totalOwn +' 元</span></p>'+
                '<p>消费记录：<span id="spent_coupon">'+frozenArray[i].totalConsume+' 元</span></p>'+
                '<p>转赠优惠券：<span id="send_coupon">'+frozenArray[i].totalSendOut+' 元</span></p>'+
                '</div>'+
                '<div class="col-md-4 pull-right">' +
                '<button name="thaw" type="button" class="btn btn-default pull-right w-45" data-toggle="modal" data-target="#myModal_thaw" onclick="thaw(this)" id="' + frozenArray[i].account + '">解冻</button>'+
                '</div></div></div>');
        }
    }else{
        for(var i=(pager-1)*items;i<pager*items;i++){
            $("#frozen_consumer_list").append('<div class="panel panel-default"><div class="panel-body" ><div class="clo-md-4 pull-left">'+
                '<p>注册手机号：<span>'+frozenArray[i].account+'</span> </p>'+
                '<p>状态：<span id="consumer_state">'+'异常用户'+'</span></p>'+
                '</div>'+
                '<div class="clo-md-1 pull-left"><p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p></div>'+
                '<div class="clo-md-3 pull-left">'+
                '<p>持有优惠券：<span id="own_coupon">'+frozenArray[i].totalOwn +' 元</span></p>'+
                '<p>消费记录：<span id="spent_coupon">'+frozenArray[i].totalConsume+' 元</span></p>'+
                '<p>转赠优惠券：<span id="send_coupon">'+frozenArray[i].totalSendOut+' 元</span></p>'+
                '</div>'+
                '<div class="col-md-4 pull-right">' +
                '<button name="thaw" type="button" class="btn btn-default pull-right w-45" data-toggle="modal" data-target="#myModal_thaw" onclick="thaw(this)" id="' + frozenArray[i].account + '">解冻</button>'+
                '</div></div></div>');
        }
    }
}

function thaw(obj){
    var id = $(obj).prop("id");
    $("#confirm_thaw").click(function(){
        $.ajax({
            url:"bank/thawExceptionAccount.action",
            data:{"account":id},
            dataType:"json",
            type:"POST",
            async:false,
            success:function(data){
                if(data.resultCode == "1"){
                    $("#"+id).prop("disabled",true);
                    load_frozen_users();
                }else{
                    alert("处理失败！")
                }
            }
        });}
    );
}

function search(obj){
    var phone = $($(obj).parent().prev().children()[0]).prop("value");
    var status = $(obj).prop("name");
    var pattern = /^1[34578]\d{9}$/;
    if(!pattern.test(phone)){
        return;
    }else{
        $.ajax({
            url:"bank/searchConsumer.action",
            type:"POST",
            async:false,
            data:{"account":phone,"status":status},
            dataType:"json",
            success:function(result){
                switch(status){
                    case "1":
                        normalArray = result.list;
                        loadData("1");
                        break;
                    case "2":
                        abnormalArray = result.list;
                        load_abnormal("1");
                        break;
                    case "3":
                        frozenArray = result.list;
                        load_frozen("1");
                        break;
                }
            }
        });
    }
}