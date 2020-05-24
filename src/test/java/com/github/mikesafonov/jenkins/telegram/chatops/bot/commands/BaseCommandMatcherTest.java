package com.github.mikesafonov.jenkins.telegram.chatops.bot.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Mike Safonov
 */
class BaseCommandMatcherTest {

    private BaseCommandMatcher matcher;

    @BeforeEach
    void setUp() {
        matcher = new NoArgsCommandMatcher();
    }

    @Test
    void shouldReturnAndMatcher() {
        EqualsCommandMatcher equalsCommandMatcher = new EqualsCommandMatcher("");

        CommandMatcher and = matcher.and(equalsCommandMatcher);

        assertThat(and).isInstanceOf(AndCommandMatcher.class).satisfies(andMatcher -> {
            assertThat(((AndCommandMatcher) andMatcher).getMatchers()).containsOnly(matcher, equalsCommandMatcher);
        });
    }

    @Test
    void shouldReturnOrMatcher() {
        EqualsCommandMatcher equalsCommandMatcher = new EqualsCommandMatcher("");

        CommandMatcher or = matcher.or(equalsCommandMatcher);

        assertThat(or).isInstanceOf(OrCommandMatcher.class).satisfies(andMatcher -> {
            assertThat(((OrCommandMatcher) andMatcher).getMatchers()).containsOnly(matcher, equalsCommandMatcher);
        });
    }

}
