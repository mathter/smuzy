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
package io.github.mathter.smuzy.annotation;

import io.github.mathter.smuzy.AnnotationProperty;
import io.github.mathter.smuzy.DefaultMemberResolver;
import io.github.mathter.smuzy.DefaultNameResolver;
import io.github.mathter.smuzy.MemberResolver;
import io.github.mathter.smuzy.NameResolver;
import io.github.mathter.smuzy.Property;
import io.github.mathter.smuzy.PropertyResolver;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class AnnotationPropertyResolver extends AnnotationBased implements PropertyResolver {

    public static final PropertyResolver INSTANCE = new AnnotationPropertyResolver();

    private final NameResolver nameResolver;

    private final MemberResolver memberResolver;

    public AnnotationPropertyResolver(NameResolver nameResolver, MemberResolver memberResolver) {
        this.nameResolver = nameResolver;
        this.memberResolver = memberResolver;
    }

    public AnnotationPropertyResolver(NameResolver nameResolver) {
        this(nameResolver, new DefaultMemberResolver(nameResolver));
    }

    public AnnotationPropertyResolver() {
        this(DefaultNameResolver.Factory.get());
    }

    @Override
    public io.github.mathter.smuzy.Property resolve(Member member) {
        final Class<?> clazz = member.getDeclaringClass();

        return Optional
                .ofNullable(((AccessibleObject) member).getAnnotation(io.github.mathter.smuzy.annotation.Property.class))
                .map(a -> new AnnotationProperty(a.value()))
                .orElseGet(() -> this.memberResolver.resolve(clazz, this.nameResolver.resolvePropertyName(member))
                        .stream()
                        .filter(e -> e != null)
                        .filter(e -> ((AccessibleObject) e).isAnnotationPresent(io.github.mathter.smuzy.annotation.Property.class))
                        .map(e -> ((AccessibleObject) e).getAnnotation(io.github.mathter.smuzy.annotation.Property.class))
                        .map(e -> new AnnotationProperty(e.value()))
                        .findFirst()
                        .orElse(null)
                );
    }

    @Override
    public <T> Collection<Property> resolve(Class<T> clazz) {
        return this.getClassMembers(clazz)
                .map(e -> ((AccessibleObject) e).getAnnotation(io.github.mathter.smuzy.annotation.Property.class))
                .filter(Objects::nonNull)
                .map(e -> e.value())
                .distinct()
                .map(AnnotationProperty::new)
                .collect(Collectors.toList());
    }
}
