package com.briankotek.sencha.dependencies

/**
 * Models a graph vertex for a given Sencha class file.
 */
class SenchaClass
{
	String fileName
	String qualifiedClassName
	String filePath
	String extendsClass
	List requires
	List alternateClassNames

	String toString() {
		return toName()
	}

	String toDetails( String basePath="" ) {
		return "${ qualifiedClassName ?: fileName } [${ filePath.replace( basePath, '' ) }]"
	}

	String toName() {
		return "${ qualifiedClassName ?: fileName }"
	}

	Boolean isInList( List targetList ) {
		return ( targetList.contains( filePath ) || targetList.contains( qualifiedClassName ) || targetList.contains( fileName ) )
	}

	String convertFilePath( String rootPath ) {
		return filePath.replace( rootPath, '' ).replace( '\\', '/' ).replace( '.coffee', '' ).replace( '.js', '' )
	}

}

