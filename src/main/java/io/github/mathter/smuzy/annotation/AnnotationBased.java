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

import io.github.mathter.smuzy.ProcessingElementType;
import io.github.mathter.smuzy.SmuzyConstants;
import io.github.mathter.smuzy.util.Util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class AnnotationBased {

    private static final PropertyProcessing DEFAULT_PROPERTY_PROCESSING = new PropertyProcessing() {
        @Override
        public ProcessingElementType[] value() {
            return SmuzyConstants.DEFAULT_PROCESSING_TYPES;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return PropertyProcessing.class;
        }
    };

    protected PropertyProcessing getProcessingElementTypes(Class<?> clazz) {
        return Optional
                .ofNullable(clazz.getAnnotation(PropertyProcessing.class))
                .orElse(DEFAULT_PROPERTY_PROCESSING);
    }

    protected Stream<? extends Member> getClassMembers(Class<?> clazz) {
        return Util.getClassMembers(clazz, this.getProcessingElementTypes(clazz).value());
    }
}
