$(function() {
	$('a.delete').click(function(e) {
		if(confirm('是否删除?')) {
			var href = $(this).attr('href')
			$.ajax({
				type: 'DELETE',
				url: href,
				success: function() {
					document.location.reload()
				}
			});
		}
		e.preventDefault();
		return false
	});
	
	$("#logoutbtn").click(function(){
		if (confirm('是否登出?')) {
			$.ajax({
				type: 'GET',
				url: "/user/logout",
				success: function() {
					document.location.reload()
				}
			});
		}
		
	});
	
});