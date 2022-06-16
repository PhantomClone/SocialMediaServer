package software.mayr.smserver.web.httphandler;

public enum RequestMethod {

    POST, GET;

    public static RequestMethod getValueOf(String stringRequestMethod) {
        stringRequestMethod = stringRequestMethod.toUpperCase();
        for (RequestMethod requestMethod : values()) {
            if (requestMethod.name().equals(stringRequestMethod))
                return requestMethod;
        }
        return null;
    }

}
