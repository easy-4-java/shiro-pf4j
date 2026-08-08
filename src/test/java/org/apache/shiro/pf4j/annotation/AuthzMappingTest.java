package org.apache.shiro.pf4j.annotation;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests for {@link AuthzMapping} annotation attributes.
 */
public class AuthzMappingTest {

    @AuthzMapping(id = "authzId", title = "Authz Title", desc = "Authz Description")
    static class AnnotatedClass {
    }

    @AuthzMapping(id = "minimalAuthzId")
    static class MinimalAnnotatedClass {
    }

    @Test
    public void shouldRetainIdAttribute() {
        AuthzMapping mapping = AnnotatedClass.class.getAnnotation(AuthzMapping.class);
        assertNotNull(mapping);
        assertEquals("authzId", mapping.id());
    }

    @Test
    public void shouldRetainTitleAttribute() {
        AuthzMapping mapping = AnnotatedClass.class.getAnnotation(AuthzMapping.class);
        assertEquals("Authz Title", mapping.title());
    }

    @Test
    public void shouldRetainDescAttribute() {
        AuthzMapping mapping = AnnotatedClass.class.getAnnotation(AuthzMapping.class);
        assertEquals("Authz Description", mapping.desc());
    }

    @Test
    public void shouldDefaultTitleToEmptyString() {
        AuthzMapping mapping = MinimalAnnotatedClass.class.getAnnotation(AuthzMapping.class);
        assertNotNull(mapping);
        assertEquals("", mapping.title());
    }

    @Test
    public void shouldDefaultDescToEmptyString() {
        AuthzMapping mapping = MinimalAnnotatedClass.class.getAnnotation(AuthzMapping.class);
        assertEquals("", mapping.desc());
    }

    @Test
    public void shouldBePresentOnAnnotatedClass() {
        assertTrue(AnnotatedClass.class.isAnnotationPresent(AuthzMapping.class));
    }
}
