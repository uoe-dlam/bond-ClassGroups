<!DOCTYPE html>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="/bbNG" prefix="bbNG"%>
<%@taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<fmt:message var="noRecentTask" key="bond.classgroups.recentlog.norecenttask" />

<bbNG:genericPage ctxId="ctx" navItem="bond-ClassGroups-nav-recentLog">

    <bbNG:jsFile href="js/log.js" />
    <bbNG:cssFile href="css/default.css" />

    <c:choose>
        <c:when test="${actionBean.task == null}">
            <p>${noRecentTask}</p>
        </c:when>

        <c:otherwise>
            <jsp:include page="log.jsp" />
        </c:otherwise>
    </c:choose>

</bbNG:genericPage>