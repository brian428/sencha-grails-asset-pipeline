import asset.pipeline.AssetHelper
import asset.pipeline.JsAssetFile
import asset.pipeline.sencha.SenchaAssetContextHolder
import asset.pipeline.sencha.SenchaJsAssetFile
import org.codehaus.groovy.grails.commons.GrailsApplication

class SenchaGrailsAssetPipelineGrailsPlugin {
    def version = "0.1"
    def grailsVersion = "2.0 > *"
    def pluginExcludes = [
            "grails-app/views/error.gsp",
            "grails-app/views/index.gsp",
            "grails-app/assets/**",
            "grails-app/views/**",
            "web-app/**",
            "test-project/**"
    ]

    def title = "Sencha Grails Asset Pipeline Plugin for Sencha Applications"
    def author = "Brian Kotek"
    def authorEmail = ""
    def description = '''\
"Sencha Grails Asset Pipeline Plugin that understands how to order dependencies in Sencha applications. Requires asset-pipeline version 1.8.9 or higher.
'''

    def documentation = "https://github.com/brian428/sencha-grails-asset-pipeline"
    def license = "APACHE"
    def issueManagement = [ system: "Github", url: "https://github.com/brian428/sencha-grails-asset-pipeline/issues" ]

    def doWithSpring = {
        senchaAssetContextHolder(SenchaAssetContextHolder) { bean ->
            bean.factoryMethod = 'getInstance'
        }
    }

    def doWithDynamicMethods = { ctx ->
        def grailsApplication = ctx.getBean( GrailsApplication.APPLICATION_ID )
        SenchaJsAssetFile.senchaAppRootPath = grailsApplication.config?.grails?.assets?.sencha?.appRootPath

        // Replace JsAssetFile with SenchaJsAssetFile
        if( JsAssetFile in AssetHelper.assetSpecs ) {
            Collections.replaceAll( AssetHelper.assetSpecs, JsAssetFile, SenchaJsAssetFile )
        }
        else {
            println "adding new SenchaJsAssetFile class"
            AssetHelper.assetSpecs << SenchaJsAssetFile
        }

        // Proceed with CoffeeScript-related logic only if the coffee-asset-pipeline plugin in installed.
        if( manager?.hasGrailsPlugin( "coffee-asset-pipeline" ) ) {
            Class coffeeAssetFileClass = Class.forName( "asset.pipeline.coffee.CoffeeAssetFile" )
            Class senchaCoffeeAssetFileClass = Class.forName( "asset.pipeline.sencha.SenchaCoffeeAssetFile" )
            senchaCoffeeAssetFileClass.senchaAppRootPath = grailsApplication.config?.grails?.assets?.sencha?.appRootPath

            // Replace CoffeeAssetFile with SenchaCoffeeAssetFile
            if( coffeeAssetFileClass in AssetHelper.assetSpecs ) {
                Collections.replaceAll( AssetHelper.assetSpecs, coffeeAssetFileClass, senchaCoffeeAssetFileClass )
            }
            else {
                AssetHelper.assetSpecs << senchaCoffeeAssetFileClass
            }
        }
    }

}
