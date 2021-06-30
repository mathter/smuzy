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

import com.github.mathter.smuzy.util.Util;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Optional;

public class DefaultMemberResolver implements MemberResolver {

    private static final MemberResolver INSTANCE = new DefaultMemberResolver();

    public static final MemberResolver.Factory Factory = new Factory();

    private final NameResolver nameResolver;

    public DefaultMemberResolver(NameResolver nameResolver) {
        this.nameResolver = nameResolver;
    }

    public DefaultMemberResolver() {
        this(DefaultNameResolver.Factory.get());
    }

    @Override
    public MemberResolveResult resolve(Class<?> clazz, String name) {
        return Util.getClassMembers(clazz, SmuzyConstants.DEFAULT_PROCESSING_TYPES)
                .filter(e -> name.equals(this.nameResolver.resolvePropertyName(e)))
                .sequential()
                .reduce(
                        new MemberResolveResult(),
                        (l, r) -> {
                            if (r instanceof Field) {
                                l.setField((Field) r);
                            } else if (r instanceof Method) {
                                if (((Method) r).getParameterCount() == 0) {
                                    l.setGetMethod((Method) r);
                                } else if (((Method) r).getParameterCount() == 1) {
                                    l.setSetMethod((Method) r);
                                } else {
                                    throw new IllegalStateException(r + " is not setter or getter method!");
                                }
                            } else {
                                throw new IllegalStateException(r + " is unknown type! Must be java.lang.reflect.Field or java.lang.reflect.Method!");
                            }

                            return l;
                        },
                        (l, r) -> l != null ? l : r
                );
    }

    @Override
    public MemberResolveResult resolve(Class<?> clazz, Member member) {
        return Util.getClassMembers(clazz, SmuzyConstants.DEFAULT_PROCESSING_TYPES)
                .filter(e -> Optional.ofNullable(this.nameResolver.resolvePropertyName(member))
                        .map(ee -> ee.equals(this.nameResolver.resolvePropertyName(e)))
                        .orElse(false)
                )
                .sequential()
                .reduce(
                        new MemberResolveResult(),
                        (l, r) -> {
                            if (r instanceof Field) {
                                l.setField((Field) r);
                            } else if (r instanceof Method) {
                                if (((Method) r).getParameterCount() == 0) {
                                    l.setGetMethod((Method) r);
                                } else if (((Method) r).getParameterCount() == 1) {
                                    l.setSetMethod((Method) r);
                                } else {
                                    throw new IllegalStateException(r + " is not setter or getter method!");
                                }
                            } else {
                                throw new IllegalStateException(r + " is unknown type! Must be java.lang.reflect.Field or java.lang.reflect.Method!");
                            }

                            return l;
                        },
                        (l, r) -> l != null ? l : r
                );
    }

    public static class Factory implements MemberResolver.Factory {
        @Override
        public MemberResolver get() {
            return INSTANCE;
        }
    }
}
