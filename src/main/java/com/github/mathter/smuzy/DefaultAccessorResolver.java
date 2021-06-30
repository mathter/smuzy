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

import com.github.mathter.smuzy.annotation.AnnotationPropertyResolver;
import com.github.mathter.smuzy.util.Util;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

public class DefaultAccessorResolver implements AccessorResolver {

    private static final AccessorResolver INSTANCE = new DefaultAccessorResolver();

    public static final AccessorResolverFactory Factory = new Factory();

    private final PropertyResolver propertyResolver;

    private final ProcessingElementType[] order;

    public DefaultAccessorResolver(PropertyResolver propertyResolver, ProcessingElementType[] order) {
        this.propertyResolver = propertyResolver;
        this.order = order;
    }

    public DefaultAccessorResolver(PropertyResolver propertyResolver) {
        this(propertyResolver, new ProcessingElementType[]{ProcessingElementType.FIELD, ProcessingElementType.METHOD});
    }

    public DefaultAccessorResolver() {
        this(new ChainPropertyResolver(AnnotationPropertyResolver.INSTANCE, DefaultPropertyResolver.Factory.get()));
    }

    @Override
    public <V, T> Accessor<V, T> resolve(Property property, Class<T> clazz) {
        final Accessor<V, T> result;
        final Pair<Member, Member> members = Util.getClassMembers(clazz, order)
                .map(e -> new Pair<Member, Property>(e, this.propertyResolver.resolve(e)))
                .filter(e -> e.right != null)
                .filter(e -> property.equals(e.right))
                .reduce(
                        new Pair<>(),
                        (l, r) -> {
                            l.left = isGetter(r.left) ? r.left : l.left;
                            l.right = isSetter(r.left) ? r.left : l.right;
                            return l;
                        },
                        (l, r) -> l != null ? l : r
                );

        if (members.left == null || members.right == null) {
            result = null;
        } else {
            result = AccessorFactory.of(members.left, members.right);
        }

        return result;
    }

    private static boolean isGetter(Member member) {
        return member instanceof Field || (member instanceof Method && ((Method) member).getParameterCount() == 0);
    }

    private static boolean isSetter(Member member) {
        return member instanceof Field || (member instanceof Method && ((Method) member).getParameterCount() == 1);
    }

    private static class Pair<L, R> {
        private L left;

        private R right;

        public Pair() {
        }

        public Pair(L left, R right) {
            this.left = left;
            this.right = right;
        }

    }

    public static final class Factory implements AccessorResolverFactory {
        @Override
        public AccessorResolver get() {
            return INSTANCE;
        }
    }
}
