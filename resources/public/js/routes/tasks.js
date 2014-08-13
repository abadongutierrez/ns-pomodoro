(function(App, Ember, DS) {
    'use strict';

    App.TasksRoute = Ember.Route.extend({
        model: function() {
            return this.store.find('task');
        }
    });

})(NSPomodoroApp, Ember, DS);