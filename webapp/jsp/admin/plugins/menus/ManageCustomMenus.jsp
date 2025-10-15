<jsp:useBean id="manageCustomMenus" scope="session" class="fr.paris.lutece.plugins.menus.web.CustomMenusJspBean" />
<% String strContent = manageCustomMenus.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>