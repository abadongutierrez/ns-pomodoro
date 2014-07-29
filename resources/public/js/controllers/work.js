(function(App, Ember, DS) {
    'use strict';

    NSPomodoroApp.WorkController = Ember.ArrayController.extend({
        offset: 0,

        actions: {
            backward: function() {
                this.set('offset', this.get('offset') - 1);
                this.set('model', this.store.find('work', { offset: this.get('offset') }));
            },

            today: function() {
                this.set('offset', 0);
                this.set('model', this.store.find('work', { offset: this.get('offset') }));
            },

            forward: function() {
                this.set('offset', this.get('offset') + 1);
                this.set('model', this.store.find('work', { offset: this.get('offset') }));
            }
        }
    });

})(NSPomodoroApp, Ember, DS);