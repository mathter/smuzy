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

import io.github.mathter.smuzy.data.Department;
import io.github.mathter.smuzy.data.Person;
import io.github.mathter.smuzy.data.TestBean;
import io.github.mathter.smuzy.data.TestBean2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Date;

public class MapSmuzyTest {
    @Test
    public void testSimple() {
        final Smuzy smuzy = Factory.Default.empty();
        final Person person = smuzy.as(Person.class);

        Assertions.assertNull(person.getName());
        Assertions.assertNull(person.getLastName());
        Assertions.assertNull(person.getBirthday());

        person.setName("John");
        Assertions.assertEquals("John", person.getName());
    }

    @Test
    public void testComplex() {
        final Smuzy smuzy = Factory.Default.empty();
        final Department department = smuzy.as(Department.class);

        department.setName("Foreign");
        Assertions.assertEquals("Foreign", department.getName());

        Assertions.assertNull(department.getManager());
        Assertions.assertNull(department.getStaff());

        department.setManager(Factory.Default.empty().as(Person.class));
        Assertions.assertNotNull(department.getManager());

        department.setStaff(Arrays.asList((Factory.Default.empty().as(Person.class)), Factory.Default.empty().as(Person.class)));
        Assertions.assertNotNull(department.getStaff());
    }

    @Test
    public void testAccessor() {
        final Smuzy smuzy = Factory.Default.empty();
        final Department department = smuzy.as(Department.class);

        AccessorResolver accessorResolver = new MapSmuzy.AccessResolver();
        Accessor<String, Smuzy> accessor = accessorResolver.resolve(new BeanProperty("name"), smuzy);
        accessor.set(smuzy, "Foreign");

        Assertions.assertEquals("Foreign", accessor.get(smuzy));
        Assertions.assertEquals("Foreign", department.getName());
    }

    @Test
    public void testOfObject() {
        final Date date = new Date();
        final Smuzy smuzy = Factory.Default.empty();
        final TestBean testBean = new TestBean();
        testBean.name = "John";
        testBean.lastName = "Brown";
        testBean.setNick("wow");
        testBean.setBirthday(date);

        Assertions.assertNotNull(smuzy.of(testBean));

        final Person person = smuzy.as(Person.class);
        Assertions.assertEquals("John", person.getName());
        Assertions.assertEquals("Brown", person.getLastName());
        Assertions.assertEquals(date, person.getBirthday());
    }

    @Test
    public void testAsClassWithCustomConstractor() {
        final Date date = new Date();
        final Smuzy smuzy = Factory.Default.empty();
        final TestBean2 testBean = new TestBean2("John");
        testBean.lastName = "Brown";
        testBean.setNick("wow");
        testBean.setBirthday(date);

        Assertions.assertNotNull(smuzy.of(testBean));

        final TestBean result = smuzy.as(TestBean.class);
        Assertions.assertEquals("John", result.name);
        Assertions.assertEquals("Brown", result.getLastName());
        Assertions.assertEquals(date, result.getBirthday());
    }
}
