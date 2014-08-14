(function(App, Ember, DS) {
    'use strict';

    App.TaskController = Ember.ObjectController.extend({
        isEditing: false,
        isAddingTags: false,
        bufferedName: Ember.computed.oneWay('name'),
        tags: null,

        actions: {
            editName: function () {
                this.set('isEditing', true);
            },

            doneEditing: function() {
                var bufferedName = this.get('bufferedName').trim();
                if (Ember.isEmpty(bufferedName)) {
                    // The `doneEditing` action gets sent twice when the user hits
                    // enter (once via 'insert-newline' and once via 'focus-out').
                    //
                    // We debounce our call to 'removeTodo' so that it only gets
                    // made once.
                    Ember.run.debounce(this, 'delete', 0);
                } else {
                    var task = this.get('model');
                    task.set('name', bufferedName);
                    task.save();
                }

                // Re-set our newly edited title to persist its trimmed version
                this.set('bufferedName', bufferedName);
                this.set('isEditing', false);
            },

            cancelEditing: function () {
                this.set('bufferedName', this.get('name'));
                this.set('isEditing', false);
            },

            delete: function() {
                var answer = confirm("Are you sure of deleting this task?");
                if (answer == true) {
                    this.removeTask();
                }
            },

            startAddingTags: function() {
                this.set('isAddingTags', true);

                return false;
            },

            cancelAddingTags: function() {
                this.set('isAddingTags', false);
            },

            addTags: function() {
                if (!this.get('model.tags')) {
                    this.get('model').set('tags', this.get('tags').split(','));
                }
                else {
                    this.get('model.tags').pushObjects(this.get('tags').split(','));
                }
                this.set('tags', null);
                this.set('isAddingTags', false);
            }
        },

        removeTask: function () {
            var task = this.get('model');
            task.deleteRecord();
            task.save();
        }
    });

})(NSPomodoroApp, Ember, DS);