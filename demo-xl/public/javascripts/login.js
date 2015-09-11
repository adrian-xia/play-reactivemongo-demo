$(function(){
	
	$("#loginbtn").click(function(){
		$('#error').html("");
		if (checkForm()) {
			$("#loginForm").submit();
		}else{
			return false;
		}
	});
	
	function checkForm(){
	     if ($('#username').val() == "") {
	        $('#error').html("用户名不能为空");
	        $("#username").focus();
	        return false;
	    }
	    if ($('#password').val() == "") {
	        $('#error').html("密码不能为空");
	        $("#password").focus();
	        return false;
	    }
	    return true;
	}
	
});