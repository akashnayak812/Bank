<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Debug - Session Info</title>
</head>
<body>
    <h1>Debug Session Information</h1>
    
    <h2>Session Details:</h2>
    <%
        HttpSession sess = request.getSession(false);
        if (sess == null) {
            out.println("<p style='color:red;'>NO SESSION EXISTS</p>");
        } else {
            out.println("<p style='color:green;'>Session exists: " + sess.getId() + "</p>");
            out.println("<p>Session created: " + new java.util.Date(sess.getCreationTime()) + "</p>");
            out.println("<p>Last accessed: " + new java.util.Date(sess.getLastAccessedTime()) + "</p>");
            out.println("<p>Max inactive interval: " + sess.getMaxInactiveInterval() + " seconds</p>");
            
            out.println("<h3>Session Attributes:</h3>");
            out.println("<ul>");
            
            java.util.Enumeration<String> attrs = sess.getAttributeNames();
            boolean hasAttrs = false;
            while (attrs.hasMoreElements()) {
                hasAttrs = true;
                String attrName = attrs.nextElement();
                Object attrValue = sess.getAttribute(attrName);
                out.println("<li><strong>" + attrName + ":</strong> " + attrValue + "</li>");
            }
            
            if (!hasAttrs) {
                out.println("<li style='color:orange;'>No attributes in session</li>");
            }
            out.println("</ul>");
        }
    %>
    
    <hr>
    <a href="login.jsp">Go to Login</a> | 
    <a href="DashboardServlet">Go to Dashboard</a>
</body>
</html>
