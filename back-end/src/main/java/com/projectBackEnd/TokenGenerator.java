package main.java.com.projectBackEnd;

import java.util.Random;

/**
 * Class which will generate a random alphanumeric token of a given length
 */
public class TokenGenerator {
    /**
     * Generates a random string assignment
     * @param length The length of the token to be returned
     * @param lex The lexicon of characters to choose from
     * @return A randomly generated assortment of letters from the given lex.
     */
    public static String generateToken(int length, String lex) {
        Random rand = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            sb.append(lex.charAt(rand.nextInt(lex.length())));
        }
        return sb.toString();
    }
}
