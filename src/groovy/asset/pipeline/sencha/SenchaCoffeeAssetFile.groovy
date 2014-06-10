package asset.pipeline.sencha

import asset.pipeline.coffee.CoffeeAssetFile
import groovy.util.logging.Log4j

@Log4j
class SenchaCoffeeAssetFile extends CoffeeAssetFile {

    static processors = CoffeeAssetFile.processors
    static final contentType = CoffeeAssetFile.contentType
    static extensions = CoffeeAssetFile.extensions
    static final String compiledExtension = CoffeeAssetFile.compiledExtension

    Boolean senchaProcessed = false

    String directiveForLine( String line ) {
        String result = super.directiveForLine( line )

        if( !senchaProcessed ) {
            if( !SenchaAssetHelper.senchaClassDictionary ) {
                SenchaAssetHelper.initClassDictionary()
            }

            log.debug( "Building requires list for: ${ file }" )
            List senchaRequires = SenchaAssetHelper.senchaClassDictionary.buildRequiresList( file, SenchaAssetHelper.jsAssetsRootPath )
            if( senchaRequires ) {
                result = result ? "${ result }," : "require "
                result += senchaRequires.join( ',' )
            }

            senchaProcessed = true
            log.debug( "Resulting requires list: ${ result }" )
        }

        return result
    }

    static void reset() {
        SenchaAssetHelper.senchaAppRootPath = null
        SenchaAssetHelper.senchaClassDictionary = null
    }

}
