$(function() {
	var appId = '';
	var timestamp = '';
	var nonceStr = '';
	var signature = '';
	var jsApiList = [];
	var DEVICE_ID = "gh_a397d76571e4_f85678b6c444defe";
	$.get('http://lock.dpdaidai.top/wechat/goReadCardAnniu', function(data,
			status) {
		console.log('data :' + data.data.url + "\nStatus: " + status);
		appId = data.data.appId;
		timestamp = data.data.timestamp;
		nonceStr = data.data.nonceStr;
		signature = data.data.signature;

		wx.config({
			beta: true,
			debug : true, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
			appId : appId, // 必填，公众号的唯一标识
			timestamp : timestamp, // 必填，生成签名的时间戳
			nonceStr : nonceStr, // 必填，生成签名的随机串
			signature : signature,// 必填，签名
			jsApiList : [ //需要调用的接口，都得在这里面写一遍      
			      "openWXDeviceLib" ,     
			      "closeWXDeviceLib",//关闭设备库（只支持蓝牙设备）      
			      "getWXDeviceInfos",//获取设备信息（获取当前用户已绑定的蓝牙设备列表）     
			      "getWXDeviceBindTicket",
			      "getWXDeviceUnbindTicket", 
			      "sendDataToWXDevice",//发送数据给设备      
			      "startScanWXDevice",//扫描设备（获取周围所有的设备列表，无论绑定还是未被绑定的设备都会扫描到）      
			      "stopScanWXDevice",//停止扫描设备      
			      "connectWXDevice",//连接设备      
			      "disconnectWXDevice",//断开设备连接      
			      "getWXDeviceTicket",//获取操作凭证      
			      
			      "chooseImage",
			      "uploadImage",
			      "downloadImage",
			      
			      //下面是监听事件：      
			      "onWXDeviceBindStateChange",//微信客户端设备绑定状态被改变时触发此事件      
			      "onWXDeviceStateChange",//监听连接状态，可以监听连接中、连接上、连接断开      
			      "onReceiveDataFromWXDevice",//接收到来自设备的数据时触发      
			      "onScanWXDeviceResult",//扫描到某个设备时触发      
			      "onWXDeviceBluetoothStateChange"//手机蓝牙打开或关闭时触发      
			    ]
		});
		// 判断调用jsapi返回状态 true表示成功
		wx.error(function(res) {
			alert("调用微信jsapi返回的状态:" + res.errMsg);
		});
		//初始化config成功
		wx.ready(function() {
			
			alert("准备好调用微信 jsapi");
			//检查api是否注册成功
			wx.checkJsApi({
				jsApiList : [ 'openWXDeviceLib', 'fdfdd' ], // 需要检测的JS接口列表，所有JS接口列表见附录2,
				success : function(res) {
					// 以键值对的形式返回，可用的api值true，不可用为false
					// 如：{"checkResult":{"chooseImage":true},"errMsg":"checkJsApi:ok"}
				},
				fail : function() {
					alert("xxx");
				}
			});
			
			//当扫描到设备
			wx.on('onScanWXDeviceResult',function(res){
				var device = "device : ";
				for(var index in res.devices){
					device = device +"{deviceId:"+ res.devices[index].deviceId + ",base64BroadcastData:" + res.devices[index].base64BroadcastData+"},\n";
				}
				device = device + "isCompleted:" +res.isCompleted;
				alert(device);
			});
			
		});
		


	});

	$("#CallGetWXrefresh").on("click", function(e) {
		wx.invoke('openWXDeviceLib', {}, function(res) {
			alert_obj(res);
		});
	});

	$("#getWXDeviceInfos").on("click",function(e){
		wx.invoke('getWXDeviceInfos',{},function(res){
			if(res.err_msg == "getWXDeviceInfos:ok"){
				var infos = "deviceInfos:";
				for(var info in res.deviceInfos){
					infos = infos + "\n" + "{deviceInfos : [{deviceId:"+res.deviceInfos[info].deviceId+",state:"+res.deviceInfos[info].state+"},"
				}
				alert(infos);
			}else{
				alert("getWXDeviceInfos:fail");
			}
			
		});
	});
	
	//开始扫描
	$("#startScanWXDevice ").on("click",function(e){
		wx.invoke('startScanWXDevice',"ble",function(res){
			alert_obj(res);
		});
	});

	//连接设备
	$("#connectWXDevice").on("click",function(e){
		wx.invoke("connectWXDevice",{'deviceId':DEVICE_ID},function(res){
			alert_obj(res);
		});
	});
	
	//断开连接
	$("#disconnectWXDevice").on("click",function(e){
		wx.invoke("disconnectWXDevice",{'deviceId':DEVICE_ID},function(res){
			alert_obj(res);
		});
	});
	
	//发送数据
	$("#sendDataToWXDevice").on("click",function(e){
		var cmdBytes = [0x06,0x01,0x01,0x01,0x5C,0x01,0x21,0x1F,
            						 0x29,0x1E,0x0F,0x4E,0x0C,0x13,0x28,0x25];
		alert(bytes_array_to_base64(cmdBytes));
		wx.invoke("sendDataToWXDevice",{'deviceId':DEVICE_ID,'base64Data':bytes_array_to_base64(cmdBytes)+""},function(res){
			alert_obj(res);
		});
	});
	/**
	 * 练习使用js sdk
	 * 上传 , 预览 和 下载图片
	 */
	$("#accessIMG").on("click", function(e){
		wx.previewImage({
			current: '', // 当前显示图片的http链接
			urls: ["http://lock.dpdaidai.top/yy.jpg","http://lock.dpdaidai.top/dd.jpg"] // 需要预览的图片http链接列表
			});
		wx.chooseImage({
			count: 1, // 默认9
			sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有
			sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有
			success: function (res) {
				var localIds = res.localIds; // 返回选定照片的本地ID列表，localId可以作为img标签的src属性显示图片
				
				wx.uploadImage({
					localId: localIds.pop(), // 需要上传的图片的本地ID，由chooseImage接口获得
					isShowProgressTips: 1, // 默认为1，显示进度提示
					success: function (res) {
						var serverId = res.serverId; // 返回图片的服务器端ID
						$.get("http://lock.dpdaidai.top/wechat/uploadWxImg?serverId="+serverId,function(res){
							alert_obj(res);
							$("#newIMG").attr("src","http://file.api.weixin.qq.com/cgi-bin/media/get?access_token="+res.data.token+"&media_id="+res.data.serverId );
						});
					}
				});
			}
		});
	});
	
});

