package org.apache.shiro.pf4j.annotation;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests for {@link AuthcMapping} annotation attributes.
 */
public class AuthcMappingTest {

    @AuthcMapping(id = "testId", title = "Test Title", desc = "Test Description")
    static class AnnotatedClass {
    }

    @AuthcMapping(id = "minimalId")
    static class MinimalAnnotatedClass {
    }

    @Test
    public void shouldRetainIdAttribute() {
        AuthcMapping mapping = AnnotatedClass.class.getAnnotation(AuthcMapping.class);
        assertNotNull(mapping);
        assertEquals("testId", mapping.id());
    }

    @Test
    public void shouldRetainTitleAttribute() {
        AuthcMapping mapping = AnnotatedClass.class.getAnnotation(AuthcMapping.class);
        assertEquals("Test Title", mapping.title());
    }

    @Test
    public void shouldRetainDescAttribute() {
        AuthcMapping mapping = AnnotatedClass.class.getAnnotation(AuthcMapping.class);
        assertEquals("Test Description", mapping.desc());
    }

    @Test
    public void shouldDefaultTitleToEmptyString() {
        AuthcMapping mapping = MinimalAnnotatedClass.class.getAnnotation(AuthcMapping.class);
        assertNotNull(mapping);
        assertEquals("", mapping.title());
    }

    @Test
    public void shouldDefaultDescToEmptyString() {
        AuthcMapping mapping = MinimalAnnotatedClass.class.getAnnotation(AuthcMapping.class);
        assertEquals("", mapping.desc());
    }

    @Test
    public void shouldBePresentOnAnnotatedClass() {
        assertTrue(AnnotatedClass.class.isAnnotationPresent(AuthcMapping.class));
    }

    @Test
    public void shouldNotBePresentOnPlainClass() {
        assertFalse(String.class.isAnnotationPresent(AuthcMapping.class));
    }
}
