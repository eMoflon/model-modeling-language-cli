package de.nexus.mmlcli.serializer;

import org.eclipse.emf.ecore.ENamedElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EcoreIdResolverTest {
    private EcoreIdResolver idResolver;

    @BeforeEach
    void setUp() {
        this.idResolver = new EcoreIdResolver();
    }

    @Test
    void resolveUnknownId() {
        ENamedElement namedElement = Mockito.mock(ENamedElement.class);
        final UUID mockedUUID = UUID.randomUUID();

        try (MockedStatic<UUID> uuidMockedStatic = Mockito.mockStatic(UUID.class)) {
            uuidMockedStatic.when(UUID::randomUUID).thenReturn(mockedUUID);

            assertEquals(mockedUUID, this.idResolver.resolveId(namedElement));
        }
    }

    @Test
    void resolveKnownId() throws IllegalAccessException {
        final Map<ENamedElement, UUID> customElementMapping = new HashMap<>();
        ENamedElement namedElement = Mockito.mock(ENamedElement.class);

        final UUID mockedUUID = UUID.randomUUID();
        customElementMapping.put(namedElement, mockedUUID);

        Field field = ReflectionUtils
                .findFields(EcoreIdResolver.class, f -> f.getName().equals("elementMapping"),
                        ReflectionUtils.HierarchyTraversalMode.TOP_DOWN)
                .get(0);

        field.setAccessible(true);
        field.set(this.idResolver, customElementMapping);

        assertEquals(mockedUUID, this.idResolver.resolveId(namedElement));
    }
}