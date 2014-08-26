(function(App, Ember, DS) {
    'use strict';

    App.TasksController = Ember.ArrayController.extend({
        sortProperties: ['entered_date'],
        sortAscending: false,
        title: 'Tasks',
        name: null,
        tagsStr: "",

        actions: {
            createTask: function() {
                var name = null,
                    task = null,
                    tasks = null,
                    self = this;

                name = this.get('name').trim();
                if (!name) {
                    return;
                }

                task = this.store.createRecord('task', {
                    name: name
                });
                task.save();

                this.set('name', '');
                this.store.find('task').then(function(data) {
                    self.set('content', data);    
                });
            },

            filterByTags: function() {
                var self = this, //
                    tasks = null, //
                    tagsStr = this.get('tagsStr'), //
                    tags = tagsStr.trim().length > 0 ? tagsStr.split(',') : [];
                console.log(tags);
                this.store.find('task', { filterTags: tags }).then(function(data) {
                    self.set('content', data);
                });
            }
        }
    });

})(NSPomodoroApp, Ember, DS);