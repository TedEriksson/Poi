$(document).ready(function() {
	$('#get').bind('click',function() {
		$.get('http://poi.dev/api/points/'+$('#pointID').val(), function(data) {
			var point = $.parseJSON(data);
			var string = "";
			if (point.length == 0) {
				string = "ID not valid";
			} else {
				for (var i = 0; i < point.length; i++) {
					string += "ID: "+point[i].id+ "<br>Name: "+point[i].name+"<br>Message: "+point[i].message+"<br>Lng: " +point[i].lng+"<br>Lat: "+point[i].lat+"<br><br>";
				};
				$("#pointID").val(point[0].id);
				$("#pointName").val(point[0].name);
				$("#pointLng").val(point[0].lng);
				$("#pointLat").val(point[0].lat);
				$("#pointMsg").val(point[0].message);
			}
			$('#results').html(string);
		});
	});

	$('#update').bind('click', function() {
		var postData = {	id : $("#pointID").val(),
							name : $("#pointName").val(),
							lng : $("#pointLng").val(),
							lat : $("#pointLat").val(),
							message : $("#pointMsg").val()};

		$.ajax({
			url: 'http://poi.dev/api/points/'+$('#pointID').val(),
			type: "POST",
			data: JSON.stringify(postData),
			processData: true,
  			dataType:"json",
			success: function(data) {
				alert("Updated");
				$('#results').html(data);
			}
		});
	});
});
