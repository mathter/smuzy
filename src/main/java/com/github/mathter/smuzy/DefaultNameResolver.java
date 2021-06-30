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

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

public class DefaultNameResolver implements NameResolver {

    private static final NameResolver INSTANCE = new DefaultNameResolver();

    public static final NameResolver.Factory Factory = new Factory();

    @Override
    public <T> String resolvePropertyName(Member member) {
        final String result;

        if (member instanceof Method) {
            result = this.resolvePropertyName((Method) member);
        } else if (member instanceof Field) {
            result = this.resolvePropertyName((Field) member);
        } else {
            result = null;
        }

        return result;
    }

    private <T> String resolvePropertyName(Method method) {
        final String result;
        final char[] propertyName;
        final char[] methodName = method.getName().toCharArray();
        final int methodPrameterCount = method.getParameterCount();

        if (methodPrameterCount == 0) {
            if (methodName[0] == 'g') {
                if (methodName[1] == 'e' && methodName[2] == 't') {
                    propertyName = copy(methodName, 3);
                } else {
                    propertyName = methodName;
                }
            } else if (methodName[0] == 'i') {
                if (methodName[1] == 's') {
                    propertyName = copy(methodName, 2);
                } else {
                    propertyName = methodName;
                }
            } else {
                propertyName = methodName;
            }
        } else if (methodPrameterCount == 1) {
            if (methodName[0] == 's' && methodName[1] == 'e' && methodName[2] == 't') {
                propertyName = copy(methodName, 3);
            } else {
                propertyName = methodName;
            }
        } else {
            propertyName = null;
        }

        if (propertyName != null) {
            propertyName[0] = Character.toLowerCase(propertyName[0]);
            result = new String(propertyName);
        } else {
            result = null;
        }

        return result;
    }

    private String resolvePropertyName(Field field) {
        return field.getName();
    }

    private char[] copy(char[] src, int from) {
        final char[] result;
        final int newLength = src.length - from;

        if (newLength > 0) {
            result = new char[newLength];
            System.arraycopy(src, from, result, 0, result.length);
        } else {
            result = null;
        }

        return result;
    }

    private static class Factory implements NameResolver.Factory {
        @Override
        public NameResolver get() {
            return INSTANCE;
        }
    }
}
