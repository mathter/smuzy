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

import java.lang.reflect.Member;
import java.util.Collection;
import java.util.function.Supplier;

public interface PropertyResolver {

    public Property resolve(Member member);

    public <T> Collection<Property> resolve(Class<T> clazz);

    default <T> Collection<Property> resolve(Object object) {
        return this.resolve(object.getClass());
    }

    public interface Factory extends Supplier<PropertyResolver> {
    }
}
