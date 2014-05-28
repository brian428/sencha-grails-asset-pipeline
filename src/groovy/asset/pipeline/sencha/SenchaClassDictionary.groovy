package asset.pipeline.sencha

class SenchaClassDictionary {

    Map senchaClassByFilePath = [ : ]
    Map senchaClassByQualifiedClassName = [ : ]
    Map senchaClassByAlternateClassName = [ : ]
    Map senchaClassByFileName = [ : ]

    /**
     * Add a senchaClass to the dictionary.
     * @param senchaClass
     * @return
     */
    Boolean addSenchaClass( SenchaClass senchaClass ) {
        Boolean isExisting = false

        if( !senchaClassByFilePath[ senchaClass.filePath ] ) {
            senchaClassByFilePath[ senchaClass.filePath ] = senchaClass
        }
        else {
            mergeRequires( senchaClass )
            isExisting = true
        }

        if( senchaClass.qualifiedClassName ) {
            senchaClassByQualifiedClassName[ senchaClass.qualifiedClassName ] = senchaClass
        }

        if( senchaClass.fileName ) {
            senchaClassByFileName[ senchaClass.fileName ] = senchaClass
        }

        senchaClass.alternateClassNames.each {
            senchaClassByAlternateClassName[ it ] = senchaClass
        }

        return isExisting
    }

    /**
     * Find a senchaClass using the passed lookup key.
     * @param key
     * @return
     */
    SenchaClass findSenchaClass( String key ) {
        if( senchaClassByFilePath[ key ] ) {
            return senchaClassByFilePath[ key ]
        }
        if( senchaClassByQualifiedClassName[ key ] ) {
            return senchaClassByQualifiedClassName[ key ]
        }
        if( senchaClassByAlternateClassName[ key ] ) {
            return senchaClassByAlternateClassName[ key ]
        }

        // Still no match, so attempt to match partial file name
        def result = senchaClassByFilePath.find { fileName, thisSenchaClass ->
            if( key && thisSenchaClass.filePath.contains( key ) ) {
                if( key != "Base" || key == "Base" && !thisSenchaClass.qualifiedClassName ) {
                    println "WARNING: Matching ${ key } by partial file name to ${ thisSenchaClass.toDetails() }"
                    return true
                }
            }
            return false
        }

        if( result ) {
            return result.value
        }

        // File name may not be unique, so getting here can have unexpected results!
        /*
        if( senchaClassByFileName[ key ] ) {
            config.writeLog( "WARNING: Matching ${ key } by file name only. May cause unexpected results since file name alone may not be unique!" )
            return senchaClassByFileName[ key ]
        }
        */

        return null
    }

    /**
     * Merges requires data for a senchaClass onto an existing senchaClass.
     * @param senchaClass
     */
    void mergeRequires( SenchaClass senchaClass ) {
        SenchaClass existingSenchaClass = senchaClassByFilePath[ senchaClass.filePath ]
        existingSenchaClass.requires = ( existingSenchaClass.requires + senchaClass.requires ).unique() - null
        senchaClass.requires = existingSenchaClass.requires
    }

    /**
     * Given a file name, returns a list of required file names.
     * @param file The file to determine dependencies for.
     * @param pathToRemove An initial file path to remove from the paths of the required files. Usually the path to the root JS folder.
     */
    List buildRequiresList( File file, String pathToRemove = "" ) {
        List result = [ ]
        SenchaClass senchaClass = findSenchaClass( file.canonicalPath )

        if( senchaClass && senchaClass.requires ) {
            File removePath = new File( pathToRemove )

            result = senchaClass.requires.collect { it ->
                SenchaClass requiredClass = findSenchaClass( it )
                if( !requiredClass ) {
                    return null
                }
                else {
                    return requiredClass.convertFilePath( removePath.canonicalPath )
                }
            } - null
        }

        return result
    }

}
