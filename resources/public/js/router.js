(function(App, Ember, DS) {
    'use strict';

    // NSPomodoroApp.Router = Ember.Router.extend({
    //     root: Ember.Route.extend({
    //         index: Ember.Route.extend({
    //             route: '/'
    //         })
    //     })
    // });

    App.Router.map(function() {
        this.resource('tasks', { path: '/tasks' });
        this.resource('task', { path: '/tasks/:id' });
        this.resource('pomodoro', { path: '/tasks/:id/pomodoro' })
        this.resource('work', {path: '/work'});
    });


})(NSPomodoroApp, Ember, DS);