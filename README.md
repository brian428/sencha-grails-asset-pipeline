Grails sencha-grails-asset-pipeline Plugin
===================================

[![Build Status](https://travis-ci.org/brian428/sencha-grails-asset-pipeline.png?branch=master)](https://travis-ci.org/brian428/sencha-grails-asset-pipeline)

## Introduction

An add-on for the [ Asset Pipeline Plugin ](http://grails.org/plugin/asset-pipeline) that understands how to resolve file dependencies for Sencha Ext JS applications.

<table>
    <tr>
        <td><strong>Note:</strong> This plugin requires version asset-pipeline version 1.8.9 or higher! (For more, see <a href="https://github.com/bertramdev/asset-pipeline/pull/99" target="_blank">this pull request</a>.)</td>
    </tr>
</table>


## What It Does

This plugin can handle JavaScript files as well as CoffeeScript (if the [ coffee-asset-pipeline plugin ](http://grails.org/plugin/coffee-asset-pipeline) is installed). It parses the files in your Ext JS application folder (and subfolders) and builds a mapping of file names to Sencha class names, as well as what files each file requires. 

It will:

* Handle arrays of class names declared in the `requires:` property of a class.
* When using CoffeeScript, it should properly handle arrays using commas or arrays that omit commas by defining each element on a separate line.
* Implicitly require any class name declared in the `extend:` or `override:` property of a class.
* Require class names declared using `Ext.require()` and `Ext.syncRequire()`.
* Require the correct file(s) even when using an alternate class name (defined in the `alternateClassName:` array in a class).
* Determine the correct required files even when multiple classes are defined in a single file.
* It should even handle classes required by the `@require()` Sencha Cmd annotation.

## Configuration

Other than installing the plugin, the only other config option is to set a `grails.assets.sencha.appRootPath` in your `Config.groovy` file. This lets the plugin know what your Sencha application's root is. It's a relative path from your `assets/javascripts` folder. 

For example, if your app is located at: 
`grails-app/assets/javascripts/app`

then you'd set this config option to: 
`grails.assets.sencha.appRootPath = "app"`

By default, it will treat your `assets/javascripts` folder as the app root.