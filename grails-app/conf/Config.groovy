import asset.pipeline.coffee.CoffeeAssetFile
import asset.pipeline.coffee.CoffeeScriptProcessor
import asset.pipeline.sencha.SenchaCoffeeAssetFile
import asset.pipeline.sencha.SenchaCoffeeScriptProcessor
import asset.pipeline.sencha.SenchaCoffeeScriptProcessor
import asset.pipeline.sencha.SenchaJsAssetFile

// configuration for plugin testing - will not be included in the plugin zip

log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}

    error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
           'org.codehaus.groovy.grails.web.pages', //  GSP
           'org.codehaus.groovy.grails.web.sitemesh', //  layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping', // URL mapping
           'org.codehaus.groovy.grails.commons', // core / classloading
           'org.codehaus.groovy.grails.plugins', // plugins
           'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'
}

/*
Relative path from assets/javascripts where the Sencha application is located. e.g. 'assets/javascripts/app' would
mean this value should be set to 'app'.
*/
//grails.assets.sencha.appRootPath = "js"
grails.assets.sencha.appRootPath = "app"
