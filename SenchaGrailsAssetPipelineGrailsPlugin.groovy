import asset.pipeline.AssetHelper
import asset.pipeline.JsAssetFile
import asset.pipeline.sencha.SenchaJsAssetFile
import org.codehaus.groovy.grails.commons.GrailsApplication

class SenchaGrailsAssetPipelineGrailsPlugin {
    def version = "0.1"
    def grailsVersion = "2.0 > *"
    def pluginExcludes = [
            "grails-app/views/error.gsp",
            "grails-app/views/index.gsp"
    ]

    def title = "Sencha Grails Asset Pipeline Plugin for Sencha Applications"
    def author = "Brian Kotek"
    def authorEmail = ""
    def description = '''\
"Sencha Grails Asset Pipeline Plugin that understands how to order dependencies in Sencha applications.
'''

    def documentation = "https://github.com/brian428/sencha-grails-asset-pipeline"
    def license = "APACHE"
    def issueManagement = [ system: "Github", url: "https://github.com/brian428/sencha-grails-asset-pipeline/issues" ]

    def doWithDynamicMethods = { ctx ->
        def grailsApplication = ctx.getBean( GrailsApplication.APPLICATION_ID )
        SenchaJsAssetFile.senchaAppRootPath = grailsApplication.config?.grails?.assets?.sencha?.appRootPath

        // Replace JsAssetFile with SenchaJsAssetFile
        if( JsAssetFile in AssetHelper.assetSpecs ) {
            Collections.replaceAll( AssetHelper.assetSpecs, JsAssetFile, SenchaJsAssetFile )
        }
        else {
            AssetHelper.assetSpecs << SenchaJsAssetFile
        }

        if( manager?.hasGrailsPlugin( "coffee-asset-pipeline" ) ) {
            asset.pipeline.sencha.SenchaCoffeeAssetFile.senchaAppRootPath = grailsApplication.config?.grails?.assets?.sencha?.appRootPath

            // Replace CoffeeAssetFile with SenchaCoffeeAssetFile
            if( asset.pipeline.coffee.CoffeeAssetFile in AssetHelper.assetSpecs ) {
                Collections.replaceAll( AssetHelper.assetSpecs, asset.pipeline.coffee.CoffeeAssetFile, asset.pipeline.sencha.SenchaCoffeeAssetFile )
            }
            else {
                AssetHelper.assetSpecs << asset.pipeline.sencha.SenchaCoffeeAssetFile
            }
        }
    }

}
