(function(App, Ember, DS) {
    'use strict';

    App.TaskRoute = Ember.Route.extend({
        model: function(params) {
            return this.store.find('task', params.id);
        }
    });

})(NSPomodoroApp, Ember, DS);