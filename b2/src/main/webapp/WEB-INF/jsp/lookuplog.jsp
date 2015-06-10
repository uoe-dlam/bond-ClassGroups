<!DOCTYPE html>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="/bbNG" prefix="bbNG"%>
<%@taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>

<bbNG:genericPage ctxId="ctx"  navItem="bond-ClassGroups-nav-lookupLog">

    <bbNG:jsFile href="js/log.js" />
    <bbNG:cssFile href="css/default.css" />

    <jsp:include page="log.jsp" />

</bbNG:genericPage>