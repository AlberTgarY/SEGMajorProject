package main.java.com.projectBackEnd;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

/**
 * Class for returning a URI For location purposes.
 */
public class URLLocation {
    /**
     * Class for converting a slug into a URLEncoded URI with its root
     * @param slug The slug to be encoded
     * @param root The root before it e.g. /root/
     * @return The URI encoded for use
     */
    public static URI location(String slug, String root) {
        String encodedSlug;
        try {
            encodedSlug = URLEncoder.encode(slug, java.nio.charset.StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            return null; //Difficult to make this error be thrown, not covered by tests.
        }
        return URI.create(root + encodedSlug);
    }
}

