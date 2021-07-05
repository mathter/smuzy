/*
 * Smuzy DTO manipulation. Simple control of your data flow!
 *
 * Copyright (C) 2021 mathter@mail.ru
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.mathter.smuzy;

import io.github.mathter.smuzy.data.Person;
import io.github.mathter.smuzy.data.TestBean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;

public class DefaultPropertyResolverTest {
    @Test
    public void resolveMethodsOnly() throws Exception {
        final PropertyResolver propertyResolver = new DefaultPropertyResolver(new DefaultNameResolver());

        Assertions.assertEquals(new BeanProperty("name"), propertyResolver.resolve(Person.class.getMethod("getName")));
        Assertions.assertEquals(new BeanProperty("name"), propertyResolver.resolve(Person.class.getMethod("setName", String.class)));
    }

    @Test
    public void testMixed() throws Exception {
        final PropertyResolver propertyResolver = new DefaultPropertyResolver(new DefaultNameResolver());
        final Collection<Property> properties = propertyResolver.resolve(TestBean.class);

        Assertions.assertEquals(4, properties.size());
        Assertions.assertTrue(properties.contains(new BeanProperty("name")));
        Assertions.assertTrue(properties.contains(new BeanProperty("lastName")));
        Assertions.assertTrue(properties.contains(new BeanProperty("birthday")));
        Assertions.assertTrue(properties.contains(new BeanProperty("nick")));
    }
}
