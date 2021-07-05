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

import io.github.mathter.smuzy.util.Util;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.stream.Collectors;

public class DefaultPropertyResolver implements PropertyResolver {

    private static final PropertyResolver INSTANCE = new DefaultPropertyResolver();

    public static final PropertyResolver.Factory Factory = new Factory();

    private final NameResolver nameResolver;

    private final MemberResolver memberResolver;

    public DefaultPropertyResolver() {
        this(new DefaultNameResolver());
    }

    public DefaultPropertyResolver(NameResolver nameResolver) {
        this(nameResolver, new DefaultMemberResolver(nameResolver));
    }

    public DefaultPropertyResolver(NameResolver nameResolver, MemberResolver memberResolver) {
        this.nameResolver = nameResolver;
        this.memberResolver = memberResolver;
    }

    @Override
    public Property resolve(Member member) {
        final Property property;

        if (member instanceof Field) {
            property = this.resolve((Field) member);
        } else if (member instanceof Method) {
            property = this.resolve((Method) member);
        } else {
            throw new IllegalArgumentException(member + " is not a field of setter or getter method!");
        }

        return property;
    }

    private Property resolve(Method method) {
        return new BeanProperty(String.valueOf(this.nameResolver.resolvePropertyName(method)));
    }

    private Property resolve(Field field) {
        return new BeanProperty(String.valueOf(this.nameResolver.resolvePropertyName(field)));
    }

    @Override
    public <T> Collection<Property> resolve(Class<T> clazz) {
        return Util.getClassMembers(clazz, SmuzyConstants.DEFAULT_PROCESSING_TYPES)
                .map(e -> this.memberResolver.resolve(clazz, e))
                .filter(e -> e.isPerfect())
                .map(e -> e.getFirstNotNull())
                .map(e -> this.nameResolver.resolvePropertyName(e))
                .distinct()
                .sorted()
                .map(BeanProperty::new)
                .collect(Collectors.toList());
    }

    private static final class Factory implements PropertyResolver.Factory {
        @Override
        public PropertyResolver get() {
            return INSTANCE;
        }
    }
}
