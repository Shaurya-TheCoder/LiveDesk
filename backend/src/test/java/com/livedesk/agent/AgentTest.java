package com.livedesk.agent;

import com.livedesk.agent.domain.Agent;
import com.livedesk.agent.domain.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AgentTest {

    @Test
    void constructorTrimsAndLowercasesEmailAndRejectsBlankName() {
        Agent agent = new Agent("Asha", "  Asha@Example.COM ", "hashed", Role.AGENT);

        assertEquals("asha@example.com", agent.getEmail());
        assertThrows(IllegalArgumentException.class,
                () -> new Agent(" ", "a@b.com", "hashed", Role.AGENT));
    }

    @Test
    void activeChatCountCannotExceedMaxConcurrencyOrGoBelowZero() {
        Agent agent = new Agent("Asha", "asha@example.com", "hashed", Role.AGENT);
        int max = agent.getMaxConcurrency();

        assertThrows(IllegalStateException.class, agent::decrementActiveChatCount);

        for (int i = 0; i < max; i++) {
            agent.incrementActiveChatCount();
        }

        assertEquals(max, agent.getActiveChatCount());
        assertThrows(IllegalStateException.class, agent::incrementActiveChatCount);
    }
}