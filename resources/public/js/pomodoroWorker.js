var workTimerVar = null;
var restTimerVer = null;

function timerString(secondsLeft) {
    var min, sec;
    var minutes = Math.floor(secondsLeft/60);
    var seconds = secondsLeft - minutes * 60;

    // if number of minutes less than 10, add a leading "0"
    min = minutes.toString();
    if (min.length == 1){
        min = "0" + min;
    }
    // if number of seconds less than 10, add a leading "0"
    sec = seconds.toString();
    if (sec.length == 1){
        sec = "0" + sec;
    }
    return min + ":" + sec;
}

function workTimer(secondsLeft, pomodoroSpec) {
    if (parseInt(secondsLeft) <= 0) {
        clearTimeout(workTimerVar);
        self.postMessage({timerStr: "00:00", type: "working"});
        var sl = pomodoroSpec.restTimeLeft;
        restTimerVar = setInterval(function() {
            restTimer(sl--, pomodoroSpec);
        }, 1000);
    }
    else {
        self.postMessage({timerStr: timerString(secondsLeft), type: "working"});
    }
}

function stopPomodoroAjaxCall(pomodoroSpec) {
    // stop pomodoro in server
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("POST", "/tasks/" + pomodoroSpec.task.id + "/pomodoros/" + pomodoroSpec.pomodoro.id + "/end", false);
    xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
    xmlhttp.send();   
}

function restTimer(secondsLeft, pomodoroSpec) {
    if (parseInt(secondsLeft) <= 0) {
        clearTimeout(restTimerVar);
        self.postMessage({timerStr: "00:00", type: "resting"});
        stopPomodoroAjaxCall(pomodoroSpec);
    }
    else {
        self.postMessage({timerStr: timerString(secondsLeft), type: "resting"});
    }
}

self.addEventListener('message', function(e) {
    var pomodoroSpec = e.data.pomodoroSpec;

    if (parseInt(pomodoroSpec.workTimeLeft) > 0) {
        var secondsLeft = pomodoroSpec.workTimeLeft;
        workTimerVar = setInterval(function() {
            workTimer(secondsLeft--, pomodoroSpec);
        }, 1000);
    }
    else {
        self.postMessage("-1");
    }
}, false);