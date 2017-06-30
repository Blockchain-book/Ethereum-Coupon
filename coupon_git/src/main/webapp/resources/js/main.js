// config
var ORIGIN = 'http://localhost:8080'
var DOMAIN = 'http://localhost:8080/coupon/Front'
var BANK_STAFF_ID = ''
// var BANK_STAFF_ID = CHECK_FLAG == 1 ? 'ff2e1a22-7619-11e6-b3f2-1feb3f769586' : '324b01a4-761a-11e6-b3f2-1feb3f769586'
if (Cookies.get('staff_id')) {
  BANK_STAFF_ID = Cookies.get('staff_id')
}

// boostrap操作
$('#sub-tab-title a').click(function (e) {
  e.preventDefault()
  $(this).tab('show')
})

// ajax
function getInfo (url, data, cb) {
  $.get(url, data).complete(function (res) {
    cb(res.responseJSON)
  }, 'json')
}

function postStatus (url, data, cb) {
  $.post(url, data).complete(function (res) {
    cb(res.responseJSON)
  }, 'json')
}

// 商户审批列表
function listApprovals() {
  var url = ORIGIN + '/coupon/bank/queryUncheckMerchants.action'
  var data = {}
  function cb (data) {
    html = ''
    data.forEach(function (item) {
    html +=  '<div class="panel panel-default">'
    html +=  '  <div class="panel-heading">' + item.name + '</div>'
    html +=  '  <div class="panel-body">'
    html +=  '    <div class="pull-left">'
    html +=  '      <p>商户注册信息：</p>'
    html +=  '      <p>注册手机号：<span>' + item.account + '</span></p>'
    html +=  '      <p>营业执照号：<span>' + item.licence + '</span></p>'
    html +=  '      <p>注册地址：<span>' + item.address + '</span></p>'
    html +=  '    </div>'
    html +=  '    <div class="col-md-4 pull-right">'
    html +=  '      <form action="/coupon/bank/updateMerchantRegisterInfo.action" id="'+ item.id +'">'
    html +=  '        <button type="button" onclick="approvalCheck(1, event)" class="btn btn-success w-45">通过</button>'
    html +=  '        <button type="button" onclick="approvalCheck(-1, event)" class="btn btn-danger pull-right w-45">拒绝</button>'
    html +=  '        <textarea class="form-control mt10" rows="3" placeholder="请输入审批意见"></textarea>'
    html +=  '      </form>'
    html +=  '    </div>'
    html +=  '  </div>'
    html +=  '</div>'
    })
    $('#audit-content').html(html)
  }
  getInfo(url, data, cb)
}

// 更新商户注册请求
function approvalCheck (status, e) {
  var f = $(e.srcElement).parent()
  var url = ORIGIN + f.attr('action')
  var data = {
    'id': f.attr('id'),
    'status': status
  }
  function cb (res) {
    $('#alert-bar').show()
    p = f.parents('.panel')
    p.fadeOut()
  }
  postStatus(url, data, cb)
}


// 结算券请求列表
function listApplications () {
  var url = ORIGIN + '/coupon/bank/querySettlementApplicationList.action'
  var data = {
    // 'bankStaffId': BANK_STAFF_ID
    'id': BANK_STAFF_ID
  }
  function cb (data) {
    html = ''
    data.forEach(function (item) {
      html += '<div class="panel panel-default">'
      html += '  <div class="panel-body">'
      html += '    <div class="pull-left">'
      html += '      <p>商户名：<span>'+ item.merchantName +'</span></p>'
      html += '      <p>申请结算券金额：<span>'+ item.operationAmount +'</span></p>'
      html += '      <p>商户当前银行账户可用余额：<span>xxxxxx</span></p>'
      html += '      <p>申请时间：<span>'+ item.operationDate +'</span></p>'
      html += '    </div>'
      html += '    <div class="col-md-4 pull-right">'
      if (CHECK_FLAG == 1) {
        html += '      <form action="/coupon/bank/scFirstCheck.action" id="'+ item.id +'">'
      } else {
        html += '      <form action="/coupon/bank/scRecheck.action" id="'+ item.id +'">'
      }
      html += '        <input type="hidden" name="merchantId" value="'+ item.merchantId +'">'
      html += '        <input type="hidden" name="operationAmount" value="'+ item.operationAmount +'">'
      html += '        <button type="button" onclick="applicationCheck(2, event)" class="btn btn-success w-45 js-check-success">通过</button>'
      html += '        <button type="button" onclick="applicationCheck(0, event)" class="btn btn-danger pull-right w-45 js-check-failed">拒绝</button>'
      html += '        <textarea name="operatorOpinion" class="form-control mt10" rows="3" placeholder="请输入审批意见"></textarea>'
      html += '      </form>'
      html += '    </div>'
      html += '  </div>'
      html += '</div>'
    })
    $('#distribution-content').html(html)
  }
  getInfo(url, data, cb)
}

