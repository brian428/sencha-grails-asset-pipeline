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
            SenchaCoffeeAssetFile.senchaAppRootPath = "deft_coffee"
            String contentType        = "application/javascript"
            File sourceFile = new File( "grails-app/assets/javascripts/deft_coffee.coffee" )
        when:
            SenchaCoffeeAssetFile assetFile = AssetHelper.assetForFile( sourceFile )
        then:
            assetFile != null
    }

    def "creates the proper asset file for a JavaScript source file"() {
        given: "A uri that depends on the Deft JS library CoffeeScript source"
            SenchaJsAssetFile.senchaAppRootPath = "deft_js"
            String contentType        = "application/javascript"
            File sourceFile = new File( "grails-app/assets/javascripts/deft_js.js" )
        when:
            JsAssetFile assetFile = AssetHelper.assetForFile( sourceFile )
        then:
            assetFile != null
    }

    def "gets Sencha class dependency list order correct for the Deft JS library CoffeeScript source"() {
        given: "A uri that depends on the Deft JS library CoffeeScript source"
            SenchaCoffeeAssetFile.senchaAppRootPath = "deft_coffee"
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
            SenchaJsAssetFile.senchaAppRootPath = "deft_js"
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
            SenchaCoffeeAssetFile.senchaAppRootPath = "requires_syntaxes"
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

    private def isIncludedBefore( String basePath, String firstFile, String secondFile, dependencyList ) {
        String firstFilePath = basePath ? "${ basePath }/${ firstFile }" : firstFile
        String secondFilePath = basePath ? "${ basePath }/${ secondFile }" : secondFile
        println "Checking that ${ firstFilePath } is loaded before ${secondFilePath }"
        return dependencyList.findIndexOf{ it.path == firstFilePath } < dependencyList.findIndexOf{ it.path == secondFilePath }
    }

    private def checkDeftDependencies( basePath, dependencyList ) {
        isIncludedBefore( basePath, "Deft/util/Function.js", "Deft/log/Logger.js", dependencyList )
        isIncludedBefore( basePath, "Deft/log/Logger.js", "Deft/ioc/DependencyProvider.js", dependencyList )
        isIncludedBefore( basePath, "Deft/log/Logger.js", "Deft/ioc/Injector.js", dependencyList )
        isIncludedBefore( basePath, "Deft/ioc/DependencyProvider.js", "Deft/ioc/Injector.js", dependencyList )
        isIncludedBefore( basePath, "Deft/event/LiveEventListener.js", "Deft/event/LiveEventBus.js", dependencyList )
        isIncludedBefore( basePath, "Deft/ioc/Injector.js", "Deft/mixin/Injectable.js", dependencyList )
        isIncludedBefore( basePath, "Deft/core/Class.js", "Deft/mixin/Injectable.js", dependencyList )
        isIncludedBefore( basePath, "Deft/log/Logger.js", "Deft/mixin/Injectable.js", dependencyList )
        isIncludedBefore( basePath, "Deft/util/DeftMixinUtils.js", "Deft/mixin/Observer.js", dependencyList )
        isIncludedBefore( basePath, "Deft/event/LiveEventBus.js", "Deft/mvc/ComponentSelectorListener.js", dependencyList )
        isIncludedBefore( basePath, "Deft/mvc/ComponentSelectorListener.js", "Deft/mvc/ComponentSelector.js", dependencyList )
        isIncludedBefore( basePath, "Deft/core/Class.js", "Deft/mvc/Observer.js", dependencyList )
        isIncludedBefore( basePath, "Deft/core/Class.js", "Deft/mvc/ViewController.js", dependencyList )
        isIncludedBefore( basePath, "Deft/log/Logger.js", "Deft/mvc/ViewController.js", dependencyList )
        isIncludedBefore( basePath, "Deft/mvc/ComponentSelector.js", "Deft/mvc/ViewController.js", dependencyList )
        isIncludedBefore( basePath, "Deft/mixin/Injectable.js", "Deft/mvc/ViewController.js", dependencyList )
        isIncludedBefore( basePath, "Deft/mixin/Observer.js", "Deft/mvc/ViewController.js", dependencyList )
        isIncludedBefore( basePath, "Deft/mvc/Observer.js", "Deft/mvc/ViewController.js", dependencyList )
        isIncludedBefore( basePath, "Deft/util/Function.js", "Deft/promise/Consequence.js", dependencyList )
        isIncludedBefore( basePath, "Deft/promise/Consequence.js", "Deft/promise/Resolver.js", dependencyList )
        isIncludedBefore( basePath, "Deft/promise/Resolver.js", "Deft/promise/Deferred.js", dependencyList )
        isIncludedBefore( basePath, "Deft/promise/Deferred.js", "Deft/promise/Promise.js", dependencyList )
        isIncludedBefore( basePath, "Deft/promise/Promise.js", "Deft/promise/Chain.js", dependencyList )
    }

}
