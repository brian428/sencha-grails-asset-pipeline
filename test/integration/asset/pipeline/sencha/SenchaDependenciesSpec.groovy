package asset.pipeline.sencha

import asset.pipeline.AssetHelper
import asset.pipeline.DirectiveProcessor
import asset.pipeline.JsAssetFile
import grails.test.spock.IntegrationSpec
import spock.lang.Ignore

class SenchaDependenciesSpec extends IntegrationSpec {

    def cleanup() {
        SenchaCoffeeAssetFile.reset()
        SenchaJsAssetFile.reset()
    }

    def "creates the proper asset file for a CoffeeScript source file"() {
        given: "A uri that depends on the Deft JS library CoffeeScript source"
            SenchaAssetHelper.senchaAppRootPath = "deft_coffee"
            String contentType        = "application/javascript"
            File sourceFile = new File( "grails-app/assets/javascripts/deft_coffee.coffee" )
        when:
            SenchaCoffeeAssetFile assetFile = AssetHelper.assetForFile( sourceFile )
        then:
            assetFile != null
    }

    def "creates the proper asset file for a JavaScript source file"() {
        given: "A uri that depends on the Deft JS library CoffeeScript source"
            SenchaAssetHelper.senchaAppRootPath = "deft_js"
            String contentType        = "application/javascript"
            File sourceFile = new File( "grails-app/assets/javascripts/deft_js.js" )
        when:
            JsAssetFile assetFile = AssetHelper.assetForFile( sourceFile )
        then:
            assetFile != null
    }

    def "gets Sencha class dependency list order correct for the Deft JS library CoffeeScript source"() {
        given: "A uri that depends on the Deft JS library CoffeeScript source"
            SenchaAssetHelper.senchaAppRootPath = "deft_coffee"
            String contentType        = "application/javascript"
            File sourceFile = new File( "grails-app/assets/javascripts/deft_coffee.coffee" )
            SenchaCoffeeAssetFile assetFile = AssetHelper.assetForFile( sourceFile )
            DirectiveProcessor directiveProcessor = new DirectiveProcessor( contentType )
            String basePath = "deft_coffee"
        when:
            List dependencyList = directiveProcessor.getFlattenedRequireList( assetFile )
        then:
            checkDeftDependencies( basePath, dependencyList )
    }

    def "gets Sencha class dependency list order correct for the Deft JS library JavaScript source"() {
        given: "A uri that depends on the Deft JS library JavaScript source"
            SenchaAssetHelper.senchaAppRootPath = "deft_js"
            String contentType        = "application/javascript"
            File sourceFile = new File( "grails-app/assets/javascripts/deft_js.js" )
            JsAssetFile assetFile = AssetHelper.assetForFile( sourceFile )
            DirectiveProcessor directiveProcessor = new DirectiveProcessor( contentType )
            String basePath = "deft_js"
        when:
            List dependencyList = directiveProcessor.getFlattenedRequireList( assetFile )
        then:
            checkDeftDependencies( basePath, dependencyList )
    }

    def "gets Sencha class dependency list order correct when using different requires syntaxes and alternate class names"() {
        given: "A uri that depends on files using different requires syntaxes and alternate class names"
            SenchaAssetHelper.senchaAppRootPath = "requires_syntaxes"
            String contentType        = "application/javascript"
            File sourceFile = new File( "grails-app/assets/javascripts/requires_syntaxes.coffee" )
            SenchaCoffeeAssetFile assetFile = AssetHelper.assetForFile( sourceFile )
            DirectiveProcessor directiveProcessor = new DirectiveProcessor( contentType )
            String basePath = "requires_syntaxes"
        when:
            List dependencyList = directiveProcessor.getFlattenedRequireList( assetFile )
        then:
            isIncludedBefore( basePath, "FileLast.js", "FileFour.js", dependencyList )
            isIncludedBefore( basePath, "FileFour.js", "FileThree.js", dependencyList )
            isIncludedBefore( basePath, "FileThree.js", "FileTwo.js", dependencyList )
            isIncludedBefore( basePath, "FileTwo.js", "FileOne.js", dependencyList )
            isIncludedBefore( null, "FileOne.js", "requires_syntaxes.js", dependencyList )
    }

    def "generates WAR with correctly ordered precompiled assets"() {
        given: "A generated WAR for a project using the sencha-grails-asset-pipeline plugin"
            File precompiledFile = new File( "test-project/target/assets/deft_js2.js" )
            if( !precompiledFile.exists() ) {
                throw new Error( "It looks like the generated WAR build for /test-project doesn't exist. You may need to run a 'grails war' in the test-project first, so tests against the precompiled assets can run." )
            }
        when:
            String fileContent = precompiledFile.text
        then:
            fileContent.length() > 0
            checkWarDependencies( fileContent )
    }

