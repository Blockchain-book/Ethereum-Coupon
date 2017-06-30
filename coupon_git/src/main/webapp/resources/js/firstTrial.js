function check(obj, s) {
    var status;
    var pass_id = $(obj).parent().prop("id");
    var opinion = $(obj).parent().children("textarea").val();
    var url = "../bank/bankStaffCheck.action";
    if(s == 1) {
        status = "pass_apply_first";
        document.getElementById("distribution_text").innerHTML = "确定批准此结算券初审请求";
        $("#confirmDistribution").click(function(){
            executeAjax(url, pass_id, status, opinion, s)
        });
    }
    else if(s == 2) {
        status = "refuse_apply_first";
        document.getElementById("distribution_text").innerHTML = "确定拒绝此结算券初审请求";
        $("#confirmDistribution").click(function(){
            executeAjax(url, pass_id, status, opinion, s)
        });
    }
    else if(s == 3) {
        status = "pass_withdraw_first";
        document.getElementById("withdrawal_text").innerHTML = "确定批准此结算券提现初审请求";
        $("#confirmWithdrawal").click(function(){
            executeAjax(url, pass_id, status, opinion, s)
        });
    }
    else if(s == 4) {
        status = "refuse_withdraw_first";
        document.getElementById("withdrawal_text").innerHTML = "确定拒绝此结算券提现初审请求";
        $("#confirmWithdrawal").click(function(){
            executeAjax(url, pass_id, status, opinion, s)
        });
    }
}

function executeAjax(url, pass_id, status, opinion, s){
    $.ajax({
        type: "POST",
        url: url,
        data: {"pass_id":pass_id, "status":status,"operatorOpinion":opinion},
        dataType: "json",
        success: function(data){
            var result_list = eval(data.list);
            checkList = result_list;
            loadList(1,s);
        }
    });
}

function queryCheckList() {
    var url = "../bank/queryFirstSettlementList.action";
    $.ajax({
        type: "POST",
        url:url,
        dataType:"json",
        success: function (data) {
            var result_list = eval(data.list);
            checkList = result_list;
            console.log(checkList);
            loadList(1,1);
        }
    });
}

function queryWithDrawCheckList() {
    var url = "../bank/queryFirstWithdrawSettlementList.action";
    $.ajax({
        type: "POST",
        url:url,
        dataType:"json",
        success: function (data) {
            var result_list = eval(data.list);
            checkList = result_list;
            console.log(checkList);
            loadList(1,3);
        }
    });
}

function loadList(pager,s) {
    if( s==1 || s == 2) {
        $('#distribution-content').empty();
        $('#pagination-firstApplyCheck').empty();
    }
    if( s==3 || s == 4) {
        $('#withdrawal-content').empty();
        $('#pagination-firstWithDrawCheck').empty();
    }
    var page_number = parseInt(checkList.length/items);
    if(checkList.length%items > 0){
        page_number += 1;
    }
    var html = '';
    if(pager == page_number){
        for (var i = (pager-1)*items ; i < checkList.length ; i++){
            html += '<div class="panel panel-default">';
            html += '   <div class="panel-body">';
            html += '       <div class="pull-left">';
            html += '           <p>商户名：<span>' + checkList[i].merchantName + '</span></p>';
            html += '           <p>申请结算券金额：<span>' + checkList[i].operationAmount + '</span></p>';
            // html += '           <p>商户当前银行账户可用余额：<span>' +<%--${sfclist.settlementBalance}--%> + '</span></p>';
            html += '       </div>';
            html += '       <div class="col-md-4 pull-right" id=' + checkList[i].id + '>';
            if(s == 1 || s == 2) {
                html += '           <button type="button" data-toggle="modal" data-target="#myModal_distribution" onclick="check(this, 1)" class="btn btn-success w-45 js-check-success">通过</button>';
                html += '           <button type="button" data-toggle="modal" data-target="#myModal_distribution" onclick="check(this, 2)" class="btn btn-danger pull-right w-45 js-check-failed">拒绝</button>';
            }
            if(s == 3 || s == 4) {
                html += '           <button type="button" data-toggle="modal" data-target="#myModal_withdrawal" onclick="check(this, 3)" class="btn btn-success w-45 js-check-success">通过</button>';
                html += '           <button type="button" data-toggle="modal" data-target="#myModal_withdrawal" onclick="check(this, 4)" class="btn btn-danger pull-right w-45 js-check-failed">拒绝</button>';
            }
            html += '           <textarea name="operatorOpinion" class="form-control mt10" rows="3" placeholder="请输入审批意见"></textarea>';
            html += '       </div>';
            html += '   </div>';
            html += '</div>';
        }
    }else{
        for (var i = (pager-1)*items ; i < pager*items ; i++){
            html += '<div class="panel panel-default">';
            html += '   <div class="panel-body">';
            html += '       <div class="pull-left">';
            html += '           <p>商户名：<span>' + checkList[i].merchantName + '</span></p>';
            html += '           <p>申请结算券金额：<span>' + checkList[i].operationAmount + '</span></p>';
            // html += '           <p>商户当前银行账户可用余额：<span>' +<%--${sfclist.settlementBalance}--%> + '</span></p>';
            html += '       </div>';
            html += '       <div class="col-md-4 pull-right" id=' + checkList[i].id + '>';
            if(s == 1 || s == 2) {
                html += '           <button type="button" onclick="check(this, 1)" class="btn btn-success w-45 js-check-success">通过</button>';
                html += '           <button type="button" onclick="check(this, 2)" class="btn btn-danger pull-right w-45 js-check-failed">拒绝</button>';
            }
            if(s == 3 || s == 4) {
                html += '           <button type="button" onclick="check(this, 3)" class="btn btn-success w-45 js-check-success">通过</button>';
                html += '           <button type="button" onclick="check(this, 4)" class="btn btn-danger pull-right w-45 js-check-failed">拒绝</button>';
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
                $("#pagination-firstApplyCheck").append('<li class="active" ><a onclick="loadList('+(i+1)+',1)">'+(i+1)+'</a></li>');
            }else {
                $("#pagination-firstApplyCheck").append('<li ><a onclick="loadList('+(i+1)+',1)">'+(i+1)+'</a></li>');
            }
        }
    }
    if( s==3 || s == 4) {
        $('#withdrawal-content').append(html);
        for(var i=0;i<page_number;i++){
            if(i == (pager-1)){
                $("#pagination-firstWithDrawCheck").append('<li class="active" ><a onclick="loadList('+(i+1)+',3)">' + (i + 1) + '</a></li>');
            }else {
                $("#pagination-firstWithDrawCheck").append('<li ><a onclick="loadList('+(i+1)+',3)">' + (i + 1) + '</a></li>');
            }
        }
    }
}