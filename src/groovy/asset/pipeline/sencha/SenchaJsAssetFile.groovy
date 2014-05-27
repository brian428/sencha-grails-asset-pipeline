package asset.pipeline.sencha

import asset.pipeline.AssetProcessorService
import asset.pipeline.JsAssetFile
import org.codehaus.groovy.grails.web.context.ServletContextHolder as SCH
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes as GA


class SenchaJsAssetFile extends JsAssetFile {

	static processors = JsAssetFile.processors
	static String senchaAppRootPath = null
	static Boolean inferRequires = false
	static SenchaClassDictionary senchaClassDictionary
	private static File senchaAppRoot = null

	Boolean senchaProcessed = false

	String directiveForLine(String line) {
		String result = super.directiveForLine( line )

		if( !senchaProcessed ) {
			def ctx = SCH.servletContext.getAttribute( GA.APPLICATION_CONTEXT )
			AssetProcessorService assetProcessorService = ctx.assetProcessorService
			String assetMapping = assetProcessorService.assetMapping
			String jsAssetsRootPath = "./grails-app/${ assetMapping }/javascripts"

			if( !senchaClassDictionary ) {
				initClassDictionary( jsAssetsRootPath )
			}

			List senchaRequires = senchaClassDictionary.buildRequiresList( file, jsAssetsRootPath )
			if( senchaRequires ) {
				result = result ? "${ result }," : "require "
				result += senchaRequires.join( ',' )
			}

			senchaProcessed = true
		}

		return result
	}

	void initClassDictionary( String jsAssetsRootPath ) {
		if( senchaAppRootPath ) {
			jsAssetsRootPath += "/${ senchaAppRootPath }"
		}

		senchaAppRoot = new File( jsAssetsRootPath )
		SenchaDependencyLookup dependencyLookup = new SenchaDependencyLookup( appRoot: senchaAppRoot, inferRequires: inferRequires )
		dependencyLookup.init()
		senchaClassDictionary = dependencyLookup.senchaClassDictionary
  	}

}
