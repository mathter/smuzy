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
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface MemberResolver {

    public MemberResolveResult resolve(Class<?> clazz, String name);

    public MemberResolveResult resolve(Class<?> clazz, Member member);

    public interface Factory extends Supplier<MemberResolver> {
    }

    public static class MemberResolveResult {

        private Field field;

        private Method getMethod;

        private Method setMethod;

        public Field getField() {
            return field;
        }

        public Method getGetMethod() {
            return getMethod;
        }

        public Method getSetMethod() {
            return setMethod;
        }

        public Stream<Member> stream() {
            return Stream.of(this.field, this.getMethod, this.setMethod);
        }

        public Stream<Member> perfectStream() {
            return this.isPerfect() ? this.stream() : Stream.empty();
        }

        protected void setField(Field field) {
            this.field = field;
        }

        protected void setGetMethod(Method getMethod) {
            this.getMethod = getMethod;
        }

        protected void setSetMethod(Method setMethod) {
            this.setMethod = setMethod;
        }

        protected Member getFirstNotNull() {
            final Member result;
            if (this.field != null) {
                result = this.field;
            } else {
                if (this.getMethod != null) {
                    result = this.getMethod;
                } else {
                    result = this.setMethod;
                }
            }

            return result;
        }

        public boolean isPerfect() {
            return this.field != null || (this.getMethod != null && this.setMethod != null);
        }
    }
}
