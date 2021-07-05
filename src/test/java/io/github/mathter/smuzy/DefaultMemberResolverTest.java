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

public class DefaultMemberResolverTest {
    @Test
    public void test0() throws Exception {
        final MemberResolver memberResolver = new DefaultMemberResolver(DefaultNameResolver.Factory.get());
        final MemberResolver.MemberResolveResult mrr = memberResolver.resolve(TestBean.class, "lastName");

        Assertions.assertEquals(TestBean.class.getField("lastName"), mrr.getField());
        Assertions.assertEquals(TestBean.class.getMethod("getLastName"), mrr.getGetMethod());
        Assertions.assertEquals(TestBean.class.getMethod("setLastName", String.class), mrr.getSetMethod());
    }

    @Test
    public void test1() throws Exception {
        final MemberResolver memberResolver = new DefaultMemberResolver(DefaultNameResolver.Factory.get());
        final MemberResolver.MemberResolveResult mrr = memberResolver.resolve(Person.class, "lastName");

        Assertions.assertNull(null);
        Assertions.assertEquals(Person.class.getMethod("getLastName"), mrr.getGetMethod());
        Assertions.assertEquals(Person.class.getMethod("setLastName", String.class), mrr.getSetMethod());
    }

    @Test
    public void test2() throws Exception {
        final MemberResolver memberResolver = new DefaultMemberResolver(DefaultNameResolver.Factory.get());
        final MemberResolver.MemberResolveResult mrr = memberResolver.resolve(TestBean.class, TestBean.class.getMethod("equals", Object.class));

        Assertions.assertNull(mrr.getField());
        Assertions.assertNull(mrr.getGetMethod());
        Assertions.assertNull(mrr.getSetMethod());
    }
}
