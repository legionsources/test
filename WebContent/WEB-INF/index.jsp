<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Panele</title>
        <script
            src="http://ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js">
        </script>
        <script>
        $(document).ready(function() {
        	function getUsers() {
                $.ajax({
                    type : 'GET',
                    url: 'test',
                    dataType : 'json',
                    success: function(response) {
                        makeTable(response);
                    },
                    error: function(e) {
                        alert("Error in retrieving data: " + e);
                    }
                });
            }
        	
        	function makeTable(data) {
        		var div = $('.fromAjax').empty();
        		div.append('<h2>All users from AJAX</h2>');      
        		var table='<table border="1"> <th>ID</th><th>Full Name</th><th>Screen Name</th> <th>Email</th>';
          		$.each(data.userList, function(index) {
          			table += '<tr>';
           			$.each(data.userList[index], function(key, value) {
           				table += '<td width="200px">' + value + '</td>';
                  	});
          		    table += '</tr>';
     
                });
          		table += '</table>';
          		div.append(table);
        	}
        	
        	$('.showUsers').on("click", function() {
        	    getUsers();
        	});
        	
        });
        </script>
</head>
<body>
    <h2> doPost - add user</h2>
	<form method="post" action="servletas">
        <table> 
            <tr>
                <td>Pilnas vardas: </td>
                <td><input name="fullName"></td>
            </tr>
            <tr>
               <td>Screen vardas: </td>
               <td><input name="screenName"></td>
            </tr>
            <tr>
                <td>Email adresas: </td>
                <td><input name="email"></td>
            </tr>
        </table>
        <input type="submit" value="Insert User">
    </form>
    
    <h2> doGet - get all users</h2>
    <form method="get" action="servletas">
         <input name="getButton" type="submit" value="Get Users (doGet)">
    </form>
    
    <h2> ajax - get all users</h2>
    <input class="showUsers" type="submit" value="Get Users (AJAX)">
    
    <c:if test="${!empty dataList}">
        <h2>All users from doGet</h2>        
        <table id="dataTable" border="1">
            <tr>
                <th>ID</th>
                <th>Full Name</th>
                <th>Screen Name</th>
                <th>Email</th>
            </tr>
                    
            <c:forEach items="${dataList}" var="user">
                <tr>
                    <td width="200px">${user.getId()}</td>
                    <td width="200px">${user.getFullName()}</td>
                    <td width="200px">${user.getScreenName()}</td>
                    <td width="200px">${user.getEmail()}</td>
                    <td width="100px">
                        <form method="post" action="servletas">
                            <input name="dell" type="hidden" value="${user.getId()}">
                            <input name="dellButton" type="submit" value="delete">
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:if>
    
    <div class="fromAjax" >
       
    </div>

</body>
</html>