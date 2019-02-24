open in browser http://localhost:8080/testservlet

That resource is protected. A login-page will be displayed.
After Login with username=testuser and password=resutset (hardcoded), a hello world page will be displayed.
In the server-response, you will find the Autorisation-Header with the Bearer jwt.

Copy that jwt and call the url with curl:
```
curl -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsInVzZXJoYXNoIjoidGVzdHVzZXIiLCJyb2xlIjoiYWRtaW4tc2NyaXB0LG1hbmFnZXItc2NyaXB0LHRvbWNhdCxtYW5hZ2VyLWd1aSx0ZXN0cm9sZSxhZG1pbi1ndWkiLCJleHAiOjE1NTA5NzU0ODAsImlhdCI6MTU1MDk2MTA4MH0.x_W6fXdZRVxoHMcAuN5vw6ZTs8TiBP4mmJiZslwygX4" http://localhost:8080/testservlet/
  <html>
  <body>
  <h2>Hello World!</h2>
  </body>
  </html>
```

If the jwt is missing, the form-based login-page will take over:
```
curl -i http://localhost:8080/testservlet/
HTTP/1.1 200
Cache-Control: private
Expires: Thu, 01 Jan 1970 00:00:00 GMT
Set-Cookie: JSESSIONID=28BCC59922843E6704DA5E7CA7F342D4; Path=/testservlet; HttpOnly
Content-Type: text/html;charset=ISO-8859-1
Content-Length: 614
Date: Sat, 23 Feb 2019 22:43:51 GMT

<html>
<head>
<title>Login</title>
<body>
<form method="POST" action='j_security_check;jsessionid=28BCC59922843E6704DA5E7CA7F342D4' >
  <table border="0" cellspacing="5">
    <tr>
      <th align="right">Username:</th>
      <td align="left"><input type="text" name="j_username"></td>
    </tr>
    <tr>
      <th align="right">Password:</th>
      <td align="left"><input type="password" name="j_password"></td>
    </tr>
    <tr>
      <td align="right"><input type="submit" value="Log In"></td>
      <td align="left"><input type="reset"></td>
    </tr>
  </table>
</form>
</body>
</html>
```