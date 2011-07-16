<%--@elvariable id="model" type="java.util.Map"--%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html>
<head>
    <%@ include file="head.jsp" %>
    <script type="text/javascript" src="<c:url value="/script/swfobject.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/script/prototype.js"/>"></script>

    <sub:url value="/coverArt.view" var="coverArtUrl">
        <c:if test="${not empty model.coverArt}">
            <sub:param name="path" value="${model.coverArt.path}"/>
        </c:if>
    </sub:url>

    <meta name="og:title" content="${fn:escapeXml(model.songs[0].metaData.artist)} &mdash; ${fn:escapeXml(model.songs[0].metaData.album)}"/>
    <meta name="og:type" content="album"/>
    <meta name="og:image" content="http://${model.redirectFrom}.subsonic.org${coverArtUrl}&size=200"/>

    <script type="text/javascript">
        function init() {
            var flashvars = {
                id:"player1",
                screencolor:"000000",
                frontcolor:"<spring:theme code="textColor"/>",
                backcolor:"<spring:theme code="backgroundColor"/>",
                stretching: "fill",
                "playlist.position": "bottom",
                "playlist.size": 200
            };
            var params = {
                allowfullscreen:"true",
                allowscriptaccess:"always"
            };
            var attributes = {
                id:"player1",
                name:"player1"
            };
            swfobject.embedSWF("<c:url value="/flash/jw-player-5.6.swf"/>", "placeholder", "500", "500", "9.0.0", false, flashvars, params, attributes);
        }

        function playerReady(thePlayer) {
            var player = $("player1");
            var list = new Array();

        <c:forEach items="${model.songs}" var="song" varStatus="loopStatus">
        <sub:url value="/stream" var="streamUrl">
        <sub:param name="path" value="${song.path}"/>
        </sub:url>

            // TODO: Use video provider for aac, m4a
            list[${loopStatus.count-1}] = {
                file: "${streamUrl}&player=${model.player}",
                image: "${coverArtUrl}&size=500",
                title: "${fn:escapeXml(song.title)}",
                provider: "${song.video ? "video" : "sound"}",
                description: "${fn:escapeXml(song.metaData.artist)}"
            };

        <c:if test="${not empty song.metaData.duration}">
            list[${loopStatus.count-1}].duration = ${song.metaData.duration};
        </c:if>

        </c:forEach>

            player.sendEvent("LOAD", list);
            player.sendEvent("PLAY");
        }

    </script>
</head>

<body class="mainframe bgcolor1" style="padding-top:2em" onload="init();">

<div style="margin:auto;width:500px">
    <h1 >${model.songs[0].metaData.artist}</h1>
    <div style="float:left;padding-right:1.5em">
        <h2 style="margin:0;">${model.songs[0].metaData.album}</h2>
    </div>
    <div class="detail" style="float:right">Streaming by <a href="http://subsonic.org/" target="_blank"><b>Subsonic</b></a></div>

    <div style="clear:both;padding-top:1em">
        <div id="placeholder">
            <a href="http://www.adobe.com/go/getflashplayer" target="_blank"><fmt:message key="playlist.getflash"/></a>
        </div>
    </div>
    <div style="padding-top: 2em">${fn:escapeXml(model.share.description)}</div>
</div>
</body>
</html>
