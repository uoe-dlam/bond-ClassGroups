<!DOCTYPE html>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="/bbNG" prefix="bbNG"%>
<%@taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<fmt:message var="notConfigured" key="bond.classgroups.cleanup.notconfigured" />
<fmt:message var="cleanUpButton" key="bond.classgroups.cleanup.cleanupbutton" />
<fmt:message var="configButton" key="bond.classgroups.cleanup.configbutton" />
<fmt:message var="description" key="bond.classgroups.cleanup.description">
    <fmt:param value="${actionBean.configuration.cleanUpDaysToKeep}" />
</fmt:message>

<bbNG:genericPage ctxId="ctx"  navItem="bond-ClassGroups-nav-cleanUp">

    <bbNG:cssFile href="css/default.css" />

    <c:choose>
        <c:when test="${actionBean.configuration.cleanUpDaysToKeep <= 0}">
            <p>${notConfigured}</p>
            <bbNG:button label="${configButton}" url="Config.action" />
        </c:when>

        <c:otherwise>
            <p>${description}</p>
            <bbNG:button label="${cleanUpButton}" url="CleanUp.action?execute" />
        </c:otherwise>
    </c:choose>

</bbNG:genericPage>