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

    <g:if test="${ SenchaAssetHelper.senchaAppRootPath == 'deft_js2' }">
        <h3>JavaScript Sencha Asset-Pipeline TEST APP</h3>
        <a href="http://localhost:8080/sencha-pipeline-test/assets/deft_js2.js?compile=false" target="_blank">Single File</a><br/>
        <a href="http://localhost:8080/sencha-pipeline-test/assets/deft_js2.js" target="_blank">Aggregated File</a><br/>
    </g:if>

</body>
</html>