function alert_obj(res) { 
	  var s = ""; 
	  for (var property in res) { 
	   s = s + "\n "+property +": " + res[property] ; 
	  } 
	  alert(s); 
	}

function my_openWXDeviceLib() {
	var x = 0;
	wx.invoke('openWXDeviceLib', {}, function(res) {

		alert_obj(res);
		/*
		 * bluetoothState:off
		 * maxVersion:1
		 * minVersion:11
		 * isSupportBLE:yes
		 * err_msg: openWXDeviceLib:ok
		 */
		
		if (res.err_msg == 'openWXDeviceLib:ok') {
			if (res.bluetoothState == 'off') {
				showdialog("太着急啦", "亲,使用前请先打开手机蓝牙！");
				$("#lbInfo").innerHTML = "1.请打开手机蓝牙";
				$("#lbInfo").css({
					color : "red"
				});
				x = 1;
				isOver();
			}
			;
			if (res.bluetoothState == 'unauthorized') {
				showdialog("出错啦", "亲,请授权微信蓝牙功能并打开蓝牙！");
				$("#lbInfo").html("1.请授权蓝牙功能");
				$("#lbInfo").css({
					color : "red"
				});
				x = 1;
				isOver();
			}
			;
			if (res.bluetoothState == 'on') {
				// showdialog("太着急啦","亲,请查看您的设备是否打开！");
				$("#lbInfo").html("1.蓝牙已打开,未找到设备");
				$("#lbInfo").css({
					color : "red"
				});
				// $("#lbInfo").attr(("style", "background-color:#000");
				x = 0;
				// isOver();
			}
			;
		} /*else {
			$("#lbInfo").html("1.微信蓝牙打开失败");
			x = 1;
			showdialog("微信蓝牙状态", "亲,请授权微信蓝牙功能并打开蓝牙！");
		}*/
	});
	return x; // 0表示成功 1表示失败
}

