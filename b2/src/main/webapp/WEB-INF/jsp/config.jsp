<!DOCTYPE html>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="/bbNG" prefix="bbNG"%>
<%@taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<fmt:message var="groupAvailability" key="bond.classgroups.config.groupavailability" />
<fmt:message var="groupAvailabilityInstructions" key="bond.classgroups.config.groupavailability.instructions" />
<fmt:message var="availabilityMode" key="bond.classgroups.config.groupavailability.availabilitymode" />
<fmt:message var="availabilityModeCreate" key="bond.classgroups.config.groupavailability.availabilitymode.create" />
<fmt:message var="availabilityModeCreateDesc" key="bond.classgroups.config.groupavailability.availabilitymode.createdescription" />
<fmt:message var="availabilityModeUpdate" key="bond.classgroups.config.groupavailability.availabilitymode.update" />
<fmt:message var="availabilityModeUpdateDesc" key="bond.classgroups.config.groupavailability.availabilitymode.updatedescription" />
<fmt:message var="defaultAvailbility" key="bond.classgroups.config.groupavailability.defaultavailability" />
<fmt:message var="defaultAvailbilityAvailable" key="bond.classgroups.config.groupavailability.defaultavailability.available" />
<fmt:message var="defaultAvailbilityAvailableDesc" key="bond.classgroups.config.groupavailability.defaultavailability.availabledescription" />
<fmt:message var="defaultAvailbilityUnavailable" key="bond.classgroups.config.groupavailability.defaultavailability.unavailable" />
<fmt:message var="defaultAvailbilityUnavailableDesc" key="bond.classgroups.config.groupavailability.defaultavailability.unavailabledescription" />

<fmt:message var="groupTools" key="bond.classgroups.config.grouptools" />
<fmt:message var="groupToolsInstructions" key="bond.classgroups.config.grouptools.instructions" />
<fmt:message var="groupToolsMode" key="bond.classgroups.config.grouptools.grouptoolsmode" />
<fmt:message var="groupToolsModeCreate" key="bond.classgroups.config.grouptools.grouptoolsmode.create" />
<fmt:message var="groupToolsModeCreateDesc" key="bond.classgroups.config.grouptools.grouptoolsmode.createdescription" />
<fmt:message var="groupToolsModeReAdd" key="bond.classgroups.config.grouptools.grouptoolsmode.readd" />
<fmt:message var="groupToolsModeReAddDesc" key="bond.classgroups.config.grouptools.grouptoolsmode.readddescription" />
<fmt:message var="groupToolsModeSync" key="bond.classgroups.config.grouptools.grouptoolsmode.sync" />
<fmt:message var="groupToolsModeSyncDesc" key="bond.classgroups.config.grouptools.grouptoolsmode.syncdescription" />
<fmt:message var="groupToolsDefaultTools" key="bond.classgroups.config.grouptools.defaultTools" />

<fmt:message var="groupLeader" key="bond.classgroups.config.groupleader" />
<fmt:message var="groupLeaderInsructions" key="bond.classgroups.config.groupleader.instructions" />
<fmt:message var="groupLeaderChangeMode" key="bond.classgroups.config.groupleader.leaderchangemode" />
<fmt:message var="groupLeaderChangeModeDesc" key="bond.classgroups.config.groupleader.leaderchangemodedescription" />
<fmt:message var="groupLeaderChangeModeOverride" key="bond.classgroups.config.groupleader.leaderchangemode.override" />
<fmt:message var="groupLeaderChangeModeOverrideDesc" key="bond.classgroups.config.groupleader.leaderchangemode.overridedescription" />
<fmt:message var="groupLeaderChangeModeFeed" key="bond.classgroups.config.groupleader.leaderchangemode.feed" />
<fmt:message var="groupLeaderChangeModeFeedDesc" key="bond.classgroups.config.groupleader.leaderchangemode.feeddescription" />

<fmt:message var="deserialiser" key="bond.classgroups.config.deserialiser" />
<fmt:message var="deserialiserInstructions" key="bond.classgroups.config.deserialiser.instructions" />
<fmt:message var="pullCsvFile" key="bond.classgroups.config.deserialiser.pullcsvfile" />
<fmt:message var="pullCsvFileInstructions" key="bond.classgroups.config.deserialiser.pullcsvfile.instructions" />
<fmt:message var="pullCsvFileDefault" key="bond.classgroups.config.deserialiser.default">
    <fmt:param value="${pullCsvFile}" />
