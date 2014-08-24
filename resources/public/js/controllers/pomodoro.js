(function(App, Ember, DS) {
    'use strict';

    App.PomodoroController = Ember.ObjectController.extend({
        // Pomodoro Worker
        worker: new Worker("/js/pomodoroWorker.js"),
        showStartPomodoroButton: true, 
        timeText: '00:00',
        isPomodoroWorkingState: false,

        initPomodoroWorker: function() {
            var self = this;
            self.get('worker').addEventListener('message', function(event) {
                if (parseInt(event.data) == -1) {
                    alert('Pomodoro already finishied, please start a new one.');
                    self.set('showStartPomodoroButton', true);
                }
                else {
                    self.set('timeText', event.data.timerStr);
                    if (event.data.type === "working") {
                        self.set('isPomodoroWorkingState', true);
                    }
                    else if (event.data.type === "resting") {
                        self.set('isPomodoroWorkingState', false);
                    }

                    if (event.data.timerStr === "00:00") {
                        if (event.data.type === "working") {
                            console.log("Pomodoro Finished!, take a rest.");
                            alert('Pomodoro Finished!, take a rest.');
                        }
                        else if (event.data.type === "resting") {
                            console.log("Enough resting, go back to work.");
                            alert('Enough resting, go back to work.');
                            self.setProperties({
                                showStartPomodoroButton: true
                            });
                        }
                    }
                }
            });
        }.on('init'),

        actions: {
            startPomodoro: function() {
                var currPomodorSpec = null,
                    postUrl = "/api/v1/tasks/" + this.get('id') + "/pomodoros/start",
                    self = this;
                $.post(postUrl, {}, function(data) {
                    currPomodorSpec = {
                        task: {
                            id: data.data.pomodoro.task_id
                        },
                        pomodoro: {
                            id: data.data.pomodoro.pomodoro_id
                        },
                        workTimeLeft: data.data.work_time_left,
                        restTimeLeft: data.data.rest_time_left
                    };

                    self.set('showStartPomodoroButton', false);
                    self.set('isPomodoroWorkingState', true);
                    // send initial message to worker and start
                    self.get('worker').postMessage({pomodoroSpec: currPomodorSpec});
                });
            }
        }
    });

})(NSPomodoroApp, Ember, DS);