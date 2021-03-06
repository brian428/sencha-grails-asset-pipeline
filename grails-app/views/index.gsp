<%@ page import="asset.pipeline.sencha.SenchaAssetHelper" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title></title>
    <!--
        The compiled coffee files end up at: /asset-pipeline/assets, e.g.:

        Source file at: grails-app/assets/javascripts/app/app.coffee
        Can be loaded from: http://localhost:8080/asset-pipeline/assets/app/app.js

        Source file at: grails-app/assets/javascripts/app/model/Probability.coffee
        Can be loaded from: http://localhost:8080/asset-pipeline/assets/app/model/Probability.js

        etc.
    -->
    <%-- <asset:javascript src="deft.js"/>
    <asset:javascript src="deft.js" />
    <script type="text/javascript" src="assets/deft.js"></script>
    --%>
</head>

<body>

    <g:if test="${ SenchaAssetHelper.senchaAppRootPath == 'deft_coffee' }">
        <h3>CoffeeScript Sencha Asset-Pipeline</h3>
        <a href="http://localhost:8080/sencha-grails-asset-pipeline/assets/deft_coffee.js?compile=false" target="_blank">Single File</a><br/>
        <a href="http://localhost:8080/sencha-grails-asset-pipeline/assets/deft_coffee.js" target="_blank">Aggregated File</a><br/>
    </g:if>

    <g:if test="${ SenchaAssetHelper.senchaAppRootPath == 'deft_js' }">
        <h3>JavaScript Sencha Asset-Pipeline</h3>
        <a href="http://localhost:8080/sencha-grails-asset-pipeline/assets/deft_js.js?compile=false" target="_blank">Single File</a><br/>
        <a href="http://localhost:8080/sencha-grails-asset-pipeline/assets/deft_js.js" target="_blank">Aggregated File</a><br/>
    </g:if>

</body>
</html>