package com.briankotek.sencha.dependencies

import grails.test.spock.IntegrationSpec
import spock.lang.Shared

class SenchaDependencyLookupSpec extends IntegrationSpec {

    @Shared
    File tempDir

    def setupSpec() {
        tempDir = new File( "./temp" )
        tempDir.mkdir()
    }

    def cleanupSpec() {
        tempDir.deleteDir()
    }

    def "creates temp directory"() {
        when:
            File testTempDir = tempDir
        then:
            testTempDir.exists()
    }

    def "handles standard requires array in JavaScript"() {
        given:
            String senchaClassName = "MyApp.test.TestClass"
            String parentClassName = "MyApp.test.ParentTestClass"
            String code = """
                Ext.define('${ senchaClassName }', {
                    extend: '${ parentClassName }',
                    requires: ['MyApp.test.Class1', 'MyApp.test.Class2', 'MyApp.test.Class3']
                })
            """
            File sourceFile = writeTempFile( "test1.js", code )
            SenchaDependencyLookup lookup = createSenchaDependencyLookup()
        when:
            List result = lookup.buildLookup( sourceFile )
        then:
            result.size() == 4
            result.each { assert it in [ parentClassName, 'MyApp.test.Class1', 'MyApp.test.Class2', 'MyApp.test.Class3' ] }
        when:
            SenchaClassDictionary dictionary = lookup.senchaClassDictionary
            SenchaClass senchaClass = dictionary.senchaClassByFilePath[ sourceFile.canonicalPath ]
        then:
            checkSenchaClass( senchaClass, sourceFile, dictionary, senchaClassName, parentClassName )
    }

    def "handles multiline requires array in JavaScript"() {
        given:
            String senchaClassName = "MyApp.test.TestClass"
            String alternateClassName = "MyApp.TestClass"
            String parentClassName = "MyApp.test.ParentTestClass"
            String code = """
                Ext.define('${ senchaClassName }', {
                    extend: '${ parentClassName }',
                    alternateClassName: ['${ alternateClassName }'],
                    requires: [
                        'MyApp.test.Class1',
                        'MyApp.test.Class2',
                        'MyApp.test.Class3'
                    ]
                })
            """
            File sourceFile = writeTempFile( "test1.js", code )
            SenchaDependencyLookup lookup = createSenchaDependencyLookup()
        when:
            List result = lookup.buildLookup( sourceFile )
        then:
            result.size() == 4
            result.each { assert it in [ parentClassName, 'MyApp.test.Class1', 'MyApp.test.Class2', 'MyApp.test.Class3' ] }
        when:
            SenchaClassDictionary dictionary = lookup.senchaClassDictionary
            SenchaClass senchaClass = dictionary.senchaClassByFilePath[ sourceFile.canonicalPath ]
        then:
            checkSenchaClass( senchaClass, sourceFile, dictionary, senchaClassName, parentClassName, alternateClassName )
    }

    def "handles standard requires array in CoffeeScript"() {
        given:
            String senchaClassName = "MyApp.test.TestClass"
            String alternateClassName = "MyApp.TestClass"
            String parentClassName = "MyApp.test.ParentTestClass"
            String code = """
                Ext.define('${ senchaClassName }',
                    extend: '${ parentClassName }'
                    alternateClassName: ['${ alternateClassName }']
                    requires: ['MyApp.test.Class1', 'MyApp.test.Class2', 'MyApp.test.Class3']
                )
            """
            File sourceFile = writeTempFile( "test1.coffee", code )
            SenchaDependencyLookup lookup = createSenchaDependencyLookup()
        when:
            List result = lookup.buildLookup( sourceFile )
        then:
            result.size() == 4
            result.each { assert it in [ parentClassName, 'MyApp.test.Class1', 'MyApp.test.Class2', 'MyApp.test.Class3' ] }
        when:
            SenchaClassDictionary dictionary = lookup.senchaClassDictionary
            SenchaClass senchaClass = dictionary.senchaClassByFilePath[ sourceFile.canonicalPath ]
        then:
            checkSenchaClass( senchaClass, sourceFile, dictionary, senchaClassName, parentClassName, alternateClassName )
    }

    def "handles multiline requires array in CoffeeScript"() {
        given:
            String senchaClassName = "MyApp.test.TestClass"
            String alternateClassName = "MyApp.TestClass"
            String parentClassName = "MyApp.test.ParentTestClass"
            String code = """
                Ext.define('${ senchaClassName }',
                    extend: '${ parentClassName }'
                    alternateClassName: ['${ alternateClassName }']
                    requires: [
                        'MyApp.test.Class1'
                        'MyApp.test.Class2'
                        'MyApp.test.Class3'
                    ]
                )
            """
            File sourceFile = writeTempFile( "test1.coffee", code )
            SenchaDependencyLookup lookup = createSenchaDependencyLookup()
        when:
            List result = lookup.buildLookup( sourceFile )
        then:
            result.size() == 4
            result.each { assert it in [ parentClassName, 'MyApp.test.Class1', 'MyApp.test.Class2', 'MyApp.test.Class3' ] }
        when:
            SenchaClassDictionary dictionary = lookup.senchaClassDictionary
            SenchaClass senchaClass = dictionary.senchaClassByFilePath[ sourceFile.canonicalPath ]
        then:
            checkSenchaClass( senchaClass, sourceFile, dictionary, senchaClassName, parentClassName, alternateClassName )
    }

