(function(App, Ember, DS) {
    'use strict';

    App.TasksController = Ember.ArrayController.extend({
        sortProperties: ['entered_date'],
        sortAscending: false,
        title: 'Tasks',
        name: null,
        actions: {
            createTask: function() {
                var name = null,
                    task = null,
                    tasks = null;

                name = this.get('name').trim();
                if (!name) {
                    return;
                }

                task = this.store.createRecord('task', {
                    name: name
                });
                task.save();

                this.set('name', '');
                tasks = this.store.find('task');
                this.set('content', tasks);
            }
        }
    });

})(NSPomodoroApp, Ember, DS);