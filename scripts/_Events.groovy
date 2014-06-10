eventAssetPrecompileStart = { assetConfig ->

    // Populate the raw config so the correct asset paths can be determined later.
    def senchaAssetHelperClass = classLoader.loadClass( "asset.pipeline.sencha.SenchaAssetHelper" )
    senchaAssetHelperClass.grailsConfig = config

    def assetHelperClass = classLoader.loadClass( "asset.pipeline.AssetHelper" )
    def jsAssetFileClass = classLoader.loadClass( "asset.pipeline.JsAssetFile" )
    def senchaJsAssetFileClass = classLoader.loadClass( "asset.pipeline.sencha.SenchaJsAssetFile" )
    senchaAssetHelperClass.senchaAppRootPath = config?.grails?.assets?.sencha?.appRootPath

    // Replace JsAssetFile with SenchaJsAssetFile
    if( jsAssetFileClass in assetHelperClass.assetSpecs ) {
        Collections.replaceAll( assetHelperClass.assetSpecs, jsAssetFileClass, senchaJsAssetFileClass )
    }
    else {
        assetHelperClass.assetSpecs << senchaJsAssetFileClass
    }

    // Proceed with CoffeeScript-related logic only if the coffee-asset-pipeline plugin in installed.
    if( pluginManager?.hasGrailsPlugin( "coffee-asset-pipeline" ) ) {
        def coffeeAssetFileClass = classLoader.loadClass( "asset.pipeline.coffee.CoffeeAssetFile" )
        def senchaCoffeeAssetFileClass = classLoader.loadClass( "asset.pipeline.sencha.SenchaCoffeeAssetFile" )

        // Replace CoffeeAssetFile with SenchaCoffeeAssetFile
        if( coffeeAssetFileClass in assetHelperClass.assetSpecs ) {
            Collections.replaceAll( assetHelperClass.assetSpecs, coffeeAssetFileClass, senchaCoffeeAssetFileClass )
        }
        else {
            assetHelperClass.assetSpecs << senchaCoffeeAssetFileClass
        }
    }

}