/*******************************************************************************
 * 取得微信设备信息 作者：V型知识库 www.vxzsk.com 2016-04-04 my_getWXDeviceInfos 入口参数：无
 * 出口参数：返回一个已经链接的设备的ID
 ******************************************************************************/
function my_getWXDeviceInfos() {

	wx.invoke('getWXDeviceInfos', {}, function(res) {
		var len = res.deviceInfos.length; // 绑定设备总数量
		for (i = 0; i <= len - 1; i++) {
			// alert(i + ' ' + res.deviceInfos[i].deviceId + ' '
			// +res.deviceInfos[i].state);
			if (res.deviceInfos[i].state === "connected") {
				$("#lbdeviceid").html(res.deviceInfos[i].deviceId);
				C_DEVICEID = res.deviceInfos[i].deviceId;
				$("#lbInfo").html("2.设备已成功连接");
				$("#lbInfo").css({
					color : "green"
				});

				break;
			}
		}

	});
	return;
}
// 打印日志 V型知识库 www.vxzsk.com
function mlog(m) {
	var log = $('#logtext').val();
	// log=log+m;
	log = m;
	$('#logtext').val(log);
}

/*******************************************************************************
 * 显示提示信息
 ******************************************************************************/
function showdialog(DialogTitle, DialogContent) {
	var $dialog = $("#Mydialog");
	$dialog.find("#dialogTitle").html(DialogTitle);
	$dialog.find("#dialogContent").html(DialogContent);
	$dialog.show();
	$dialog.find(".weui_btn_dialog").one("click", function() {
		$dialog.hide();
	});
}

/**
 *  Byte数组转Base64字符,原理同上 
 * @Param [0x00,0x00]
 * @return Base64字符串
 **/
function bytes_array_to_base64(array) {
    if (array.length == 0) {
        return "";
    }
    var b64Chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/';
    var result = "";
    // 给末尾添加的字符,先计算出后面的字符
    var d3 = array.length % 3;
    var endChar = "";
    if (d3 == 1) {
        var value = array[array.length - 1];
        endChar = b64Chars.charAt(value >> 2);
        endChar += b64Chars.charAt((value << 4) & 0x3F);
        endChar += "==";
    } else if (d3 == 2) {
        var value1 = array[array.length - 2];
        var value2 = array[array.length - 1];
        endChar = b64Chars.charAt(value1 >> 2);
        endChar += b64Chars.charAt(((value1 << 4) & 0x3F) + (value2 >> 4));
        endChar += b64Chars.charAt((value2 << 2) & 0x3F);
        endChar += "=";
    }
  
    var times = array.length / 3;
    var startIndex = 0;
    // 开始计算
    for (var i = 0; i < times - (d3 == 0 ? 0 : 1); i++) {
        startIndex = i * 3;
  
        var S1 = array[startIndex + 0];
        var S2 = array[startIndex + 1];
        var S3 = array[startIndex + 2];
  
        var s1 = b64Chars.charAt(S1 >> 2);
        var s2 = b64Chars.charAt(((S1 << 4) & 0x3F) + (S2 >> 4));
        var s3 = b64Chars.charAt(((S2 & 0xF) << 2) + (S3 >> 6));
        var s4 = b64Chars.charAt(S3 & 0x3F);
        // 添加到结果字符串中
        result += (s1 + s2 + s3 + s4);
    }
  
    return result + endChar;
}
 
