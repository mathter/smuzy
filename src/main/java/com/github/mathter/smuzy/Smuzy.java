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

import java.util.function.Function;

public interface Smuzy {

    default <T> T as(Class<T> interfaceType) {
        return this.as(interfaceType, c -> {
            try {
                return c.getConstructor().newInstance();
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("Can't create instance of class: " + c, e);
            }
        });
    }

    default <T> T as(Class<T> interfaceType, Function<Class<T>, T> instanceFactory) {
        return this.as(interfaceType, DefaultPropertyResolver.Factory.get(), instanceFactory);
    }

    default <T> T as(Class<T> interfaceType, PropertyResolver propertyResolver, Function<Class<T>, T> instanceFactory) {
        return this.as(interfaceType, propertyResolver, new DefaultAccessorResolver(propertyResolver), instanceFactory);
    }

    public <T> T as(Class<T> interfaceType, PropertyResolver propertyResolver, AccessorResolver accessorResolver, Function<Class<T>, T> instanceFactory);

    default <T> Smuzy of(T object) {
        return this.of(object, DefaultPropertyResolver.Factory.get());
    }

    default <T> Smuzy of(T object, PropertyResolver propertyResolver) {
        return this.of(object, propertyResolver, new DefaultAccessorResolver(propertyResolver));
    }

    public <T> Smuzy of(T object, PropertyResolver propertyResolver, AccessorResolver accessorResolver);
}