// 结算券审核
function applicationCheck (status, e) {
  var f = $(e.srcElement).parent()
  var url = ORIGIN + f.attr('action')
  // var checkStatus = status
  var data = {
    'id': f.attr('id'),
    'bankStaffId': BANK_STAFF_ID
  }
  if (CHECK_FLAG == 1) {
    data.operatorOpinion = f.children('[name="operatorOpinion"]').val()
    data.checkStatus = status
  } else {
    data.merchantId = f.children('[name="merchantId"]').val()
    data.operationAmount = f.children('[name="operationAmount"]').val()
    data.recheckOpinion = f.children('[name="operatorOpinion"]').val()
    data.recheckStatus = status
  }
  function cb (res) {
    $('#alert-bar').show()
    p = f.parents('.panel')
    p.fadeOut()
  }
  postStatus(url, data, cb)
}


// 提现请求列表
function listWithdrawals () {
  var url = ORIGIN + '/coupon/bank/querySettlementWdList.action'
  var data = {
    // 'bankStaffId': BANK_STAFF_ID
    'id': BANK_STAFF_ID
  }
  function cb (data) {
    html = ''
    data.forEach(function (item) {
      html += '<div class="panel panel-default">'
      html += '  <div class="panel-body">'
      html += '    <div class="pull-left">'
      html += '      <p>商户名：<span>'+ item.merchantName +'</span></p>'
      html += '      <p>结算券提现金额：<span>'+ item.operationAmount +'</span></p>'
      html += '      <p>商户当前银行账户可用余额：<span>xxxxxx</span></p>'
      html += '      <p>申请时间：<span>'+ item.operationDate +'</span></p>'
      html += '    </div>'
      html += '    <div class="col-md-4 pull-right">'
      if (CHECK_FLAG == 1) {
        html += '    <form action="/coupon/bank/wdFirstCheck.action" id="'+ item.id +'">'
      } else {
        html += '    <form action="/coupon/bank/wdRecheck.action" id="'+ item.id +'">'
      }
      html += '        <input type="hidden" name="merchantId" value="'+ item.merchantId +'">'
      html += '        <input type="hidden" name="operationAmount" value="'+ item.operationAmount +'">'
      html += '        <button type="button" onclick="withdrawalCheck(2, event)" class="btn btn-success w-45 js-check-success">通过</button>'
      html += '        <button type="button" onclick="withdrawalCheck(0, event)" class="btn btn-danger pull-right w-45 js-check-failed">拒绝</button>'
      html += '        <textarea name="operatorOpinion" class="form-control mt10" rows="3" placeholder="请输入审批意见"></textarea>'
      html += '      </form>'
      html += '    </div>'
      html += '  </div>'
      html += '</div>'
    })
    $('#withdrawal-content').html(html)
  }
  getInfo(url, data, cb)
}

// 结算券提现审核
function withdrawalCheck (status, e) {
  var f = $(e.srcElement).parent()
  var url = ORIGIN + f.attr('action')
  var data = {
    'id': f.attr('id'),
    'bankStaffId': BANK_STAFF_ID
  }
  if (CHECK_FLAG == 1) {
    data.operatorOpinion = f.children('[name="operatorOpinion"]').val()
    data.checkStatus = status
  } else {
    data.merchantId = f.children('[name="merchantId"]').val()
    data.operationAmount = f.children('[name="operationAmount"]').val()
    data.recheckOpinion = f.children('[name="operatorOpinion"]').val()
    data.recheckStatus = status
  }
  function cb (res) {
    $('#alert-bar').show()
    p = f.parents('.panel')
    p.fadeOut()
  }
  postStatus(url, data, cb)
}

// 登录注册

// 登录注册切换
$('#check-to-signin').click(function () {
  $('#signup-box').hide()
  $('#signin-box').show()
  SIGN_MODE = 2
})
$('#check-to-signup').click(function () {
  $('#signin-box').hide()
  $('#signup-box').show()
  SIGN_MODE = 1
})

// 注册
$('#btn-signup').click(function () {
  if ($('#password').val() != $('#repassword').val()){
    alert('两次输入密码不一致，请重新输入')
    return false
  }
  var url = ORIGIN + '/coupon/bank/register.action'
  var data = {
    'account': $('#username').val(),
    'password': $('#password').val(),
    'position': $('[name="positionOptions"]:checked').val()
  }
  function cb(res) {
    console.log(res)
    if (res.resultCode) {
      alert('注册成功');
      window.location.reload();
    }
  }
  postStatus(url, data, cb)
})
// 登录
$('#btn-signin').click(function () {
  var url = ORIGIN + '/coupon/bank/login.action'
  var data = {
    'account': $('#username').val(),
    'password': $('#password').val()
  }
  function cb(res) {
    console.log(res)
    if (res.resultCode) {
      Cookies.set('staff_id', res.id);
      if (res.position == 1) {
        window.location.href = DOMAIN + '/index.html';
      } else if (res.position == 2) {
        window.location.href = DOMAIN + '/recheck.html';
      }
    }
  }
  postStatus(url, data, cb)
})