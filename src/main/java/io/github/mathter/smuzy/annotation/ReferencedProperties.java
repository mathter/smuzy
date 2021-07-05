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

import io.github.mathter.smuzy.DefaultMemberResolver;
import io.github.mathter.smuzy.MemberResolver;
import io.github.mathter.smuzy.SmuzyConstants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface ReferencedProperties {
    public Class<?> referencedClass();

    public Class<? extends MemberResolver.Factory> memberResolverFactory() default DefaultMemberResolver.Factory.class;

    public boolean annotaionFirst() default true;

    public PropertyType propertyType() default PropertyType.ANNOTATION;

    public Property[] properties();

    public @interface Property {
        public String value() default SmuzyConstants.NONE;

        public String beanProp();
    }

    public enum PropertyType {
        ANNOTATION,
        BEAN
    }
}
