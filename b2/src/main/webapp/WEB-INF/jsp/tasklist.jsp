<!DOCTYPE html>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="/bbNG" prefix="bbNG"%>
<%@taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>

<bbNG:genericPage ctxId="ctx" navItem="bond-ClassGroups-nav-taskList">

    <bbNG:cssFile href="css/default.css" />

    <bbNG:pagedList collection="${actionBean.tasks}" className="au.edu.bond.classgroups.model.Task" objectVar="task"
                    recordCount="${actionBean.taskCount}" displayPagingControls="true"
                    emptyMsg="No previous tasks." initialSortCol="enteredDate" initialSortBy="DESC">
        <bbNG:listElement name="id" isRowHeader="true" label="Id" comparator="${actionBean.idComparator}">${task.id}</bbNG:listElement>
        <bbNG:listElement name="status" label="Status" comparator="${actionBean.statusComparator}">${task.status}</bbNG:listElement>
        <bbNG:listElement name="enteredDate" label="Entered" comparator="${actionBean.enteredComparator}">${task.enteredDate}</bbNG:listElement>
        <bbNG:listElement name="scheduledDate" label="Scheduled" comparator="${actionBean.scheduledComparator}">${task.scheduledDate}</bbNG:listElement>
        <bbNG:listElement name="startedDate" label="Started" comparator="${actionBean.startedComparator}">${task.startedDate}</bbNG:listElement>
        <bbNG:listElement name="endedDate" label="Ended" comparator="${actionBean.endedComparator}">${task.endedDate}</bbNG:listElement>
        <bbNG:listElement name="enteredNode" label="Entered Node" comparator="${actionBean.enteredNodeComparator}">${task.enteredNode}</bbNG:listElement>
        <bbNG:listElement name="processingNode" label="Processing Node" comparator="${actionBean.processingNodeComparator}">${task.processingNode}</bbNG:listElement>
        <bbNG:listElement name="totalGroups" label="Total Groups" comparator="${actionBean.totalGroupsComparator}">${task.totalGroups}</bbNG:listElement>
        <bbNG:listElement name="processedGroups" label="Processed Groups" comparator="${actionBean.processedGroupsComparator}">${task.processedGroups}</bbNG:listElement>
        <bbNG:listElement name="links" label="Log"><a href="Log.action?taskId=${task.id}">Log</a></bbNG:listElement>
    </bbNG:pagedList>

</bbNG:genericPage>