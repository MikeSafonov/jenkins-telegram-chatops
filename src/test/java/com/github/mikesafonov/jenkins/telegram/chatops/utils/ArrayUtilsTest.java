package com.github.mikesafonov.jenkins.telegram.chatops.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Mike Safonov
 */
class ArrayUtilsTest {

    @Test
    void shouldRemoveFirstElement() {
        String[] original = new String[]{"one", "two", "three"};
        String[] actual = ArrayUtils.copyWithoutFirst(original);

        assertThat(actual).containsOnly("two", "three");
    }

}
