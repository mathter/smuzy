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
package com.github.mathter.smuzy.util;

import com.github.mathter.smuzy.ProcessingElementType;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class Util {

    public static <T> Class<T> checkComplexClass(Class<T> clazz) {
        if (clazz != null) {
            if (clazz.isInterface()) {
                throw new IllegalArgumentException(clazz + " is interface!");
            } else if (clazz.isPrimitive()) {
                throw new IllegalArgumentException(clazz + " is primitive!");
            } else if (clazz.isSynthetic()) {
                throw new IllegalArgumentException(clazz + " is enum!");
            }
        } else {
            throw new NullPointerException();
        }

        return clazz;
    }

    public static boolean filter(Member member) {
        final int modifiers = member.getModifiers();
        return Modifier.isPublic(modifiers)
                && (!(member instanceof Field) || !Modifier.isFinal(modifiers))
                && !Modifier.isStatic(modifiers);
    }

    public static Predicate<Member> filter(Set<ProcessingElementType> types) {
        return (m) -> (m instanceof Field && types.contains(ProcessingElementType.FIELD))
                || (m instanceof Method && types.contains(ProcessingElementType.METHOD));
    }

    public static Stream<? extends Member> getClassMembers(Class<?> clazz, ProcessingElementType[] order) {
        return Stream
                .of(order)
                .flatMap(e -> {
                    final Stream<? extends Member> result;

                    switch (e) {
                        case FIELD:
                            result = Util.getFields(clazz)
                                    .filter(Util::filter);
                            break;

                        case METHOD:
                            result = Util.getMethods(clazz)
                                    .filter(Util::filter);
                            break;

                        default:
                            throw new IllegalArgumentException(e + "is unknown!");
                    }

                    return result;
                });
    }

    private static Stream<? extends Member> getFields(Class<?> clazz) {
        return Optional.ofNullable(clazz.getSuperclass())
                .map(e -> Stream.concat(Stream.of(clazz.getDeclaredFields()), Util.getFields(e)))
                .orElse(Stream.empty());
    }

    private static Stream<? extends Member> getMethods(Class<?> clazz) {
        if (clazz.isInterface()) {
            // TODO
        }
        return Optional.ofNullable(clazz.getSuperclass())
                .map(e -> Stream.concat(Stream.of(clazz.getDeclaredMethods()), Util.getMethods(e)))
                .orElse(clazz.isInterface() ? Stream.of(clazz.getDeclaredMethods()) : Stream.empty());
    }

    private Util() {
    }
}
