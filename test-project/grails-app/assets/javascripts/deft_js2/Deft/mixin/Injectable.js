// Generated by CoffeeScript 1.6.3
/*
Copyright (c) 2012-2013 [DeftJS Framework Contributors](http://deftjs.org)
Open source under the [MIT License](http://en.wikipedia.org/wiki/MIT_License).
*/

/**
* A mixin that marks a class as participating in dependency injection. Used in conjunction with Deft.ioc.Injector.
*/

Ext.define('Deft.mixin.Injectable', {
  requires: ['Deft.core.Class', 'Deft.ioc.Injector', 'Deft.log.Logger', 'Deft.util.DeftMixinUtils'],
  /**
  	@private
  */

  onClassMixedIn: function(target) {
    target.prototype.constructor = this.createMixinInterceptor(target.prototype.constructor);
    target.onExtended(function(clazz, config) {
      if (config.hasOwnProperty("constructor")) {
        config.constructor = Deft.mixin.Injectable.createMixinInterceptor(config.constructor);
      } else {
        clazz.prototype.constructor = Deft.mixin.Injectable.createMixinInterceptor(clazz.prototype.constructor);
      }
      return true;
    });
  },
  statics: {
    MIXIN_COMPLETED_KEY: "$injected",
    PROPERTY_NAME: "inject",
    /**
    		* @private
    */

    createMixinInterceptor: function(targetMethod) {
      return Ext.Function.createInterceptor(targetMethod, function() {
        Deft.mixin.Injectable.constructorInterceptor(this, arguments);
        return true;
      });
    },
    /**
    		* @private
    */

    constructorInterceptor: function(target, targetInstanceConstructorArguments) {
      var injectConfig, mixinCompletedKey, propertyName;
      mixinCompletedKey = Deft.mixin.Injectable.MIXIN_COMPLETED_KEY;
      propertyName = Deft.mixin.Injectable.PROPERTY_NAME;
      if (!target[mixinCompletedKey]) {
        Deft.util.DeftMixinUtils.mergeSuperclassProperty(target, propertyName, Deft.mixin.Injectable.propertyMergeHandler);
        injectConfig = target[propertyName];
        Deft.Injector.inject(injectConfig, target, targetInstanceConstructorArguments, false);
        Deft.mixin.Injectable.afterMixinProcessed(target);
      }
      return true;
    },
    /**
    		* @private
    		* Called by DeftMixinUtils.mergeSuperclassProperty(). Allows each mixin to define its own
    		* customized subclass/superclass merge logic.
    */

    propertyMergeHandler: function(mergeTarget, mergeSource) {
      var dataInjectObject, identifier, _i, _len;
      if (Ext.isString(mergeSource)) {
        mergeSource = [mergeSource];
      }
      if (Ext.isArray(mergeSource)) {
        dataInjectObject = {};
        for (_i = 0, _len = mergeSource.length; _i < _len; _i++) {
          identifier = mergeSource[_i];
          dataInjectObject[identifier] = identifier;
        }
        mergeSource = dataInjectObject;
      }
      mergeTarget = Ext.apply(mergeTarget, mergeSource);
      return mergeTarget;
    },
    /**
    		@private
    */

    afterMixinProcessed: function(target) {
      target[Deft.mixin.Injectable.MIXIN_COMPLETED_KEY] = true;
    }
  }
});
