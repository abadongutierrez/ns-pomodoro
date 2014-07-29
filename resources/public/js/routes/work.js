(function(App, Ember, DS) {
    'use strict';

    NSPomodoroApp.WorkRoute = Ember.Route.extend({
        model: function() {
            return this.store.find('work');
        }
    });

})(NSPomodoroApp, Ember, DS);