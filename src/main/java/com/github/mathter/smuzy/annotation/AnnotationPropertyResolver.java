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
package com.github.mathter.smuzy.annotation;

import com.github.mathter.smuzy.AnnotationProperty;
import com.github.mathter.smuzy.DefaultMemberResolver;
import com.github.mathter.smuzy.DefaultNameResolver;
import com.github.mathter.smuzy.MemberResolver;
import com.github.mathter.smuzy.NameResolver;
import com.github.mathter.smuzy.Property;
import com.github.mathter.smuzy.PropertyResolver;

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
    public Property resolve(Member member) {
        final Class<?> clazz = member.getDeclaringClass();

        return Optional
                .ofNullable(((AccessibleObject) member).getAnnotation(com.github.mathter.smuzy.annotation.Property.class))
                .map(a -> new AnnotationProperty(a.value()))
                .orElseGet(() -> this.memberResolver.resolve(clazz, this.nameResolver.resolvePropertyName(member))
                        .stream()
                        .filter(e -> e != null)
                        .filter(e -> ((AccessibleObject) e).isAnnotationPresent(com.github.mathter.smuzy.annotation.Property.class))
                        .map(e -> ((AccessibleObject) e).getAnnotation(com.github.mathter.smuzy.annotation.Property.class))
                        .map(e -> new AnnotationProperty(e.value()))
                        .findFirst()
                        .orElse(null)
                );
    }

    @Override
    public <T> Collection<Property> resolve(Class<T> clazz) {
        return this.getClassMembers(clazz)
                .map(e -> ((AccessibleObject) e).getAnnotation(com.github.mathter.smuzy.annotation.Property.class))
                .filter(Objects::nonNull)
                .map(e -> e.value())
                .distinct()
                .map(AnnotationProperty::new)
                .collect(Collectors.toList());
    }
}
