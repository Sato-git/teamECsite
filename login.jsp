<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" type="text/css" href="./css/login.css">
<link rel="stylesheet" type="text/css" href="./css/header.css">
<title>ログイン画面</title>
</head>
<body>

	<div id="header">
		<jsp:include page="header.jsp" />
	</div>
	<div id="main">

		<div id="top">
			<h1>ログイン画面</h1>
		</div>
		<div id="contents">
			<s:if test="errorMessageList1.size()>0">
				<div class="error">
					<s:iterator value="errorMessageList1">
						<s:property /><br>
					</s:iterator>
				</div>
			</s:if>
			<s:if test="errorMessageList2.size()>0">
				<div class="error">
					<s:iterator value="errorMessageList2">
						<s:property /><br>
					</s:iterator>
				</div>
			</s:if>
			<s:if test="!isNotUserInfoMessage.isEmpty()">
				<div class="error2">
					<s:iterator value="isNotUserInfoMessage">
						<s:property />
					</s:iterator>
				</div>
			</s:if>

			<s:form action="LoginAction">
				<table>
					<!-- ユーザーID保存にチェックを入れていた場合 -->

					<s:if test="#session.saved_user_id==true">

						<tr>
							<th>ユーザーID</th>
							<td><s:textfield type="text" name="userId" value='%{session.user_id}' size="50" class="text" placeholder="ユーザーID"/></td>
						</tr>
						<tr>
							<th>パスワード</th>
							<td><s:password name="password" value="password" size="50" class="text" placeholder="パスワード"/></td>
						</tr>

					</s:if>


					<!-- ユーザーID保存にチェックを入れていなかった場合 -->

					<s:else>
						<tr>
							<th>ユーザーID</th>
							<td><s:textfield name="userId" value='%{userId}' /></td>
						</tr>
						<tr>
							<th>パスワード</th>
							<td><s:password name="password" value="" /></td>
						</tr>
					</s:else>
				</table>
				<!--ユーザーID保存  -->
				<br>

				<div class="userIdCheckBox"><input type="checkbox" name="savedUserId" value="true" />ユーザーID保存</div>
				<div id="submit">
					<s:submit value="ログイン" class="submit" />
				</div>
			</s:form>
			<div id="text-link">
				<p>
					<s:form action="CreateUserAction">
						<s:submit value="新規ユーザー登録" class="submit" />
					</s:form>
				<p>
					<s:form action="ResetPasswordAction">
						<s:submit value="パスワードを再設定" class="submit" />
					</s:form>
			</div>
		</div>


	</div>


</body>
</html>