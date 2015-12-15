package steamapi

import groovyx.net.http.HTTPBuilder
import groovyx.net.http.HttpResponseDecorator

class RestApiService {

    def grailsApplication

    /**
     * Sets up and makes a REST call to the given domain and forward URI
     *
     * @param domain - The target domain
     * @param path - Forward uri to send to
     * @param contentType - Content type to request e.g. "application/json"
     * @param query - A map of params to pass with the request
     * @param requestType - The request type, eg."POST". Defaults to "GET".
     * @return
     */
    def request(String domain, String path, Map query = [:], String contentType = "application/json", String requestType = "get", String username = null,
                String password = null){
        def http = new HTTPBuilder(domain)
        def result

        if(username != null || password != null){
            http.auth.basic(username, password)
        }

        Map settings = configureHTTPSettings(path, contentType, query)

        //Setup the failure handler
        http.handler.failure = { HttpResponseDecorator resp, reader ->
            result = reader
            if(grailsApplication.config.logging.api.info){
                println("Failure")
                println("Reader: " + reader)
                println("Response: " + resp)
                println("Response Status: " + resp.status)
            }
        }

        //Setup the success handler
        http.handler.success = {HttpResponseDecorator resp, reader ->
            result = reader
            if(grailsApplication.config.logging.api.info){
                println("Success")
                println("Response Status: " + resp.status)
            }
        }

        try {

            //Make the request
            if (requestType.toLowerCase() == "post") {
                http.post(settings)

                //info logging
                if (grailsApplication.config.logging.api.info) {
                    println("Calling with POST")
                }
            } else {
                http.get(settings)

                //info logging
                if (grailsApplication.config.logging.api.info) {
                    println("Calling with GET")
                }
            }

        } catch (Exception e){
            result = null
        }


        return result
    }

    /**
     * Configures the Map, settings, to be used in the HTTP call made by request()
     *
     * @param path - Forward uri to send to
     * @param contentType - Content type to request e.g. "application/json"
     * @param query - A map of params to pass with the request
     * @return
     */
    private Map configureHTTPSettings(String path, String contentType, Map query){
        Map settings = [:]

        //Verify the path and add it to settings
        if(!path.startsWith("/")){
            path = "/" + path
        }
        settings.put("path", path)

        //Add the query map if exists
        if(query.size() > 0){
            settings.put("query", query)
        }

        //Verify the contentType and add to settings if exists
        if(contentType && contentType.length() > 0){
            settings.put("contentType", contentType)
        }

        return settings
    }
}

