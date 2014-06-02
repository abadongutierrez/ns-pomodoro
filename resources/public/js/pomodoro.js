var App = {};

(function(App) {

    var Pomodoro = function(spec) {
        var _pomodoroId = null,
            _task = spec.task,
            _pomodoroWorkEndSound = document.createElement('audio'),
            _pomodoroFinishedSound = document.createElement('audio');

        _pomodoroWorkEndSound.setAttribute('src', '/audio/Buzz-SoundBible.com-1790490578.mp3');
        _pomodoroFinishedSound.setAttribute('src', '/audio/Ta_Da-SoundBible.com-1884170640.mp3');

        _pomodoroWorkEndSound.addEventListener("ended", function() {
            $('#countdown').countdown({until: +5, format: 'MS', onExpiry: _onPomodoroRested});
            $('#countdown').removeClass('pomodoro-working is-countdown').addClass('pomodoro-resting');
        });

        function onPomodoroWorked() {
            $('#countdown').countdown('destroy');
            _pomodoroWorkEndSound.play();         
        }

        function onPomodoroRested() {
            $('#countdown').countdown('destroy');
            $.post("/tasks/" + _task.taskId + "/pomodoros/" + pomodoroId + "/end");
            _pomodoroFinishedSound.play();
            $('#current-pomodoro-button').hide();
            $('#next-pomodoro-button').show();
        }

        $('#start').click(function() {
            $.post("/tasks/" + _task.taskId + "/pomodoros/start", {}, function(data, textStatus, jqXHR) {
                $('#countdown').countdown({until: +10, format: 'MS', onExpiry: _onPomodoroWorked});
                $('#countdown').removeClass('is-countdown').addClass('pomodoro-working');
            }, "html")
            .fail(function() {
                alert('error');
            });
        });
    };

    App.createPomodoro = function(spec) {
        return new Pomodoro(spec);
    };

})(App);