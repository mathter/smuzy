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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChainAccessorResolver implements AccessorResolver {

    private final List<AccessorResolver> chain = new ArrayList<>(3);

    public ChainAccessorResolver(AccessorResolver... resolvers) {
        for (AccessorResolver resolver : Objects.requireNonNull(resolvers)) {
            this.chain.add(resolver);
        }
    }

    public ChainAccessorResolver(List<AccessorResolver> resolvers) {
        this.chain.addAll(Objects.requireNonNull(resolvers));
    }

    @Override
    public <V, T> Accessor<V, T> resolve(Property property, Class<T> clazz) {
        return (Accessor<V, T>) this.chain
                .stream()
                .map(resolver -> resolver.resolve(property, clazz))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
}
