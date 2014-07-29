Ember.Inflector.inflector.uncountable('work');

var NSPomodoroApp = Ember.Application.create({
    LOG_TRANSITIONS: true
});

// NSPomodoro.ApplicationAdapter = DS.FixtureAdapter.extend();

NSPomodoroApp.ApplicationAdapter = DS.RESTAdapter.extend({
    namespace: 'api/v1',

    pathForType: function(type) {
        var returnType = this._super(type);
        console.log("Path for type: " + type + " > " + returnType);
        return returnType;
    }
});

// To be able to hande DS.attr('object')
NSPomodoroApp.ObjectTransform = DS.Transform.extend({
    deserialize: function(serialized) {
        console.log("deserialize");
        console.log(serialized);
        return Ember.isNone(serialized) ? {} : serialized;
    },

    serialize: function(deserialized) {
        console.log("serialize");
        console.log(deserialized);
        return Ember.isNone(deserialized) ? {} : deserialized;
    }
});