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

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

public class AccessorFactory {

    public static <V, T> Accessor<V, T> of(Member getMember, Member setMember) {
        final MethodHandles.Lookup lookup = MethodHandles.lookup();
        final MethodHandle getMethodHandle;
        final MethodHandle setMethodHandle;

        try {
            if (getMember instanceof Field) {
                getMethodHandle = lookup.unreflectGetter((Field) getMember);
            } else if (getMember instanceof Method) {
                getMethodHandle = lookup.unreflect((Method) getMember);
            } else {
                throw new IllegalArgumentException(getMember + " is not a field or method!");
            }

            if (getMember instanceof Field) {
                setMethodHandle = lookup.unreflectSetter((Field) setMember);
            } else if (getMember instanceof Method) {
                setMethodHandle = lookup.unreflect((Method) setMember);
            } else {
                throw new IllegalArgumentException(getMember + " is not a field or method!");
            }

            return new Accessor(getMethodHandle, setMethodHandle);
        } catch (IllegalAccessException e) {
            throw new AccessException("Can't create Accessor!", e);
        }
    }

    static class Accessor<V, T> implements io.github.mathter.smuzy.Accessor<V, T> {
        protected final MethodHandle handleGet;

        protected final MethodHandle handleSet;

        Accessor(MethodHandle handleGet, MethodHandle handleSet) {
            this.handleGet = handleGet;
            this.handleSet = handleSet;
        }

        @Override
        public V get(T object) {
            try {
                return (V) this.handleGet.bindTo(object).invoke();
            } catch (Throwable throwable) {
                throw new AccessException("Can't get var property!", throwable);
            }
        }

        @Override
        public V set(T object, V value) {
            try {
                return (V) this.handleSet.bindTo(object).invoke(value);
            } catch (Throwable throwable) {
                throw new AccessException("Can't set var property!", throwable);
            }
        }
    }
}
