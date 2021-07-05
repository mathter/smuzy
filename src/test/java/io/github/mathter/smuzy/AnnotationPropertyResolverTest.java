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

import io.github.mathter.smuzy.annotation.AnnotationPropertyResolver;
import io.github.mathter.smuzy.data.TestBean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Date;

public class AnnotationPropertyResolverTest {
    @Test
    public void test0() {
        final PropertyResolver propertyResolver = new AnnotationPropertyResolver(DefaultNameResolver.Factory.get(), DefaultMemberResolver.Factory.get());
        final Collection<Property> properties = propertyResolver.resolve(TestBean.class);

        Assertions.assertEquals(4, properties.size());
        Assertions.assertTrue(properties.contains(new AnnotationProperty("desc:name")));
        Assertions.assertTrue(properties.contains(new AnnotationProperty("desc:lastName")));
        Assertions.assertTrue(properties.contains(new AnnotationProperty("desc:birthday")));
        Assertions.assertTrue(properties.contains(new AnnotationProperty("desc:nick")));
    }

    @Test
    public void test1() throws Exception {
        final PropertyResolver propertyResolver = new AnnotationPropertyResolver(DefaultNameResolver.Factory.get(), DefaultMemberResolver.Factory.get());

        Assertions.assertEquals(
                new AnnotationProperty("desc:birthday"),
                propertyResolver.resolve(TestBean.class.getMethod("setBirthday", Date.class))
        );

        Assertions.assertEquals(
                new AnnotationProperty("desc:birthday"),
                propertyResolver.resolve(TestBean.class.getMethod("getBirthday"))
        );

        Assertions.assertEquals(
                new AnnotationProperty("desc:name"),
                propertyResolver.resolve(TestBean.class.getField("name"))
        );
    }
}
