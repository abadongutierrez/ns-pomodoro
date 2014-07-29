NSPomodoroApp.WorkController = Ember.ArrayController.extend({
    offset: 0,

    actions: {
        backward: function() {
            console.log('backward');
            this.set('offset', this.get('offset') - 1);
            this.set('model', this.store.find('work', { offset: this.get('offset') }));
        },

        today: function() {
            console.log('today');
            this.set('offset', 0);
            this.set('model', this.store.find('work', { offset: this.get('offset') }));
        },

        forward: function() {
            console.log('forward');
            this.set('offset', this.get('offset') + 1);
            this.set('model', this.store.find('work', { offset: this.get('offset') }));
        }
    }
});