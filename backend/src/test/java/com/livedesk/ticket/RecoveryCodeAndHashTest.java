package com.livedesk.ticket;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RecoveryCodeAndHashTest {

    // 4 groups of 4 chars joined by '-', using the generator's alphabet
    // (no I, O, 0, 1 to avoid look-alike characters).
    private static final String CODE_PATTERN = "^[A-HJ-NP-Z2-9]{4}(-[A-HJ-NP-Z2-9]{4}){3}$";

    @Test
    void generatedRecoveryCodeMatchesExpectedFormat() {
        for (int i = 0; i < 200; i++) {
            String code = RecoveryCodeGenerator.generate();
            assertTrue(code.matches(CODE_PATTERN), "Unexpected code format: " + code);
        }
    }

    @Test
    void generatedRecoveryCodesAreUnique() {
        Set<String> codes = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            codes.add(RecoveryCodeGenerator.generate());
        }
        assertEquals(1000, codes.size());
    }

    @Test
    void sha256MatchesKnownTestVectorAndIsDeterministic() {
        // Standard SHA-256 test vector for "abc"
        String expected = "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad";

        assertEquals(expected, HashUtil.sha256("abc"));
        assertEquals(HashUtil.sha256("abc"), HashUtil.sha256("abc"));
        assertNotEquals(HashUtil.sha256("abc"), HashUtil.sha256("abd"));
    }
}
