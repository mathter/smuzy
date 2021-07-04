package com.github.mathter.smuzy;

import com.github.mathter.smuzy.annotation.AnnotationReferencedPropertyResolverFactory;
import com.github.mathter.smuzy.annotation.ReferencedProperties;
import com.github.mathter.smuzy.annotation.ReferencedProperties.Property;
import com.github.mathter.smuzy.data.TestBean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;

public class AnnotationReferencedPropertyResolverFactoryTest {
    @Test
    public void test() {
        final PropertyResolver.Factory propertyResolverFactory = new AnnotationReferencedPropertyResolverFactory(APRF.class);
        final PropertyResolver propertyResolver = propertyResolverFactory.get();

        Assertions.assertNotNull(propertyResolver);
        Collection<com.github.mathter.smuzy.Property> properties = propertyResolver.resolve(TestBean.class);

        Assertions.assertEquals(6, properties.size());
        Assertions.assertTrue(properties.contains(new AnnotationProperty("a:name")));
        Assertions.assertTrue(properties.contains(new AnnotationProperty("a:nick")));
        Assertions.assertTrue(properties.contains(new BeanProperty("name")));
        Assertions.assertTrue(properties.contains(new BeanProperty("lastName")));
        Assertions.assertTrue(properties.contains(new BeanProperty("nick")));
        Assertions.assertTrue(properties.contains(new BeanProperty("birthday")));
    }

    @ReferencedProperties(
            referencedClass = TestBean.class,
            properties = {
                    @Property(value = "a:name", beanProp = "name"),
                    @Property(value = "a:nick", beanProp = "nick")
            }
    )
    public static class APRF implements PropertyResolver.Factory {
        @Override
        public PropertyResolver get() {
            return new DefaultPropertyResolver();
        }
    }
}