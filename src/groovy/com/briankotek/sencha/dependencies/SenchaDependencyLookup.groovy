package com.briankotek.sencha.dependencies

import groovy.io.FileType

import java.util.regex.MatchResult

class SenchaDependencyLookup {

    File appRoot
    SenchaClassDictionary senchaClassDictionary
    Boolean inferRequires = false
    Boolean inferOverrides = true

    void init() {
        senchaClassDictionary = new SenchaClassDictionary()
        def fileList = [ ]

        appRoot.eachFileRecurse( FileType.FILES ) { file ->
            fileList << file
        }

        iterateFiles( fileList )
    }

    void iterateFiles( List sourceFiles ) {
        int fileCounter = 0
        sourceFiles.each { File file ->
            buildLookup( file )
            fileCounter++
        }
    }


    List buildLookup( File file ) {
        String fileName = file.name.replaceAll( ".coffee", "" ).replaceAll( ".js", "" )
        String filePath = file.canonicalPath
        MatchResult annotatedRequire = file.text =~ /@require([^\n]*)\n/
        String jsSource = prepareSource( file )

        MatchResult definedNames = jsSource =~ /Ext\.define\('([^']*)'/
        MatchResult extendsClass = jsSource =~ /extend:'([^']*)'/
        MatchResult requires = jsSource =~ /requires:\[([^]]*)]/
        MatchResult override = jsSource =~ /override:'([^']*)'/
        MatchResult alternates = jsSource =~ /alternateClassName:\[([^]]*)]/
        MatchResult extSyncRequire = jsSource =~ /Ext\.syncRequire\(\[?([^\]|\)]*)]?\)/
        MatchResult extRequire = jsSource =~ /Ext\.require\(\[?([^\]|\)]*)]?\)/

        // Start building the full set of all required classes for this file...
        List extRequires = findRequired( extRequire, 0 )
        extRequires = extRequires + findRequired( extSyncRequire, 0 )
        extRequires = extRequires + findExtends( extendsClass, 0 )
        extRequires = extRequires + findAnnotatedRequires( annotatedRequire )

        if( inferRequires ) {
            MatchResult createdClasses = jsSource =~ /Ext\.create\('([^']*)'/
            extRequires = extRequires + findCreatedNames( createdClasses )
        }

        // Iterate class definitions
        if( definedNames.size() ) {
            definedNames.eachWithIndex { List thisDefinition, int i ->
                String thisDefinedName = findDefinedName( thisDefinition )
                String thisExtendsClass = findExtends( extendsClass, i )
                List thisAlternates = findAlternates( alternates, i )

                // Add onto any existing required classes, treating extended classes as required as well.
                extRequires = extRequires + findRequired( requires, i )
                extRequires = extRequires + findExtends( extendsClass, i )
                if( inferOverrides ) {
                    extRequires = extRequires + findOverrides( override, i )
                }

                extRequires = extRequires.unique() - null
                SenchaClass senchaClass = new SenchaClass(
                        fileName: fileName,
                        filePath: filePath,
                        qualifiedClassName: thisDefinedName,
                        requires: extRequires,
                        extendsClass: thisExtendsClass,
                        alternateClassNames: thisAlternates
                )

                Boolean alreadyExists = senchaClassDictionary.addSenchaClass( senchaClass )

                if( !alreadyExists ) {
                    //println( "Class processed: ${ senchaClass.toName() }" )
                }
            }
        }

        // Otherwise, no classes defined in this file so we'll just deal with the file and any explicit requires it has.
        else {
            extRequires = extRequires + findRequired( requires, 0 )

            extRequires = extRequires.unique() - null
            def senchaClass = new SenchaClass(
                    fileName: fileName,
                    filePath: filePath,
                    requires: extRequires
            )

            Boolean alreadyExists = senchaClassDictionary.addSenchaClass( senchaClass )
            if( !alreadyExists ) {
                //println( "File processed: ${ senchaClass.toName() }" )
            }
        }

        return extRequires
    }

    String prepareSource( File file ) {
        // Remove CS and JS block comments...
        def jsSource = file.text.replaceAll( "(?s)\\#\\#\\#.*?\\#\\#\\#", "" )
        jsSource = jsSource.replaceAll( "(?s)/\\*.*?\\*/", "" )

        // Omit all whitespace and fix any CoffeeScript arrays that omit commas...
        jsSource = jsSource.replaceAll( /(\t| )/, "" ).replaceAll( /(\r\n?|\n)/, "," )
        jsSource = jsSource.replaceAll( /[\s]/, "" )
        jsSource = jsSource.replaceAll( /\[,/, "[" ).replaceAll( /,\]/, "]" ).replaceAll( /(,,|,,,)/, "," )

        // Normalize quotes
        jsSource = jsSource.replaceAll( '"', "'" )

        return jsSource
    }

    List findRequired( MatchResult requires, int index ) {
        List result = [ ]
        if( requires?.size() > index ) {
            String normalizedResult = requires[ index ][ 1 ].replaceAll( "'", "" ).trim()
            result = normalizedResult.tokenize( "," )
        }
        return result
    }

    String findExtends( MatchResult extendsClass, int index ) {
        String result = null
        if( extendsClass?.size() > index ) {
            result = extendsClass[ index ][ 1 ].replaceAll( "'", "" ).trim()
        }
        return result
    }

    List findAlternates( MatchResult alternates, int index ) {
        List result = [ ]
        if( alternates?.size() > index ) {
            String normalizedResult = alternates[ index ][ 1 ].replaceAll( "'", "" ).trim()
            result = normalizedResult.tokenize( "," )
        }
        return result
    }

    String findDefinedName( List definedName ) {
        String result = null
        if( definedName?.size() > 0 ) {
            result = definedName[ 1 ].trim()
        }
        return result
    }

    List findCreatedNames( MatchResult createdClasses ) {
        List result = [ ]
        if( createdClasses?.size() ) {
            createdClasses.each {
                result << it[ 1 ].trim()
            }
        }
        return result
    }

    List findAnnotatedRequires( MatchResult requiredClasses ) {
        List result = [ ]
        if( requiredClasses?.size() ) {
            requiredClasses.each {
                result << it[ 1 ].replaceAll( "../", "" ).replaceAll( ".js", "" ).trim()
            }
        }
        return result
    }

    List findOverrides( MatchResult overrides, int index ) {
        List result = [ ]
        if( overrides?.size() > index ) {
            result = [ overrides[ index ][ 1 ].trim() ]
        }
        return result
    }

}
