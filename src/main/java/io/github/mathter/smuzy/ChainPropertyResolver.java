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

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class ChainPropertyResolver implements PropertyResolver {
    private final List<PropertyResolver> chain = new ArrayList<>(3);

    public ChainPropertyResolver(PropertyResolver... resolvers) {
        for (PropertyResolver resolver : Objects.requireNonNull(resolvers)) {
            this.chain.add(resolver);
        }
    }

    public ChainPropertyResolver(Collection<PropertyResolver> resolvers) {
        this.chain.addAll(Objects.requireNonNull(resolvers));
    }

    @Override
    public Property resolve(Member member) {
        return this.chain
                .stream()
                .map(resolver -> resolver.resolve(member))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    @Override
    public <T> Collection<Property> resolve(Class<T> clazz) {
        return this.chain
                .stream()
                .map(resolver -> resolver.resolve(clazz))
                .filter(e -> e != null)
                .reduce(new ArrayList<>(), (l, r) -> {
                    l.addAll(r);
                    return l;
                });
    }
}
