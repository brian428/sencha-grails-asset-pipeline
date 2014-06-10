package asset.pipeline.sencha

import com.briankotek.sencha.dependencies.SenchaClassDictionary
import com.briankotek.sencha.dependencies.SenchaDependencyLookup

class SenchaAssetHelper {

    static Boolean inferRequires = false
    static String senchaAppRootPath
    static ConfigObject grailsConfig
    static SenchaClassDictionary senchaClassDictionary
    protected static File senchaAppRoot = null

    static String getAssetMapping() {
        def assetMapping = grailsConfig?.grails?.assets?.mapping ?: "assets"
        if( assetMapping.contains( "/" ) ) {
            String message = "the property [grails.assets.mapping] can only be one level" +
                    "deep.  For example, 'foo' and 'bar' would be acceptable values, but 'foo/bar' is not"
            throw new IllegalArgumentException( message )
        }
        return assetMapping
    }

    static initClassDictionary() {
        String assetMapping = assetMapping
        String jsAssetsRootPath = "./grails-app/${ assetMapping }/javascripts"

        if( SenchaAssetHelper.senchaAppRootPath ) {
            jsAssetsRootPath += "/${ SenchaAssetHelper.senchaAppRootPath }"
        }

        senchaAppRoot = new File( jsAssetsRootPath )

        SenchaDependencyLookup dependencyLookup = new SenchaDependencyLookup( appRoot: senchaAppRoot, inferRequires: inferRequires )
        dependencyLookup.init()
        senchaClassDictionary = dependencyLookup.senchaClassDictionary
    }

}
