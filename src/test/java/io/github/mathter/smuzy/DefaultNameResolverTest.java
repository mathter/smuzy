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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class DefaultNameResolverTest {
    private NameResolver nameResolver = new DefaultNameResolver();

    @Test
    public void testByGetter() throws Exception {
        Assertions.assertEquals(
                "name",
                this.nameResolver.resolvePropertyName(Interface.class.getMethod("getName"))
        );

        Assertions.assertEquals(
                "green",
                this.nameResolver.resolvePropertyName(Interface.class.getMethod("isGreen"))
        );

        Assertions.assertEquals(
                "green",
                this.nameResolver.resolvePropertyName(Interface.class.getMethod("getGreen"))
        );

        Assertions.assertEquals(
                "count",
                this.nameResolver.resolvePropertyName(Interface.class.getMethod("count"))
        );
    }

    @Test
    public void testBySetter() throws Exception {
        Assertions.assertEquals(
                "name",
                this.nameResolver.resolvePropertyName(Interface.class.getMethod("setName", String.class))
        );

        Assertions.assertEquals(
                "green",
                this.nameResolver.resolvePropertyName(Interface.class.getMethod("setGreen", boolean.class))
        );

        Assertions.assertEquals(
                "count",
                this.nameResolver.resolvePropertyName(Interface.class.getMethod("count", long.class))
        );

        Assertions.assertEquals(
                "status",
                this.nameResolver.resolvePropertyName(Interface.class.getMethod("setStatus", String.class))
        );
    }

    @Test
    public void testNotMapped() throws Exception {
        Assertions.assertNull(this.nameResolver.resolvePropertyName(Interface.class.getMethod("function", String.class, Date.class)));
    }

    public interface Interface {
        public String getName();

        public boolean isGreen();

        public boolean getGreen();

        public boolean setGreen(boolean value);

        public void setStatus(String value);

        public String setName(String value);

        public long count();

        public void count(long value);

        public void function(String param1, Date param2);
    }
}
