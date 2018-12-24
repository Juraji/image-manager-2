/**
 * args[0] Method
 * args[1] ResourcePath
 * args[2] Headers
 * args[3] Options
 */
// noinspection JSReferencingArgumentsOutsideOfFunction,JSAnnotator,JSUnresolvedVariable
return (function (args) {
    var setRequestHeaders = function (request, headers) {
        Object.keys(headers).forEach(function (key) {
            request.setRequestHeader(key, headers[key]);
        });

        request.setRequestHeader("X-Requested-With", "XMLHttpRequest");
    };

    var doGet = function (url, headers, formData) {
        formData.append("_", (new Date()).getTime());

        var request = new XMLHttpRequest();
        var query = new URLSearchParams(formData).toString();

        request.open("GET", resourcePath + "?" + query, false);
        setRequestHeaders(request, headers);

        request.send();
        return request;
    };

    var doPost = function (url, headers, formData) {
        var request = new XMLHttpRequest();
        request.open("POST", resourcePath, false);
        setRequestHeaders(request, headers);

        request.send(formData);
        return request;
    };

    console.log("Image manager resource request:", args);
    var method = args[0];
    var resourcePath = args[1];
    var headers = args[2];

    var formData = new FormData();
    formData.append("source__url", window.location.pathname);
    formData.append("data", JSON.stringify({
        options: JSON.parse(args[3]),
        context: {}
    }));

    var request;
    if ("GET" === method) {
        request = doGet(resourcePath, headers, formData);
    } else if ("POST" === method) {
        request = doPost(resourcePath, headers, formData);
    }

    if (request.status === 200) {
        var responseData = JSON.parse(request.responseText);

        if (responseData.hasOwnProperty("resource_response")) {
            var bookmark = responseData.resource.options.hasOwnProperty("bookmarks") ?
                responseData.resource.options.bookmarks[0] : undefined;

            return JSON.stringify({
                status: request.status,
                bookmark: bookmark,
                data: responseData.resource_response.data
            });
        }
    }

    console.error("Resource get failed", request);
    return JSON.stringify({status: request.status});
})(arguments);