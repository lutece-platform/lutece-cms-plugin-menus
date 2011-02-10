<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="menus" scope="session" class="fr.paris.lutece.plugins.menus.web.MenusJspBean" />

<% menus.init( request, menus.RIGHT_MANAGE_MENUS ); %>
<%= menus.getManageMenus ( request ) %>

<%@ include file="../../AdminFooter.jsp" %>