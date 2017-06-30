var merchants = [];
const items = 1;
$(document).ready(function(){
    re_load();
});
function re_load(){
    $.ajax({
        url:"../bank/queryUncheckMerchants.action",
        type:"POST",
        dataType:"json",
        async:false,
        success:function(data){
            $("#merchants_list").empty();
            var result_array = eval(data.uncheckedMerchantList);
            merchants = result_array;
            load_merchant(1);
        }
    });
}

function load_merchant(pager) {
    var page_number = parseInt(merchants.length/items);
    if(merchants.length%items>0){
        page_number += 1;
    }

    $("#pagination-merchants").empty();
    for(var i=0;i<page_number;i++){
        if(i == (pager-1)){
            $("#pagination-merchants").append('<li class="active" ><a onclick="load_merchant('+(i+1)+')">' + (i + 1) + '</a></li>');
        }else {
            $("#pagination-merchants").append('<li ><a onclick="load_merchant('+(i+1)+')">' + (i + 1) + '</a></li>');
        }
    }
    $("#merchants_list").empty();
    if(pager == page_number){
        for(var i=(pager-1)*items;i<merchants.length;i++){
            $("#merchants_list").append('<div class="panel panel-default">'+
                '<div class="panel-heading">'+merchants[i].name+'</div>'+
                '<div class="panel-body">'+
                '<div class="col-md-4">'+
                '<p><b>商户注册信息：</b></p>'+
                '<p hidden><span id="merchant_id">'+merchants[i].id+'</span></p>'+
                '<p>注册手机号：<span>'+merchants[i].account+'</span></p>'+
                '<p>营业执照号：<span>'+merchants[i].licence+'</span></p>'+
                '<p>注册地址：<span>'+merchants[i].address+'</span></p>'+
                '<p>经营范围：<span>'+merchants[i].businessScope+'</span></p>'+
                '<p>法人姓名：<span>'+merchants[i].legalEntityName+'</span></p>'+
                '</div>'+
                '<div class="pull-right col-md-4" id="'+merchants[i].id+'">'+
                '<textarea style="padding-top: 20px; padding-bottom: 20px; margin-bottom: 20px;" class="form-control mt10" rows="3" placeholder="请输入审批意见"></textarea>'+
                '<button type="button" data-toggle="modal" data-target="#myModal_audit" class="btn btn-success w-45" onclick="pass_check(this)">通过</button>'+
                '<button type="button" data-toggle="modal" data-target="#myModal_audit" class="btn btn-danger pull-right w-45" onclick="refuse_check(this)" >拒绝</button>'+
                '</div> </div> </div>');
        }
    }else{
        for(var i=(pager-1)*items;i<pager*items;i++){
            $("#merchants_list").append('<div class="panel panel-default">'+
                '<div class="panel-heading">'+merchants[i].name+'</div>'+
                '<div class="panel-body">'+
                '<div class="col-md-4">'+
                '<p><b>商户注册信息：</b></p>'+
                '<p hidden><span id="merchant_id">'+merchants[i].id+'</span></p>'+
                '<p>注册手机号：<span>'+merchants[i].account+'</span></p>'+
                '<p>营业执照号：<span>'+merchants[i].licence+'</span></p>'+
                '<p>注册地址：<span>'+merchants[i].address+'</span></p>'+
                '<p>经营范围：<span>'+merchants[i].businessScope+'</span></p>'+
                '<p>法人姓名：<span>'+merchants[i].legalEntityName+'</span></p>'+
                '</div>'+
                '<div class="pull-right col-md-4" id="'+merchants[i].id+'">'+
                '<textarea style="padding-top: 20px; padding-bottom: 20px; margin-bottom: 20px;" class="form-control mt10" rows="3" placeholder="请输入审批意见"></textarea>'+
                '<button type="button" class="btn btn-success w-45" data-toggle="modal" data-target="#myModal_audit" onclick="pass_check(this)">通过</button>'+
                '<button type="button" class="btn btn-danger pull-right w-45" data-toggle="modal" data-target="#myModal_audit" onclick="refuse_check(this)" >拒绝</button>'+
                '</div> </div> </div>');
        }
    }
}

function pass_check(obj) {
    var id = $($(obj).parent()).prop("id");
    var opinion = $(obj).parent().children("textarea").val();
    document.getElementById("audit_text").innerHTML = "是否确定通过该商户的注册申请";
    $("#confirmAudit").click(function(){
        $.ajax({
            url:"../bank/merchantPass.action",
            type:"POST",
            data:{"merchantId":id,"status":"1","operationOpinion":opinion},
            success:function(result){
                re_load();
            },
            async:false,
            dataType:"json"
        });
    });
}

function refuse_check(obj) {
    var id = $($(obj).parent()).prop("id");
    var opinion = $(obj).prev().prev().val();
    document.getElementById("audit_text").innerHTML = "是否确定拒绝该商户的注册申请";
    $("#confirmAudit").click(function(){
        $.ajax({
            url:"../bank/merchantPass.action",
            type:"POST",
            data:{"merchantId":id,"status":"1","operationOpinion":opinion},
            success:function(result){
                re_load();
            },
            async:false,
            dataType:"json"
        });
    });
}