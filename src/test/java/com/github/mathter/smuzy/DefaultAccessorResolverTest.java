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
package com.github.mathter.smuzy;

import com.github.mathter.smuzy.annotation.AnnotationPropertyResolver;
import com.github.mathter.smuzy.data.TestBean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DefaultAccessorResolverTest {

    @Test
    public void testWithBeanPropertyResolver() {
        final AccessorResolver resolver = new DefaultAccessorResolver(DefaultPropertyResolver.Factory.get());
        final Accessor<String, TestBean> accessorName = resolver.resolve(new BeanProperty("name"), TestBean.class);
        final Accessor<String, TestBean> accessorLastName = resolver.resolve(new BeanProperty("lastName"), TestBean.class);
        final TestBean testBean = new TestBean();

        accessorName.set(testBean, "John");
        Assertions.assertEquals("John", testBean.name);

        accessorLastName.set(testBean, "LastName");
        Assertions.assertEquals("LastName", testBean.getLastName());
    }

    @Test
    public void testWithAnnotationPropertyResolver() {
        final AccessorResolver resolver = new DefaultAccessorResolver(new AnnotationPropertyResolver());
        final Accessor<String, TestBean> accessorName = resolver.resolve(new AnnotationProperty("desc:name"), TestBean.class);
        final Accessor<String, TestBean> accessorLastName = resolver.resolve(new AnnotationProperty("desc:lastName"), TestBean.class);
        final TestBean testBean = new TestBean();

        accessorName.set(testBean, "John");
        Assertions.assertEquals("John", testBean.name);

        accessorLastName.set(testBean, "LastName");
        Assertions.assertEquals("LastName", testBean.getLastName());
    }
}
