(function(App, Ember, DS) {
    'use strict';

    NSPomodoroApp.TaskController = Ember.ObjectController.extend({
        isEditing: false,
        bufferedName: Ember.computed.oneWay('name'),   
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
            }
        },

        removeTask: function () {
            var task = this.get('model');
            task.deleteRecord();
            task.save();
        }
    });

})(NSPomodoroApp, Ember, DS);