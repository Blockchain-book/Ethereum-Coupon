var checkList = [];
const items_recheck = 10;
function check(obj, s) {
    var pass_id = $(obj).parent().prop("id");
    var status;
    var opinion = $(obj).parent().children("textarea").val();
    var url = "../bank/bankStaffCheck.action";
    if(s == 1){
        status = "pass_apply_second";
        document.getElementById("re_distribution_text").innerHTML = "确定批准此结算券复审请求";
        $("#confirmReDistribution").click(function(){
            executeAjax(url, pass_id, status, opinion, s)
        });
    }
    else if(s == 2) {
        status = "refuse_apply_second";
        document.getElementById("re_distribution_text").innerHTML = "确定拒绝此结算券复审请求";
        $("#confirmReDistribution").click(function(){
            executeAjax(url, pass_id, status, opinion, s)
        });
    }
    else if(s == 3) {
        status = "pass_withdraw_second";
        document.getElementById("re_withdrawal_text").innerHTML = "确定批准此结算券提现复审请求";
        $("#confirmReWithdrawal").click(function(){
            executeAjax(url, pass_id, status, opinion, s)
        });
    }
    else if(s == 4) {
        status = "refuse_withdraw_second";
        document.getElementById("re_withdrawal_text").innerHTML = "确定拒绝此结算券提现复审请求";
        $("#confirmReWithdrawal").click(function(){
            executeAjax(url, pass_id, status, opinion, s)
        });
    }
}

function executeAjax(url, pass_id, status, opinion, s){
    $.ajax({
        type: "POST",
        url: url,
        data: {pass_id:pass_id, status:status,operatorOpinion:opinion},
        dataType: "json",
        success: function(data){
            var resultArray = eval(data.list);
            checkList = resultArray;
            loadReList(1,s);
        }
    });
}

function queryReCheckList() {
    var url = "../bank/queryReCheckSettlementList.action";
    $.ajax({
        type:"POST",
        url:url,
        dataType:"json",
        async:false,
        success:function (data) {
            var resultArray = eval(data.list);
            checkList = resultArray;
            loadReList(1,1);
        }
    });
}

function queryReWithdrawCheckList() {
    var url = "../bank/queryReWithdrawSettlementList.action";
    $.ajax({
        type:"POST",
        url:url,
        dataType:"json",
        async:false,
        success:function (data) {
            var resultArray = eval(data.list);
            checkList = resultArray;
            loadReList(1,3);
        }
    });
}

function loadReList(pager,s) {
    if( s==1 || s == 2) {
        $('#distribution-content').empty();   //清空resText里面的所有内容
        $('#pagination-reApplyCheck').empty();
    }
    if( s==3 || s == 4) {
        $('#withdrawal-content').empty();   //清空resText里面的所有内容
        $('#pagination-reWithDrawCheck').empty();
    }
    var page_number = parseInt(checkList.length/items_recheck);
    if(checkList.length%items_recheck > 0){
        page_number += 1;
    }
    var html = '';
    if(pager == page_number){
        for (var i = (pager-1)*items_recheck ; i < checkList.length ; i++){
            html += '<div class="panel panel-default">';
            html += '   <div class="panel-body">';
            html += '       <div class="pull-left">';
            html += '           <p>商户名：<span>' + checkList[i].merchantName + '</span></p>';
            html += '           <p>申请结算券金额：<span>' + checkList[i].operationAmount + '</span></p>';
            // html += '           <p>商户当前银行账户可用余额：<span>' +<%--${sfclist.settlementBalance}--%> + '</span></p>';
            html += '       </div>';
            html += '       <div class="col-md-4 pull-right" id=' + checkList[i].id + '>';
            if(s == 1 || s == 2) {
                html += '           <button type="button" data-toggle="modal" data-target="#myModal_re_distribution" onclick="check(this, 1)" class="btn btn-success w-45 js-check-success">通过</button>';
                html += '           <button type="button" data-toggle="modal" data-target="#myModal_re_distribution" onclick="check(this, 2)" class="btn btn-danger pull-right w-45 js-check-failed">拒绝</button>';
            }
            if(s == 3 || s == 4) {
                html += '           <button type="button" data-toggle="modal" data-target="#myModal_re_withdrawal" onclick="check(this, 3)" class="btn btn-success w-45 js-check-success">通过</button>';
                html += '           <button type="button" data-toggle="modal" data-target="#myModal_re_withdrawal" onclick="check(this, 4)" class="btn btn-danger pull-right w-45 js-check-failed">拒绝</button>';
            }
            html += '           <textarea name="operatorOpinion" class="form-control mt10" rows="3" placeholder="请输入审批意见"></textarea>';
            html += '       </div>';
            html += '   </div>';
            html += '</div>';
        }
    }else{
        for (var i = (pager-1)*items_recheck ; i < pager*items_recheck ; i++){
            html += '<div class="panel panel-default">';
            html += '   <div class="panel-body">';
            html += '       <div class="pull-left">';
            html += '           <p>商户名：<span>' + checkList[i].merchantName + '</span></p>';
            html += '           <p>申请结算券金额：<span>' + checkList[i].operationAmount + '</span></p>';
            // html += '           <p>商户当前银行账户可用余额：<span>' +<%--${sfclist.settlementBalance}--%> + '</span></p>';
            html += '       </div>';
            html += '       <div class="col-md-4 pull-right" id=' + checkList[i].id + '>';
            if(s == 1 || s == 2) {
                html += '           <button type="button" data-toggle="modal" data-target="#myModal_re_distribution" onclick="check(this, 1)" class="btn btn-success w-45 js-check-success">通过</button>';
                html += '           <button type="button" data-toggle="modal" data-target="#myModal_re_distribution" onclick="check(this, 2)" class="btn btn-danger pull-right w-45 js-check-failed">拒绝</button>';
            }
            if(s == 3 || s == 4) {
                html += '           <button type="button" data-toggle="modal" data-target="#myModal_re_withdrawal" onclick="check(this, 3)" class="btn btn-success w-45 js-check-success">通过</button>';
                html += '           <button type="button" data-toggle="modal" data-target="#myModal_re_withdrawal" onclick="check(this, 4)" class="btn btn-danger pull-right w-45 js-check-failed">拒绝</button>';
            }
            html += '           <textarea name="operatorOpinion" class="form-control mt10" rows="3" placeholder="请输入审批意见"></textarea>';
            html += '       </div>';
            html += '   </div>';
            html += '</div>';
        }
    }

    if( s==1 || s == 2) {
        $('#distribution-content').append(html);
        for(var i=0;i<page_number;i++){
            if(i == (pager-1)){
                $("#pagination-reApplyCheck").append('<li class="active" ><a onclick="loadReList('+(i+1)+',1)">'+(i+1)+'</a></li>');
            }else {
                $("#pagination-reApplyCheck").append('<li ><a onclick="loadReList('+(i+1)+',1)">'+(i+1)+'</a></li>');
            }
        }
    }
    if( s==3 || s == 4) {
        $('#withdrawal-content').append(html);
        for(var i=0;i<page_number;i++){
            if(i == (pager-1)){
                $("#pagination-reWithDrawCheck").append('<li class="active" ><a onclick="loadReList('+(i+1)+',3)">' + (i + 1) + '</a></li>');
            }else {
                $("#pagination-reWithDrawCheck").append('<li ><a onclick="loadReList('+(i+1)+',3)">' + (i + 1) + '</a></li>');
            }
        }
    }
}