</fmt:message>
<fmt:message var="pullCsvFileGroupsFilePath" key="bond.classgroups.config.deserialiser.pullcsvfile.groupsfilepath" />
<fmt:message var="pullCsvFileMembersFilePath" key="bond.classgroups.config.deserialiser.pullcsvfile.membersfilepath" />
<fmt:message var="pullCsvUrl" key="bond.classgroups.config.deserialiser.pullcsvurl" />
<fmt:message var="pullCsvUrlInstructions" key="bond.classgroups.config.deserialiser.pullcsvurl.instructions" />
<fmt:message var="pullCsvUrlDefault" key="bond.classgroups.config.deserialiser.default">
    <fmt:param value="${pullCsvUrl}" />
</fmt:message>
<fmt:message var="pullCsvFileGroupsFileUrl" key="bond.classgroups.config.deserialiser.pullcsvurl.groupsfileurl" />
<fmt:message var="pullCsvFileMembersFileUrl" key="bond.classgroups.config.deserialiser.pullcsvurl.membersfileurl" />
<fmt:message var="httpPushCsv" key="bond.classgroups.config.deserialiser.httppushcsv" />
<fmt:message var="httpPushCsvInstructions" key="bond.classgroups.config.deserialiser.httppushcsv.instructions" />
<fmt:message var="httpPushCsvPasskey" key="bond.classgroups.config.deserialiser.httppushcsv.passkey" />

<fmt:message var="autoCleanUp" key="bond.classgroups.config.autocleanup" />
<fmt:message var="autoCleanUpInstructions" key="bond.classgroups.config.autocleanup.instructions" />
<fmt:message var="autoCleanUpCleanUp" key="bond.classgroups.config.autocleanup.cleanup" />
<fmt:message var="autoCleanUpCleanUpEnabled" key="bond.classgroups.config.autocleanup.cleanup.enabled" />
<fmt:message var="autoCleanUpCleanUpEnabledDescription" key="bond.classgroups.config.autocleanup.cleanup.enableddescription" />
<fmt:message var="autoCleanUpCleanUpDisabled" key="bond.classgroups.config.autocleanup.cleanup.disabled" />
<fmt:message var="autoCleanUpCleanUpDisabledDescrption" key="bond.classgroups.config.autocleanup.cleanup.disableddescription" />

<fmt:message var="autoCleanUpDaysToKeep" key="bond.classgroups.config.autocleanup.daystokeep" />
<fmt:message var="autoCleanUpDaysToKeepDays" key="bond.classgroups.config.autocleanup.daystokeep.days" />

<fmt:message var="schdeduling" key="bond.classgroups.config.scheduling" />
<fmt:message var="schdedulingInstructions" key="bond.classgroups.config.scheduling.instructions" />
<fmt:message var="schdedulingEnableSchedules" key="bond.classgroups.config.scheduling.enableschedules" />
<fmt:message var="schdedulingEnableSchedulesEnabled" key="bond.classgroups.config.scheduling.enableschedules.enabled" />
<fmt:message var="schdedulingEnableSchedulesEnabledDesc" key="bond.classgroups.config.scheduling.enableschedules.enableddescription" />
<fmt:message var="schdedulingEnableSchedulesDisabled" key="bond.classgroups.config.scheduling.enableschedules.disabled" />
<fmt:message var="schdedulingEnableSchedulesDisabledDesc" key="bond.classgroups.config.scheduling.enableschedules.disableddescription" />
<fmt:message var="schedulingSchedules" key="bond.classgroups.config.scheduling.schedules" />
<fmt:message var="schedulingSchedulesAdd" key="bond.classgroups.config.scheduling.schedules.add" />
<fmt:message var="schedulingSchedulesRemove" key="bond.classgroups.config.scheduling.schedules.remove" />
<fmt:message var="schedulingSchedulesDay" key="bond.classgroups.config.scheduling.schedules.day" />
<fmt:message var="schedulingSchedulesHour" key="bond.classgroups.config.scheduling.schedules.hour" />
<fmt:message var="schedulingSchedulesMinute" key="bond.classgroups.config.scheduling.schedules.minute" />
<fmt:message var="schedulingSchedulesEveryDay" key="bond.classgroups.config.scheduling.schedules.everyday" />
<fmt:message var="schedulingSchedulesMonday" key="bond.classgroups.config.scheduling.schedules.monday" />
<fmt:message var="schedulingSchedulesTuesday" key="bond.classgroups.config.scheduling.schedules.tuesday" />
<fmt:message var="schedulingSchedulesWednesday" key="bond.classgroups.config.scheduling.schedules.wednesday" />
<fmt:message var="schedulingSchedulesThursday" key="bond.classgroups.config.scheduling.schedules.thursday" />
<fmt:message var="schedulingSchedulesFriday" key="bond.classgroups.config.scheduling.schedules.friday" />
<fmt:message var="schedulingSchedulesSaturday" key="bond.classgroups.config.scheduling.schedules.saturday" />
<fmt:message var="schedulingSchedulesSunday" key="bond.classgroups.config.scheduling.schedules.sunday" />

