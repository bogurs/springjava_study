$(document).ready(function(){
	$('#comm_show').click(function(){
		$.ajax({
			type : "POST",
			url:"/test/commentRead.ktds",
			async : true,
			dataType : "json",
			data:{
				article_num:$('#article_num').val()
			},
			success : function(data){
				//json으로 넘어오므로 파싱이 필요없음 data=JSON.parse(data);
				var html = '<table border="1">';
				$.each(data, function(entryIndex, entry){
					html += '<tr>';
					html += '<td>' + entry.comment_num + '</td>';
					html += '<td>' + entry.id + '</td>';
					html += '<td>' + entry.comment_content + '</td>';
					html += '<td>' + entry.comment_date + '</td>';
					html += '<td> <input type="button" value="댓글삭제"> </td>';
					html += '</tr>';
				});
				html += '</table>';
				$("#show_comment").html(html);
			},
			error : function(xhr){
				alert("error html = " + xhr.statusText);
			}
		});
	});
	
	$('#comm_write').click(function(){
		$.ajax({
			type : "POST",
			url : "/test/commentWrite.ktds",
			async : true,
			dataType : "json",
			data:{
				article_num:$('#article_num').val(),
				comment_content:$("#comment_content").val()
			},
			success : function(data){
				//json으로 넘어오므로 파싱이 필요없음 data=JSON.parse(data);
				var html = '<table border="1">';
				$.each(data, function(entryIndex, entry){
					html += '<tr>';
					html += '<td>' + entry.comment_num + '</td>';
					html += '<td>' + entry.id + '</td>';
					html += '<td>' + entry.comment_content + '</td>';
					html += '<td>' + entry.comment_date + '</td>';
					html += '<td> <input type="button" value="댓글삭제" id="comm_delete"> </td>';
					html += '</tr>';
				});
				html += '</table>';
				$("#show_comment").html(html);
				$("#comment_content").text("");
			},
			error : function(xhr){
				alert("error html = " + xhr.statusText);
			}
		});
	});
	
});
	
