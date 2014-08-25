(function(App, Ember, DS) {
    'use strict';

    App.Tag = DS.Model.extend({
        name: DS.attr('string'),
        task: DS.belongsTo('task')
    });

    App.TagSerializer = DS.RESTSerializer.extend({
        primaryKey: 'tag_id'
    });

})(NSPomodoroApp, Ember, DS);