package software.mayr.smserver.data.messagedata;

public enum ContentType {
    MESSAGE, PICTURE, AUDI, VIDEO;

    public static ContentType getValueOf(String stringContentType) {
            stringContentType = stringContentType.toUpperCase();
        for (ContentType contentType : values()) {
            if (stringContentType.equals(contentType.name())) {
                return contentType;
            }
        }
        return null;
    }
}