    private def checkDeftDependencies( basePath, dependencyList ) {
        def result
        List compareList = deftDependencyCompareList()
        for( thisComparison in compareList ) {
            result = isIncludedBefore( basePath, thisComparison.firstFile, thisComparison.secondFile, dependencyList )
            if( !result ) return false
        }
        return true
    }

    private def checkWarDependencies( String fileContent ) {
        def result
        List compareList = deftDependencyCompareList()
        for( thisComparison in compareList ) {
            result = isIncludedInCompiledFileBefore( fileContent, thisComparison.firstFile, thisComparison.secondFile )
            if( !result ) return false
        }
        return true
    }

    private def isIncludedBefore( String basePath, String firstFile, String secondFile, dependencyList ) {
        String firstFilePath = basePath ? "${ basePath }/${ firstFile }" : firstFile
        String secondFilePath = basePath ? "${ basePath }/${ secondFile }" : secondFile
        println "Checking that ${ firstFilePath } is loaded before ${secondFilePath }"
        return dependencyList.findIndexOf{ it.path == firstFilePath } < dependencyList.findIndexOf{ it.path == secondFilePath }
    }

    private def isIncludedInCompiledFileBefore( String fileContent, String firstFile, String secondFile ) {
        String firstClassName = firstFile.replaceAll( "/", "." ).replaceAll( ".js", "" )
        String secondClassName = secondFile.replaceAll( "/", "." ).replaceAll( ".js", "" )
        println "Checking that ${ firstClassName } is loaded before ${secondClassName }"
        return fileContent.indexOf( /Ext.define("${ firstClassName }"/ ) < fileContent.indexOf( /Ext.define("${ secondClassName }"/ )
    }

    private List deftDependencyCompareList() {
        List dependencyList = []
        dependencyList.push( [ firstFile: "Deft/util/Function.js", secondFile: "Deft/log/Logger.js" ] )
        dependencyList.push( [ firstFile: "Deft/log/Logger.js", secondFile: "Deft/ioc/DependencyProvider.js" ] )
        dependencyList.push( [ firstFile: "Deft/log/Logger.js", secondFile: "Deft/ioc/Injector.js" ] )
        dependencyList.push( [ firstFile: "Deft/ioc/DependencyProvider.js", secondFile: "Deft/ioc/Injector.js" ] )
        dependencyList.push( [ firstFile: "Deft/event/LiveEventListener.js", secondFile: "Deft/event/LiveEventBus.js" ] )
        dependencyList.push( [ firstFile: "Deft/ioc/Injector.js", secondFile: "Deft/mixin/Injectable.js" ] )
        dependencyList.push( [ firstFile: "Deft/core/Class.js", secondFile: "Deft/mixin/Injectable.js" ] )
        dependencyList.push( [ firstFile: "Deft/log/Logger.js", secondFile: "Deft/mixin/Injectable.js" ] )
        dependencyList.push( [ firstFile: "Deft/util/DeftMixinUtils.js", secondFile: "Deft/mixin/Observer.js" ] )
        dependencyList.push( [ firstFile: "Deft/event/LiveEventBus.js", secondFile: "Deft/mvc/ComponentSelectorListener.js" ] )
        dependencyList.push( [ firstFile: "Deft/mvc/ComponentSelectorListener.js", secondFile: "Deft/mvc/ComponentSelector.js" ] )
        dependencyList.push( [ firstFile: "Deft/core/Class.js", secondFile: "Deft/mvc/Observer.js" ] )
        dependencyList.push( [ firstFile: "Deft/core/Class.js", secondFile: "Deft/mvc/ViewController.js" ] )
        dependencyList.push( [ firstFile: "Deft/log/Logger.js", secondFile: "Deft/mvc/ViewController.js" ] )
        dependencyList.push( [ firstFile: "Deft/mvc/ComponentSelector.js", secondFile: "Deft/mvc/ViewController.js" ] )
        dependencyList.push( [ firstFile: "Deft/mixin/Injectable.js", secondFile: "Deft/mvc/ViewController.js" ] )
        dependencyList.push( [ firstFile: "Deft/mixin/Observer.js", secondFile: "Deft/mvc/ViewController.js" ] )
        dependencyList.push( [ firstFile: "Deft/mvc/Observer.js", secondFile: "Deft/mvc/ViewController.js" ] )
        dependencyList.push( [ firstFile: "Deft/util/Function.js", secondFile: "Deft/promise/Consequence.js" ] )
        dependencyList.push( [ firstFile: "Deft/promise/Consequence.js", secondFile: "Deft/promise/Resolver.js" ] )
        dependencyList.push( [ firstFile: "Deft/promise/Resolver.js", secondFile: "Deft/promise/Deferred.js" ] )
        dependencyList.push( [ firstFile: "Deft/promise/Deferred.js", secondFile: "Deft/promise/Promise.js" ] )
        dependencyList.push( [ firstFile: "Deft/promise/Promise.js", secondFile: "Deft/promise/Chain.js" ] )
        return dependencyList
    }

}
