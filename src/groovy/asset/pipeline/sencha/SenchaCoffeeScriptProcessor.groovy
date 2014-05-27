package asset.pipeline.sencha

import asset.pipeline.coffee.CoffeeScriptProcessor
import groovy.io.FileType
import org.mozilla.javascript.Context

class SenchaCoffeeScriptProcessor extends CoffeeScriptProcessor {

	static senchaAppRoot = null
	static SenchaDependencyLookup dependencyLookup = null

	SenchaCoffeeScriptProcessor( precompiler=false ) {
    	super( precompiler )

		String jsAssetsRoot = "./grails-app/assets/javascripts"
		if( senchaAppRoot ) {
			jsAssetsRoot += "/${ senchaAppRoot }"
		}
		File appRoot = new File( jsAssetsRoot )
		println "app root: ${ appRoot.canonicalPath }"

		// Trigger parsing of files to store paths for later requires lookups?
		// dependencyLookup.init( appRoot )

		if( !dependencyLookup ) {
			println "PARSING SENCHA APP FILES LOOKING FOR REQUIRES"
			dependencyLookup = new SenchaDependencyLookup( appRoot: appRoot )
			dependencyLookup.init()

			def list = []

			appRoot.eachFileRecurse (FileType.FILES) { file ->
			  list << file
			}

			def temp = true
		}
  	}

	def process( input, assetFile ) {
		try {
			//def cx = Context.enter()
			// input is actual file content String
			// assetFile is the CoffeeAssetFile.
			// useful?: file (File object for the file)

			File temp = assetFile.file
			println "in sencha processing:"
			println temp.canonicalPath
			println temp.parent

			return super.process( input, assetFile )
		} catch( Exception e ) {
			throw new Exception( """
			Sencha 'requires' processing failed.
			$e
			""" )
		} finally {
			//Context.exit()
		}
	}

}
