package steamapi

import javax.servlet.http.Cookie

public class Utilities {
    private static final int COOKIE_MAX_AGE = 6480000 //90 days

    private Utilities() {
        // private constructor - prevents instance instantiation
    }

    static void setCookie(response, cookieName, value) {
        def c = new Cookie(cookieName, value)
        c.path = '/'
        c.maxAge = COOKIE_MAX_AGE
        response.addCookie(c)
    }

    static void removeCookie(response, cookieName){
        def c = new Cookie(cookieName, "")
        c.path = '/'
        c.maxAge = 0
        response.addCookie(c)
    }

    static boolean checkCookieValue(request, session, cookieIdentifier, sessionIdentifier) {
        boolean foundCookie = false
        request.cookies.each {
            if (it.name == cookieIdentifier) {
                foundCookie = true
                session[sessionIdentifier] = it.value
            }
        }

        return foundCookie
    }
}
