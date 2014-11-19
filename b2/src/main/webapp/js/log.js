var updater;
var stacktraceLightbox;
var stackTraceMap = {};

var logTemplates = {
    stackTraceLink: new Template("<a onClick='openStackTrace(#{entry.id})' class='stacktraceLink'>#{lang.stacktraceLink}</a>"),
    logEntry: new Template(
        "<div class='logEntry #{rowClass}'>" +
        "<span class='logEntryColumn date'>#{entry.date}</span>" +
        "<span class='logEntryColumn level'>#{levelStr}</span>" +
        "<span class='logEntryColumn message'>#{entry.message}</span>" +
        "<span class='logEntryColumn stacktrace'>#{stackTraceLink}</span>" +
        "</div>"),
    stackTrace: new Template("<div class='stacktraceText'>#{stacktrace}</div>"),
    taskSummary: new Template("<div class='taskSummary'>" +
        "<div class='taskSummaryRow'><span class='taskSummaryColumn label'>#{lang.status}</span><span class='taskSummaryColumn detail #{statusClass}'>#{statusStr}</span></div>" +
        "<div class='taskSummaryRow'><span class='taskSummaryColumn label'>#{lang.enteredDate}</span><span class='taskSummaryColumn detail'>#{task.enteredDate}</span></div>" +
        "<div class='taskSummaryRow'><span class='taskSummaryColumn label'>#{lang.scheduledDate}</span><span class='taskSummaryColumn detail'>#{task.scheduledDate}</span></div>" +
        "<div class='taskSummaryRow'><span class='taskSummaryColumn label'>#{lang.startedDate}</span><span class='taskSummaryColumn detail'>#{task.startedDate}</span></div>" +
        "<div class='taskSummaryRow'><span class='taskSummaryColumn label'>#{lang.endedDate}</span><span class='taskSummaryColumn detail'>#{task.endedDate}</span></div>" +
        "<div class='taskSummaryRow'><span class='taskSummaryColumn label'>#{lang.enteredNode}</span><span class='taskSummaryColumn detail'>#{task.enteredNode}</span></div>" +
        "<div class='taskSummaryRow'><span class='taskSummaryColumn label'>#{lang.processingNode}</span><span class='taskSummaryColumn detail'>#{task.processingNode}</span></div>" +
        "<div class='taskSummaryRow'><span class='taskSummaryColumn label'>#{lang.totalGroups}</span><span class='taskSummaryColumn detail'>#{task.totalGroups}</span></div>" +
        "<div class='taskSummaryRow'><span class='taskSummaryColumn label'>#{lang.processedGroups}</span><span class='taskSummaryColumn detail'>#{task.processedGroups}</span></div>" +
        "</div>")
};

function setupLightbox(lang) {
    if(typeof lightbox !== 'undefined') {
        stacktraceLightbox = new lightbox.Lightbox({
            contents: { id: "stacktrace"},
            title: lang.stacktraceLightBoxTitle,
            showCloseLink: true,
            closeOnBodyClick: true,
            dimensions: {w: 800, h: 600}
        });
    }
}

function startLogPoll(taskId, lang) {
    updater = new Ajax.PeriodicalUpdater("", "Log.action", {
        method: "GET",
        frequency: 2,
        evalJSON: 'force',
        parameters: { update: undefined, taskId: taskId },
        onSuccess: function(response) {
            var json = response.responseJSON;
            var newLogHTML = "";
            json.logEntries.each(function(entry) {
                var entryModel = {
                    entry: entry,
                    lang: lang,
                    levelStr: lang.levels[entry.level],
                    rowClass: "level-" + entry.level.toLowerCase()
                };
                if(entry.stacktrace) {
                    stackTraceMap[entry.id] = entry.stacktrace;
                    entryModel.stackTraceLink = logTemplates.stackTraceLink.evaluate(entryModel);
                }
                newLogHTML += logTemplates.logEntry.evaluate(entryModel);
            });
            $("log").insert({bottom: newLogHTML});
            var taskModel = {
                task: json.task,
                lang: lang,
                statusClass: "status-" + json.task.status.toLowerCase(),
                statusStr: lang.statuses[json.task.status]
            };
            $("task").update(logTemplates.taskSummary.evaluate(taskModel));
            if(json.logEntries.last()) {
                updater.options.parameters.lastEntryId = json.logEntries.last().id;
            }
            if(json.task.status != "PENDING" && json.task.status != "PROCESSING") {
                updater.stop();
            }
        }
    });
}

function openStackTrace(id) {
    var stacktrace = stackTraceMap[id].escapeHTML();
    if(stacktraceLightbox!=null) {
        var obj = { stacktrace: stackTraceMap[id].escapeHTML(stacktrace) };
        $("stacktrace").update(logTemplates.stackTrace.evaluate(obj));
        stacktraceLightbox.open();
    } else {
        console.log(stacktrace);
    }
}