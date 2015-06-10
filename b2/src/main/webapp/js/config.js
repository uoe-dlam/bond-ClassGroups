var configTemplates = {
    scheduleRow: new Template("<div class='scheduleRow' id='schedule#{id}'>" +
        "<span class='scheduleColumn'>#{daySelectHtml}</span>" +
        "<span class='scheduleColumn'><input type='text' name='configuration.schedules[#{id}].hour' value='#{schedule.hour}' class='hour' /></span>" +
        "<span class='scheduleColumn'><input type='text' name='configuration.schedules[#{id}].minute' value='#{schedule.minute}' class='minute' /></span>" +
        "<span class='scheduleColumn'><a onclick='removeSchedule(\"#{id}\")' class='genericButton'>#{lang.remove}</a></span>" +
        "</div>"),
    scheduleDaySelect: new Template("<select name='configuration.schedules[#{id}].day'>" +
        "<option value='EVERY_DAY' #{EVERY_DAYSelected}>#{lang.everyday}</option>" +
        "<option value='MONDAY' #{MONDAYSelected}>#{lang.monday}</option>" +
        "<option value='TUESDAY' #{TUESDAYSelected}>#{lang.tuesday}</option>" +
        "<option value='WEDNESDAY' #{WEDNESDAYSelected}>#{lang.wednesday}</option>" +
        "<option value='THURSDAY' #{THURSDAYSelected}>#{lang.thursday}</option>" +
        "<option value='FRIDAY' #{FRIDAYSelected}>#{lang.friday}</option>" +
        "<option value='SATURDAY' #{SATURDAYSelected}>#{lang.saturday}</option>" +
        "<option value='SUNDAY' #{SUNDAYSelected}>#{lang.sunday}</option>" +
        "</select>")
};

Event.observe(document, 'dom:loaded', function() {
    var tabClass = $$(".deserialiserTab.tabSelected")
                        .first()
                        .classNames()
                        .filter(function(klass) { return (klass.indexOf("Config") > -1); })
                        .first();
    showTab(tabClass);
});

function showTab(tabClass) {
    $$(".deserialiserTabFrame").invoke('hide');
    $$(".deserialiserTabFrame." + tabClass).invoke('show');
    $$(".deserialiserTab").invoke('removeClassName', 'tabSelected');
    $$(".deserialiserTab." + tabClass).invoke('addClassName', 'tabSelected');
}

var scheduleId = 0;

function addSchedules(schedules, lang) {
    var scheduleTable = $("scheduleTable");
    schedules.each(function(schedule) {
       addSchedule(schedule, scheduleTable, lang);
    });
}

function addSchedule(schedule, table, lang) {
    if(!table) {
        table = $("scheduleTable");
    }
    var id = scheduleId++;
    selectOptions = {
        id: id,
        lang: lang
    };
    selectOptions[schedule.day + "Selected"] = "selected='selected'";
    var scheduleModel = {
        id: id,
        daySelectHtml: configTemplates.scheduleDaySelect.evaluate(selectOptions),
        schedule: schedule,
        lang: lang
    };
    var newScheduleHtml = configTemplates.scheduleRow.evaluate(scheduleModel);
    table.insert({bottom: newScheduleHtml});
}

function removeSchedule(scheduleId) {
    $("schedule" +  scheduleId).remove();
}

function newSchedule(lang) {
    addSchedule({}, null, lang);
}