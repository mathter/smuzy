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

import java.util.function.Supplier;

public interface AccessorResolver {
    public <V, T> Accessor<V, T> resolve(Property property, Class<T> clazz);

    default <V, T> Accessor<V, T> resolve(Property property, T object) {
        return (Accessor<V, T>) resolve(property, object.getClass());
    }

    public interface AccessorResolverFactory extends Supplier<AccessorResolver> {
    }
}
