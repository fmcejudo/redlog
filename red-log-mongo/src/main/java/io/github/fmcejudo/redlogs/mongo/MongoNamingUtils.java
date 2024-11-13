package io.github.fmcejudo.redlogs.mongo;

public final class MongoNamingUtils {

    private MongoNamingUtils() {
    }

    public static String composeCollectionName(final String prefix, final String collectionName) {
        if (prefix == null || prefix.trim().isEmpty()) {
            return camelCase(collectionName);
        }
        return camelCase(String.join("_", prefix, collectionName));
    }

    private static String camelCase(final String name) {
        StringBuilder builder = new StringBuilder();
        boolean upperCase = false;
        char[] charArray = name.toLowerCase().toCharArray();
        for (char c : charArray) {
            if (isDelimiterCharacter(c)) {
                upperCase = true;
                continue;
            }
            if (upperCase) {
                upperCase = false;
                builder.append(Character.toUpperCase(c));
                continue;
            }
            builder.append(c);

        }
        return builder.toString();
    }

    private static boolean isDelimiterCharacter(char c) {
        return c == ' ' || c == '_' || c == '-';
    }
}
