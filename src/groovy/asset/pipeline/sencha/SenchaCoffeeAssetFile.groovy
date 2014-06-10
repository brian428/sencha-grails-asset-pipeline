package asset.pipeline.sencha

import asset.pipeline.coffee.CoffeeAssetFile
import com.briankotek.sencha.dependencies.SenchaClassDictionary
import com.briankotek.sencha.dependencies.SenchaDependencyLookup
import groovy.util.logging.Log4j

@Log4j
class SenchaCoffeeAssetFile extends CoffeeAssetFile {

    static processors = CoffeeAssetFile.processors
    static final contentType = CoffeeAssetFile.contentType
    static extensions = CoffeeAssetFile.extensions
    static final String compiledExtension = CoffeeAssetFile.compiledExtension

    static Boolean inferRequires = false
    private static File senchaAppRoot = null

    Boolean senchaProcessed = false

    static SenchaClassDictionary getSenchaClassDictionary() {
        return SenchaAssetHelper.senchaClassDictionary
    }

    static setSenchaClassDictionary( SenchaClassDictionary senchaClassDictionary ) {
        SenchaAssetHelper.senchaClassDictionary = senchaClassDictionary
    }

    String directiveForLine( String line ) {
        String result = super.directiveForLine( line )

        if( !senchaProcessed ) {
            String assetMapping = SenchaAssetHelper.assetMapping
            String jsAssetsRootPath = "./grails-app/${ assetMapping }/javascripts"

            if( !senchaClassDictionary ) {
                initClassDictionary( jsAssetsRootPath )
            }

            log.debug( "Building requires list for: ${ file }" )
            List senchaRequires = senchaClassDictionary.buildRequiresList( file, jsAssetsRootPath )
            if( senchaRequires ) {
                result = result ? "${ result }," : "require "
                result += senchaRequires.join( ',' )
            }

            senchaProcessed = true
            log.debug( "Resulting requires list: ${ result }" )
        }

        return result
    }

    void initClassDictionary( String jsAssetsRootPath ) {
        if( SenchaAssetHelper.senchaAppRootPath ) {
            jsAssetsRootPath += "/${ SenchaAssetHelper.senchaAppRootPath }"
        }

        senchaAppRoot = new File( jsAssetsRootPath )
        SenchaDependencyLookup dependencyLookup = new SenchaDependencyLookup( appRoot: senchaAppRoot, inferRequires: inferRequires )
        dependencyLookup.init()
        senchaClassDictionary = dependencyLookup.senchaClassDictionary
    }

    static void reset() {
        SenchaAssetHelper.senchaAppRootPath = null
        senchaClassDictionary = null
    }

}
