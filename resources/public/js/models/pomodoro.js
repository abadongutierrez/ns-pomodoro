(function(App, Ember, DS) {
    'use strict';

    App.Pomodoro = DS.Model.extend({
        started_date: DS.attr('string'), // TODO change to date
        ended_date: DS.attr('string'), // TODO change to date
        task: DS.belongsTo('task')
    });

    // Change the primaryKey attribute for Pomodoro Model
    App.PomodoroSerializer = DS.RESTSerializer.extend({
        primaryKey: 'pomodoro_id'
    });

})(NSPomodoroApp, Ember, DS);