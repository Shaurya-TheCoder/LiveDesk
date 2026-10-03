package com.livedesk.ticket;

import java.security.SecureRandom;

public final class RecoveryCodeGenerator {

    private static final String CHARACTERS =
            "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private static final int GROUP_LENGTH = 4;
    private static final int GROUPS = 4;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private RecoveryCodeGenerator() {
    }

    public static String generate() {
        StringBuilder code = new StringBuilder(
                GROUP_LENGTH * GROUPS + (GROUPS - 1)
        );

        for (int group = 0; group < GROUPS; group++) {

            if (group > 0) {
                code.append("-");
            }

            for (int i = 0; i < GROUP_LENGTH; i++) {
                int index = SECURE_RANDOM.nextInt(CHARACTERS.length());
                code.append(CHARACTERS.charAt(index));
            }
        }

        return code.toString();
    }
}