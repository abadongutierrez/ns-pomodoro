(function(App, Ember, DS) {
    'use strict';

    NSPomodoroApp.Task = DS.Model.extend({
        user_id: DS.attr('number'),
        done_date: DS.attr('string'), // TODO change to date
        entered_date: DS.attr('string'), // TODO change to date
        is_done: DS.attr('boolean'),
        name: DS.attr('string'),
        total_pomodoros: DS.attr('number'),
        pomodoros: DS.hasMany('pomodoro', {async: true})
    });

    // Change the primaryKey attribute for Task Model
    NSPomodoroApp.TaskSerializer = DS.RESTSerializer.extend({
        primaryKey: 'task_id'
    });

    // DS.RESTSerializer.map('NSPomodoroApp.TaskWithPomodoros', {
    //     comments: { embedded: 'load' }
    // });

})(NSPomodoroApp, Ember, DS);