<fmt:message var="save" key="bond.classgroups.config.save" />

<bbNG:genericPage ctxId="ctx" navItem="bond-ClassGroups-nav-configure">

    <bbNG:jsFile href="js/config.js" />
    <bbNG:cssFile href="css/default.css" />

    <bbNG:form method="post" name="configureForm" action="Config.action?save">
        <bbNG:dataCollection>

            <bbNG:step title="${groupAvailability}" instructions="${groupAvailabilityInstructions}">
                <bbNG:dataElement label="${defaultAvailbility}" isRequired="true">
                    <ul>
                        <li><label><input type="radio" name="configuration.defaultAvailability" value="AVAILABLE" ${actionBean.configuration.defaultAvailability.toString().equals("AVAILABLE")?"checked=checked":""} /> <strong>${defaultAvailbilityAvailable}</strong>: ${defaultAvailbilityAvailableDesc}</label></li>
                        <li><label><input type="radio" name="configuration.defaultAvailability" value="UNAVAILABLE" ${actionBean.configuration.defaultAvailability.toString().equals("UNAVAILABLE")?"checked=checked":""} /> <strong>${defaultAvailbilityUnavailable}</strong>: ${defaultAvailbilityUnavailableDesc}</label></li>
                    </ul>
                </bbNG:dataElement>
                <bbNG:dataElement label="${availabilityMode}" isRequired="true">
                    <ul>
                        <li><label><input type="radio" name="configuration.availabilityMode" value="CREATE" ${actionBean.configuration.availabilityMode.toString().equals("CREATE")?"checked=checked":""} /> <strong>${availabilityModeCreate}</strong>: ${availabilityModeCreateDesc}</label></li>
                        <li><label><input type="radio" name="configuration.availabilityMode" value="UPDATE" ${actionBean.configuration.availabilityMode.toString().equals("UPDATE")?"checked=checked":""} /> <strong>${availabilityModeUpdate}</strong>: ${availabilityModeUpdateDesc}</label></li>
                    </ul>
                </bbNG:dataElement>
            </bbNG:step>

            <bbNG:step title="${groupTools}" instructions="${groupToolsInstructions}">
                <bbNG:dataElement label="${groupToolsMode}" isRequired="true">
                    <ul>
                        <li><label><input type="radio" name="configuration.toolsMode" value="CREATE" ${actionBean.configuration.toolsMode.toString().equals("CREATE")?"checked=checked":""} /> <strong>${groupToolsModeCreate}</strong>: ${groupToolsModeCreateDesc}</label></li>
                        <li><label><input type="radio" name="configuration.toolsMode" value="READD" ${actionBean.configuration.toolsMode.toString().equals("READD")?"checked=checked":""} /> <strong>${groupToolsModeReAdd}</strong>: ${groupToolsModeReAddDesc}</label></li>
                        <li><label><input type="radio" name="configuration.toolsMode" value="SYNC" ${actionBean.configuration.toolsMode.toString().equals("SYNC")?"checked=checked":""} /> <strong>${groupToolsModeSync}</strong>: ${groupToolsModeSyncDesc}</label></li>
                    </ul>
                </bbNG:dataElement>
                <bbNG:dataElement label="${groupToolsDefaultTools}" isRequired="true">
                    <ul>
                        <c:set var="allGroupTools" value="${actionBean.getAllGroupTools()}" />
                        <c:forEach var="tool" items="${allGroupTools.keySet()}">
                            <li><label><input type="checkbox" name="configuration.defaultTools" value="${tool}" ${actionBean.configuration.defaultTools.contains(tool)?"checked=checked":""} /> ${allGroupTools[tool]}</label></li>
                        </c:forEach>
                    </ul>
                </bbNG:dataElement>
            </bbNG:step>

            <bbNG:step title="${groupLeader}" instructions="${groupLeaderInsructions}">
                <bbNG:dataElement label="${groupLeaderChangeMode}" isRequired="true">
                    <p>${groupLeaderChangeModeDesc}</p>
                    <ul>
                        <li><label><input type="radio" name="configuration.leaderChangedMode" value="OVERRIDE" ${actionBean.configuration.leaderChangedMode.toString().equals("OVERRIDE")?"checked=checked":""} /> <strong>${groupLeaderChangeModeOverride}</strong>: ${groupLeaderChangeModeOverrideDesc}</label></li>
                        <li><label><input type="radio" name="configuration.leaderChangedMode" value="FEED" ${actionBean.configuration.leaderChangedMode.toString().equals("FEED")?"checked=checked":""} /> <strong>${groupLeaderChangeModeFeed}</strong>: ${groupLeaderChangeModeFeedDesc}</label></li>
                    </ul>
                </bbNG:dataElement>
            </bbNG:step>

            <bbNG:step title="${deserialiser}" instructions="${deserialiserInstructions}">
                <div class="deserialiserTabList">
                    <div class="deserialiserTab pullFileCsvConfig tabSelected"><a href="#" onclick="showTab('pullFileCsvConfig');">${pullCsvFile}</a></div>
                    <div class="deserialiserTab pullUrlCsvConfig"><a href="#" onclick="showTab('pullUrlCsvConfig');">${pullCsvUrl}</a></div>
                    <div class="deserialiserTab pushCsvConfig"><a href="#" onclick="showTab('pushCsvConfig');">${httpPushCsv}</a></div>
                </div>

                <div class="deserialiserTabFrame pullFileCsvConfig">
                    <div class="stepHelp">${pullCsvFileInstructions}</div>
                    <bbNG:dataElement>
                        <label><input type="radio" name="configuration.defaultFeedDeserialiserBean" value="pullFileCsvFeedDeserialiser" ${actionBean.configuration.defaultFeedDeserialiserBean.equals("pullFileCsvFeedDeserialiser")?"checked=checked":""} /> ${pullCsvFileDefault}</label>
                    </bbNG:dataElement>
                    <bbNG:dataElement label="${pullCsvFileGroupsFilePath}">
                        <input type="text" name="configuration.pullFileCsvFeedDeserialiser.groupsFilePath" value="${actionBean.configuration.pullFileCsvFeedDeserialiser.groupsFilePath}" />
                    </bbNG:dataElement>
                    <bbNG:dataElement label="${pullCsvFileMembersFilePath}">
                        <input type="text" name="configuration.pullFileCsvFeedDeserialiser.membersFilePath" value="${actionBean.configuration.pullFileCsvFeedDeserialiser.membersFilePath}" />
                    </bbNG:dataElement>
                </div>

                <div class="deserialiserTabFrame pullUrlCsvConfig">
                    <div class="stepHelp">${pullCsvUrlInstructions}</div>
                    <bbNG:dataElement>
                    <label><input type="radio" name="configuration.defaultFeedDeserialiserBean" value="pullUrlCsvFeedDeserialiser" ${actionBean.configuration.defaultFeedDeserialiserBean.equals("pullUrlCsvFeedDeserialiser")?"checked=checked":""} /> ${pullCsvUrlDefault}</label>
                    </bbNG:dataElement>
                    <bbNG:dataElement label="${pullCsvFileGroupsFileUrl}">
                        <input type="text" name="configuration.pullUrlCsvFeedDeserialiser.groupsUrl" value="${actionBean.configuration.pullUrlCsvFeedDeserialiser.groupsUrl}" />
                    </bbNG:dataElement>
                    <bbNG:dataElement label="${pullCsvFileMembersFileUrl}">
                        <input type="text" name="configuration.pullUrlCsvFeedDeserialiser.membersUrl" value="${actionBean.configuration.pullUrlCsvFeedDeserialiser.membersUrl}" />
                    </bbNG:dataElement>
                </div>

                <div class="deserialiserTabFrame pushCsvConfig">
                    <div class="stepHelp">${httpPushCsvInstructions}</div>
                    <bbNG:dataElement label="${httpPushCsvPasskey}">
                        <input type="text" name="configuration.pushCsvFeedDeserialiser.passkey" value="${actionBean.configuration.pushCsvFeedDeserialiser.passkey}" />
                    </bbNG:dataElement>
                </div>
            </bbNG:step>

            <bbNG:step title="${autoCleanUp}" instructions="${autoCleanUpInstructions}">
                <bbNG:dataElement label="${autoCleanUpCleanUp}" isRequired="true">
                    <ul>
                        <li><label><input type="radio" name="configuration.autoCleanUpOldTasks" value="false" ${!actionBean.configuration.autoCleanUpOldTasks?"checked=checked":""} /> <strong>${autoCleanUpCleanUpEnabled}</strong>: ${autoCleanUpCleanUpDisabledDescrption}</label></li>
                        <li><label><input type="radio" name="configuration.autoCleanUpOldTasks" value="true" ${actionBean.configuration.autoCleanUpOldTasks?"checked=checked":""} /> <strong>${autoCleanUpCleanUpDisabled}</strong>: ${autoCleanUpCleanUpEnabledDescription}</label></li>
                    </ul>
                </bbNG:dataElement>
                <bbNG:dataElement label="${autoCleanUpDaysToKeep}" isRequired="true">
                    <input type="text" name="configuration.cleanUpDaysToKeep" value="${actionBean.configuration.cleanUpDaysToKeep}" class="cleanUpDaysToKeep" /> ${autoCleanUpDaysToKeepDays}
                </bbNG:dataElement>
            </bbNG:step>

            <bbNG:step title="${schdeduling}" instructions="${schdedulingInstructions}">
                <bbNG:dataElement label="${schdedulingEnableSchedules}" isRequired="true">
                    <ul>
                        <li><label><input type="radio" name="configuration.schedulesEnabled" value="false" ${!actionBean.configuration.schedulesEnabled?"checked=checked":""} /> <strong>${schdedulingEnableSchedulesDisabled}</strong>: ${schdedulingEnableSchedulesDisabledDesc}</label></li>
                        <li><label><input type="radio" name="configuration.schedulesEnabled" value="true" ${actionBean.configuration.schedulesEnabled?"checked=checked":""} /> <strong>${schdedulingEnableSchedulesEnabled}</strong>: ${schdedulingEnableSchedulesEnabledDesc}</label></li>
                    </ul>
                </bbNG:dataElement>
                <bbNG:dataElement label="${schedulingSchedules}" isRequired="true">
                    <div><a class="genericButton" id="addScheduleButton">${schedulingSchedulesAdd}</a></div>
                    <div id="scheduleTable">
                        <div class="scheduleRow">
                            <div class="scheduleColumn header">${schedulingSchedulesDay}</div>
                            <div class="scheduleColumn header">${schedulingSchedulesHour}</div>
                            <div class="scheduleColumn header">${schedulingSchedulesMinute}</div>
                            <div class="scheduleColumn header">&nbsp;</div>
                        </div>
                    </div>
                </bbNG:dataElement>
            </bbNG:step>

            <bbNG:stepSubmit showCancelButton="true">
                <bbNG:stepSubmitButton label="${save}" id="submitSave"/>
            </bbNG:stepSubmit>
        </bbNG:dataCollection>
    </bbNG:form>

    <bbNG:jsBlock>
        <script type="text/javascript">
            Event.observe(document, 'dom:loaded', function() {
                var lang = {
                    remove: "${schedulingSchedulesRemove}",
                    everyday: "${schedulingSchedulesEveryDay}",
                    monday: "${schedulingSchedulesMonday}",
                    tuesday: "${schedulingSchedulesTuesday}",
                    wednesday: "${schedulingSchedulesWednesday}",
                    thursday: "${schedulingSchedulesThursday}",
                    friday: "${schedulingSchedulesFriday}",
                    saturday: "${schedulingSchedulesSaturday}",
                    sunday: "${schedulingSchedulesSunday}"
                }

                var schedules = ${actionBean.schedulesJson};
                if(schedules) {
                    addSchedules(schedules, lang);
                }

                ${"addScheduleButton"}.observe('click', function() {
                    newSchedule(lang);
                })
            });
        </script>
    </bbNG:jsBlock>

</bbNG:genericPage>