<!DOCTYPE html>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="/bbNG" prefix="bbNG"%>
<%@taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<fmt:message var="description" key="bond.classgroups.defaultdeserialiser.description" />
<fmt:message var="noDefaultDeserialiser" key="bond.classgroups.defaultdeserialiser.nodefaultdeserialiser" />
<fmt:message var="executeButton" key="bond.classgroups.defaultdeserialiser.executebutton" />

<bbNG:genericPage ctxId="ctx" navItem="bond-ClassGroups-nav-defaultDeserialiser">

    <bbNG:cssFile href="css/default.css" />

    <c:choose>
        <c:when test="${actionBean.defaultDeserialiserConfigured}">
            <p>${description}</p>
            <bbNG:button label="${executeButton}" url="DefaultDeserialiser.action?execute" />
        </c:when>
        <c:otherwise>
            <p>${noDefaultDeserialiser}</p>
        </c:otherwise>
    </c:choose>

</bbNG:genericPage>