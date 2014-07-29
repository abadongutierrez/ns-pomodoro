(function(App, Ember, DS) {
    'use strict';

    NSPomodoroApp.Work = DS.Model.extend({
        day: DS.attr('string'),
        pomodoros: DS.attr('object')
    });

    // Change the primaryKey attribute for Pomodoro Model
    NSPomodoroApp.WorkSerializer = DS.RESTSerializer.extend({
        primaryKey: 'day',

        extractArray: function(store, type, payload) {
            console.log("Extracting array in WorkSerializer:");
            console.log(payload);
            return this._super(store, type, payload);
        }
    });

})(NSPomodoroApp, Ember, DS);