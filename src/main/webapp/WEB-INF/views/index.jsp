<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE HTML>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<link rel="stylesheet" href='<c:url value="/resources/css/bootstrap.css" />'>
	<link rel="stylesheet" href='<c:url value="/resources/css/font-awesome.css" />'>
	<script src='<c:url value="/resources/js/jquery.min.js" />'></script>
	<script src='<c:url value="/resources/js/bootstrap.min.js" />'></script>

	<title>Телефонная книга</title>


</head>
<body>
	<div class="container col-md-4">

		<legend>
			<a href="<spring:url value='/'/>"><i class="fa fa-phone" aria-hidden="true" style="margin-right: 5px"></i>Телефонная книга</a>
		</legend>

		<%-- Форма поиска контакта --%>
		<spring:url value="/" var="formUrl" />
		<form action="${formUrl}" method="post" class="form-horizontal">
				<div class="input-group">

					<input name="search" type="text" class="form-control" placeholder="Найти по имени" <c:if test="${search != null}">value="${search}"</c:if> max="50">
					<span class="input-group-btn">
      					<button class="btn btn-default" type="submit" title="Найти"><i class="fa fa-search" aria-hidden="true"></i></button>
    				</span>
				</div>
		</form>

		<table class="table table-striped table-hover">
			<thead>
				<tr>
					<th style="width: 60px"></th>
					<th>Имя</th>
					<th style="width: 150px">Телефон</th>
				</tr>
			</thead>
			<tbody>

				<%-- Форма добавления контакта --%>
				<spring:url value="/add" var="formUrl" />
				<form:form action="${formUrl}" method="POST" modelAttribute="newContact" cssClass="form-horizontal">
					<c:if test="${search != null}">
						<input type="hidden" name="search" value="${search}"/>
					</c:if>
					<tr class="valign-top">
						<%-- Кнопка добавления позиции --%>
						<td>
							<div class="form-group no-vmargin">
								<button type="submit" class="btn btn-primary btn-block" id="addContactButton" title="Добавить контакт">+</button>
							</div>
						</td>
						<td>
							<c:set var="field" value="name"/>
							<spring:bind path="${field}">
								<div class="form-group no-vmargin <c:if test='${status.errors.hasFieldErrors(field)}'>has-error</c:if>">
									<form:input path="${field}" id="${field}Add" cssClass="form-control red-tooltip" minlength="2" maxlength="50"
												data-toggle="tooltip" title='${status.errorMessage}' data-placement="top"/>
								</div>
							</spring:bind>
						</td>
						<td>
							<c:set var="field" value="phone"/>
							<spring:bind path="${field}">
								<div class="form-group no-vmargin <c:if test='${status.errors.hasFieldErrors(field)}'>has-error</c:if>">
									<form:input path="${field}" id="${field}Add" cssClass="form-control red-tooltip" minlength="1" maxlength="11"
												data-toggle="tooltip" title='${status.errorMessage}' data-placement="top" onkeydown="return isNumberKey(event)"/>
								</div>
							</spring:bind>
						</td>
					</tr>
				</form:form>

				<%-- Отображение контактов --%>
				<c:forEach var="contactFromList" items="${contacts}" varStatus="counter">

					<%-- Форма редактирования контакта --%>
					<c:if test="${action == 'edit' && contactToEdit.id == contactFromList.id}">
						<spring:url value="/update?id=${contactFromList.id}" var="formUrl" />
						<form:form action="${formUrl}" id="editContactForm" method="POST" modelAttribute="contactToEdit" cssClass="form-horizontal">
							<c:if test="${search != null}">
								<input type="hidden" name="search" value="${search}"/>
							</c:if>
							<tr>
								<%-- Кнопки Сохранить/Отменить --%>
								<td class="text-nowrap">
									    <button type="submit" title="Сохранить" id="saveContact" class="btn-link"><i class="fa fa-check" aria-hidden="true" style="color: green"></i></button>&nbsp;|
									    <spring:url value="/" var="deleteUrl">
									    	<c:if test="${search != null}">
									    		<spring:param name="search" value="${search}"/>
									    	</c:if>
									    </spring:url>
									    <a title="Отмена" style="color:red" href='' class="inline-block"><i class="fa fa-times fa-lg" aria-hidden="true"></i></a>
								</td>

								<td>
									<c:set var="field" value="name"/>
									<spring:bind path="${field}">
										<div class="form-group no-vmargin <c:if test='${status.errors.hasFieldErrors(field)}'>has-error</c:if>">
											<form:input path="${field}" id="${field}" cssClass="form-control red-tooltip" minlength="2" maxlength="50"
														data-toggle="tooltip" title='${status.errorMessage}' data-placement="top"/>
										</div>
									</spring:bind>
								</td>

								<td>
									<c:set var="field" value="phone"/>
									<spring:bind path="${field}">
										<div class="form-group no-vmargin <c:if test='${status.errors.hasFieldErrors(field)}'>has-error</c:if>">
											<form:input path="${field}" id="${field}" cssClass="form-control red-tooltip" minlength="1" maxlength="11" onkeydown="return isNumberKey(event)"
														data-toggle="tooltip" title='${status.errorMessage}' data-placement="top"/>
										</div>
									</spring:bind>
								</td>

							</tr>
						</form:form>
					</c:if>

					<%-- Строка отображения контакта --%>
					<c:if test="${contactToEdit.id != contactFromList.id}">
						<tr class="contactRow">
							<%-- Конпки редактировать/удалить контакт --%>
							<td class="text-nowrap">
								<spring:url value="/edit?id=${contactFromList.id}" var="editUrl">
									<c:if test="${search != null}">
										<spring:param name="search" value="${search}"/>
									</c:if>
								</spring:url>
								<spring:url value="/delete?id=${contactFromList.id}" var="deleteUrl">
									<c:if test="${search != null}">
										<spring:param name="search" value="${search}"/>
									</c:if>
								</spring:url>
								<div class="editDeleteButtons">
									<a title="Редактировать" href='${editUrl}' class="inline-block"><i class="fa fa-pencil-square fa-lg" aria-hidden="true"></i></a>&nbsp;|
									<a title="Удалить" href='${deleteUrl}' class="inline-block" style="color:red"><i class="fa fa-trash fa-lg" aria-hidden="true"></i></a>
								</div>
							</td>
							<td>
								${contactFromList.name}
							</td>
							<td>
								${contactFromList.phoneFormatted}
							</td>
						</tr>
					</c:if>
				</c:forEach>
			</tbody>
		</table>

		<c:if test="${contacts.size() == 0}">
            <p class="text-muted" align="center" style="margin-top: 10px">
			    <c:choose>
                    <c:when test="${search != null && search != ''}">По запросу "${search}" ничего не нашлось :(</c:when>
			    	<c:otherwise>В телефонной книге пока нет ни одного контакта</c:otherwise>
			    </c:choose>
            </p>
		</c:if>

	</div>

	<%-- Отображение tooltip'ов с валидационными сообщениями --%>
	<script type="text/javascript">
        $('.red-tooltip').tooltip({trigger : 'hover'})
            .tooltip({container: 'body'})
            .tooltip('show');
        $(window).on('resize', function () {
            $('.red-tooltip').tooltip('show')
        })
	</script>

	<%-- Управление формой добавления контакта --%>
	<script type="text/javascript">
        $(document).ready(function () {
            var action = "${action}";
            var addButton = $('#addContactButton');
            var nameField = $('#nameAdd');
            var phoneField = $('#phoneAdd');
            if (action == 'edit') {
                addButton.prop('disabled', true);
                nameField.prop('disabled', true);
                phoneField.prop('disabled', true);
            }
		})
	</script>

    <%-- Валидация поля телефона - только числа --%>
    <script type="text/javascript">
        function isNumberKey(evt){
            var charCode = (evt.which) ? evt.which : event.keyCode
            if (charCode > 31 && (charCode < 48 || charCode > 57) && (charCode < 96 || charCode > 105))
                return false;
            return true;
        }
    </script>

	<%-- Управление видимостью кнопок Редактировать/Удалить --%>
	<c:if test="${action != 'edit'}">
    	<script type="text/javascript">
    	    $(document).ready(function() {
                $(document).find('.editDeleteButtons:first').show();
    	        $('.contactRow').hover(function() {
    	            $(document).find('.editDeleteButtons').hide();
    	            $(this).find('.editDeleteButtons').show();
    	        });
    	    });
    	</script>
	</c:if>

</body>
</html> 