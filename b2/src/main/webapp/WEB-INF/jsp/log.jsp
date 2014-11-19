<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="bbNG" uri="/bbNG" %>

<fmt:message var="summary" key="bond.classgroups.log.summary" />
<fmt:message var="log" key="bond.classgroups.log.log" />

<fmt:message var="stacktraceLightBoxTitle" key="bond.classgroups.log.stacktracelightboxtitle" />
<fmt:message var="stacktraceLink" key="bond.classgroups.log.stacktracelink" />
<fmt:message var="status" key="bond.classgroups.log.status" />
<fmt:message var="enteredDate" key="bond.classgroups.log.enteredate" />
<fmt:message var="scheduledDate" key="bond.classgroups.log.scheduledDate" />
<fmt:message var="startedDate" key="bond.classgroups.log.starteddate" />
<fmt:message var="endedDate" key="bond.classgroups.log.endeddate" />
<fmt:message var="enteredNode" key="bond.classgroups.log.enteredNode" />
<fmt:message var="processingNode" key="bond.classgroups.log.processingNode" />
<fmt:message var="totalGroups" key="bond.classgroups.log.totalgroups" />
<fmt:message var="processedGroups" key="bond.classgroups.log.processedgroups" />

<fmt:message var="levelInfo" key="bond.classgroups.level.INFO" />
<fmt:message var="levelWarning" key="bond.classgroups.level.WARNING" />
<fmt:message var="levelError" key="bond.classgroups.level.ERROR" />

<fmt:message var="statusPending" key="bond.classgroups.status.PENDING" />
<fmt:message var="statusProcessing" key="bond.classgroups.status.PROCESSING" />
<fmt:message var="statusSkipped" key="bond.classgroups.status.SKIPPED" />
<fmt:message var="statusComplete" key="bond.classgroups.status.COMPLETE" />
<fmt:message var="statusFailed" key="bond.classgroups.status.FAILED" />
<fmt:message var="statusUnknown" key="bond.classgroups.status.UNKNOWN" />
<fmt:message var="statusScheduled" key="bond.classgroups.status.SCHEDULED" />
<fmt:message var="statusCancelled" key="bond.classgroups.status.CANCELLED" />

<h1>${summary}</h1>
<div id="task"></div>

<h1>${log}</h1>
<div id="log"></div>
<div id="stacktrace" style="display: none"></div>

<bbNG:jsBlock>
    <script type="text/javascript">
        Event.observe(document, 'dom:loaded', function() {
            var lang = {
                stacktraceLightBoxTitle: "${stacktraceLightBoxTitle}",
                stacktraceLink: "${stacktraceLink}",
                status: "${status}",
                enteredDate: "${enteredDate}",
                scheduledDate: "${scheduledDate}",
                startedDate: "${startedDate}",
                endedDate: "${endedDate}",
                enteredNode: "${enteredNode}",
                processingNode: "${processingNode}",
                totalGroups: "${totalGroups}",
                processedGroups: "${processedGroups}",
                levels: {
                    'INFO': "${levelInfo}",
                    'WARNING': "${levelWarning}",
                    'ERROR': "${levelError}"
                },
                statuses: {
                    'PENDING': "${statusPending}",
                    'PROCESSING': "${statusProcessing}",
                    'SKIPPED': "${statusSkipped}",
                    'COMPLETE': "${statusComplete}",
                    'FAILED': "${statusFailed}",
                    'UNKNOWN': "${statusUnknown}",
                    'SCHEDULED': "${statusScheduled}",
                    'CANCELLED': "${statusCancelled}"
                }
            };
            setupLightbox(lang);
            startLogPoll(${actionBean.task.id}, lang);
        });
    </script>
</bbNG:jsBlock>