    def "handles a file with multiple class definitions in CoffeeScript"() {
        given:
            String senchaClassName = "MyApp.test.TestClass"
            String alternateClassName = "MyApp.TestClass"
            String parentClassName = "MyApp.test.ParentTestClass"
            String senchaClassName2 = "MyApp.test.TestClass2"
            String alternateClassName2 = "MyApp.TestClass2"
            String parentClassName2 = "MyApp.test.ParentTestClass2"
            String senchaClassName3 = "MyApp.test.TestClass3"
            String alternateClassName3 = "MyApp.TestClass3"
            String overrideClassName3 = "MyApp.test.OverrideTestClass3"

            String code = """
                Ext.define('${ senchaClassName }',
                    extend: '${ parentClassName }'
                    alternateClassName: ['${ alternateClassName }']
                    requires: ['MyApp.test.Class1', 'MyApp.test.Class2']
                )

                Ext.define('${ senchaClassName2 }',
                    extend: '${ parentClassName2 }'
                    alternateClassName: ['${ alternateClassName2 }']
                    requires: ['MyApp.test.Class3', 'MyApp.test.Class4']
                )

                ### Commented class should not be processed!
                Ext.define('MyApp.test.SkippedClass',
                    extend: 'MyApp.test.SkippedClassParent'
                    requires: ['MyApp.test.SkippedClass1', 'MyApp.test.SkippedClass2']
                )
                ###

                Ext.define('${ senchaClassName3 }',
                    override: '${ overrideClassName3 }'
                    alternateClassName: ['${ alternateClassName3 }']
                    requires: ['MyApp.test.Class5', 'MyApp.test.Class6']
                )
            """

            List expectedRequiredList = [
                    parentClassName, parentClassName2, overrideClassName3,
                    'MyApp.test.Class1', 'MyApp.test.Class2', 'MyApp.test.Class3',
                    'MyApp.test.Class4', 'MyApp.test.Class5', 'MyApp.test.Class6'
            ]

            File sourceFile = writeTempFile( "test1.coffee", code )
            SenchaDependencyLookup lookup = createSenchaDependencyLookup()
        when:
            List result = lookup.buildLookup( sourceFile )
        then:
            result.size() == 9
            result.each { assert it in expectedRequiredList }
            result.each { assert !( it in [ 'MyApp.test.SkippedClassParent', 'MyApp.test.SkippedClass1', 'MyApp.test.SkippedClass2' ] ) }
        when:
            SenchaClassDictionary dictionary = lookup.senchaClassDictionary
            SenchaClass senchaClass = dictionary.senchaClassByQualifiedClassName[ senchaClassName ]
            SenchaClass senchaClass2 = dictionary.senchaClassByQualifiedClassName[ senchaClassName2 ]
            SenchaClass senchaClass3 = dictionary.senchaClassByQualifiedClassName[ senchaClassName3 ]
        then:
            checkSenchaClass( senchaClass, sourceFile, dictionary, senchaClassName, parentClassName, alternateClassName, true )
            checkSenchaClass( senchaClass2, sourceFile, dictionary, senchaClassName2, parentClassName2, alternateClassName2, true )
            checkSenchaClass( senchaClass3, sourceFile, dictionary, senchaClassName3, null, alternateClassName3, true )
    }

