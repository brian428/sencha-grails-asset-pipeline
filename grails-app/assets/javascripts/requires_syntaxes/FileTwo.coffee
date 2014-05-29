Ext.define( 'FileTwo',
  alternateClassName: [ 'FileTwoAlt' ]

  init: ->
    Ext.syncRequire( ['FileThree'] )
    return
)