(function(App, Ember, DS) {
    'use strict';

    NSPomodoroApp.TasksRoute = Ember.Route.extend({
        model: function() {
            return this.store.find('task');
        }
    });

})(NSPomodoroApp, Ember, DS);