    def "handles a file with multiple class definitions in JavaScript"() {
        given:
            String senchaClassName = "MyApp.test.TestClass"
            String alternateClassName = "MyApp.TestClass"
            String parentClassName = "MyApp.test.ParentTestClass"
            String senchaClassName2 = "MyApp.test.TestClass2"
            String alternateClassName2 = "MyApp.TestClass2"
            String parentClassName2 = "MyApp.test.ParentTestClass2"
            String senchaClassName3 = "MyApp.test.TestClass3"
            String alternateClassName3 = "MyApp.TestClass3"
            String overrideClassName3 = "MyApp.test.OverrideTestClass3"

            String code = """
                Ext.define('${ senchaClassName }', {
                    extend: '${ parentClassName }'
                    alternateClassName: ['${ alternateClassName }']
                    requires: ['MyApp.test.Class1', 'MyApp.test.Class2']
                })

                Ext.define('${ senchaClassName2 }', {
                    extend: '${ parentClassName2 }'
                    alternateClassName: ['${ alternateClassName2 }']
                    requires: ['MyApp.test.Class3', 'MyApp.test.Class4']
                })

                ### Commented class should not be processed!
                Ext.define('MyApp.test.SkippedClass', {
                    extend: 'MyApp.test.SkippedClassParent'
                    requires: ['MyApp.test.SkippedClass1', 'MyApp.test.SkippedClass2']
                })
                ###

                Ext.define('${ senchaClassName3 }', {
                    override: '${ overrideClassName3 }'
                    alternateClassName: ['${ alternateClassName3 }']
                    requires: ['MyApp.test.Class5', 'MyApp.test.Class6']
                })
            """

            List expectedRequiredList = [
                    parentClassName, parentClassName2, overrideClassName3,
                    'MyApp.test.Class1', 'MyApp.test.Class2', 'MyApp.test.Class3',
                    'MyApp.test.Class4', 'MyApp.test.Class5', 'MyApp.test.Class6'
            ]

            File sourceFile = writeTempFile( "test1.js", code )
            SenchaDependencyLookup lookup = createSenchaDependencyLookup()
        when:
            List result = lookup.buildLookup( sourceFile )
        then:
            result.size() == 9
            result.each { assert it in expectedRequiredList }
            result.each { assert !( it in [ 'MyApp.test.SkippedClassParent', 'MyApp.test.SkippedClass1', 'MyApp.test.SkippedClass2' ] ) }
        when:
            SenchaClassDictionary dictionary = lookup.senchaClassDictionary
            SenchaClass senchaClass = dictionary.senchaClassByQualifiedClassName[ senchaClassName ]
            SenchaClass senchaClass2 = dictionary.senchaClassByQualifiedClassName[ senchaClassName2 ]
            SenchaClass senchaClass3 = dictionary.senchaClassByQualifiedClassName[ senchaClassName3 ]
        then:
            checkSenchaClass( senchaClass, sourceFile, dictionary, senchaClassName, parentClassName, alternateClassName, true )
            checkSenchaClass( senchaClass2, sourceFile, dictionary, senchaClassName2, parentClassName2, alternateClassName2, true )
            checkSenchaClass( senchaClass3, sourceFile, dictionary, senchaClassName3, null, alternateClassName3, true )
    }

    def "handles Ext.require/syncRequire"() {
        given:
            String senchaClassName = "MyApp.test.TestClass"
            String alternateClassName = "MyApp.TestClass"
            String parentClassName = "MyApp.test.ParentTestClass"
            String code = """
                Ext.define('${ senchaClassName }',
                    extend: '${ parentClassName }'
                    alternateClassName: ['${ alternateClassName }']

                    Ext.require( ['MyApp.test.Class1', 'MyApp.test.Class2'] )
                    Ext.syncRequire( 'MyApp.test.Class3' )

                )
            """
            File sourceFile = writeTempFile( "test1.coffee", code )
            SenchaDependencyLookup lookup = createSenchaDependencyLookup()
        when:
            List result = lookup.buildLookup( sourceFile )
        then:
            result.size() == 4
            result.each { assert it in [ parentClassName, 'MyApp.test.Class1', 'MyApp.test.Class2', 'MyApp.test.Class3' ] }
        when:
            SenchaClassDictionary dictionary = lookup.senchaClassDictionary
            SenchaClass senchaClass = dictionary.senchaClassByFilePath[ sourceFile.canonicalPath ]
        then:
            checkSenchaClass( senchaClass, sourceFile, dictionary, senchaClassName, parentClassName, alternateClassName )
    }

    private checkSenchaClass( SenchaClass senchaClass, File sourceFile, SenchaClassDictionary dictionary, String className, String extendsClass, String altClassName=null, Boolean isMultiClassFile=false ) {
        String fileNameWithoutExtension = sourceFile.name.replaceFirst( ~/.[^.]+$/, '' )

        assert senchaClass.qualifiedClassName == className
        assert senchaClass.fileName == fileNameWithoutExtension
        assert senchaClass.filePath == sourceFile.canonicalPath
        assert senchaClass.extendsClass == extendsClass
        if( altClassName ) {
            assert senchaClass.alternateClassNames.size() == 1
            assert senchaClass.alternateClassNames[ 0 ] == altClassName
        }

        assert dictionary.senchaClassByQualifiedClassName[ className ] == senchaClass
        if( !isMultiClassFile ) {
            assert dictionary.senchaClassByFilePath[ sourceFile.canonicalPath ] == senchaClass
            assert dictionary.senchaClassByFileName[ fileNameWithoutExtension ] == senchaClass
        }
        if( altClassName ) {
            assert dictionary.senchaClassByAlternateClassName[ altClassName ] == senchaClass
        }

        return true
    }

    private File writeTempFile( String fileName, String content ) {
        File newFile = new File( tempDir, fileName )
        newFile.write( content )
        return newFile
    }

    private SenchaDependencyLookup createSenchaDependencyLookup() {
        return new SenchaDependencyLookup( senchaClassDictionary: new SenchaClassDictionary() )
    }

}
