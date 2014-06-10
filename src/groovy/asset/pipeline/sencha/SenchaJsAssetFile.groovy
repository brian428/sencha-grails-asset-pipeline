package asset.pipeline.sencha

import asset.pipeline.JsAssetFile
import groovy.util.logging.Log4j

@Log4j
class SenchaJsAssetFile extends JsAssetFile {

    static processors = JsAssetFile.processors
    static final contentType = JsAssetFile.contentType
    static extensions = JsAssetFile.extensions
    static final String compiledExtension = JsAssetFile.compiledExtension

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
