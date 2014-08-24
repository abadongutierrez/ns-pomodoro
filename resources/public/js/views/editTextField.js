(function(App, Ember, DS) {
    'use strict';

    App.EditTextFieldView = Ember.TextField.extend({
        focusOnInsert: function () {
            // Re-set input value to get rid of a reduntant text selection
            this.$().val(this.$().val());
            this.$().focus();
        }.on('didInsertElement')
    });

    Ember.Handlebars.helper('edit-text-field', App.EditTextFieldView);

})(NSPomodoroApp, Ember, DS);