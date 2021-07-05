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

import io.github.mathter.smuzy.util.Util;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

class MapSmuzy implements Smuzy, Serializable {
    private Map<Property, Object> fields = new HashMap<>();

    private final transient AccessResolver thisAccessResolver = new AccessResolver();

    MapSmuzy() {
    }

    @Override
    public <T> T as(Class<T> interfaceType, PropertyResolver propertyResolver, AccessorResolver accessorResolver, Function<Class<T>, T> instanceFactory) {
        final T result;

        if (interfaceType.isInterface()) {
            result = (T) Proxy.newProxyInstance(
                    ClassLoader.getSystemClassLoader(), new Class[]{Objects.requireNonNull(interfaceType)}, new InvocationHandler(propertyResolver)
            );
        } else {
            result = propertyResolver.resolve(Util.checkComplexClass(interfaceType))
                    .stream()
                    .map(e -> Pair.of(e, accessorResolver.resolve(e, interfaceType)))
                    .sequential()
                    .reduce(
                            instanceFactory.apply(interfaceType),
                            (instance, p) -> {
                                p.getRight().set(instance, this.thisAccessResolver.resolve(p.getLeft(), this).get(this));

                                return instance;
                            },
                            (l, r) -> l != null ? l : r
                    );
        }

        return result;
    }

    @Override
    public <T> Smuzy of(T object, PropertyResolver propertyResolver, AccessorResolver accessorResolver) {
        propertyResolver.resolve(Util.checkComplexClass(object.getClass()))
                .forEach(e -> this.fields.put(e, accessorResolver.resolve(e, object).get(object)));

        return this;
    }

    private class InvocationHandler<V, T> implements java.lang.reflect.InvocationHandler {

        private final PropertyResolver propertyResolver;

        public InvocationHandler(PropertyResolver propertyResolver) {
            this.propertyResolver = propertyResolver;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            final V result;
            final Property property = this.propertyResolver.resolve(method);
            final Accessor<V, T> accessor = (Accessor<V, T>) MapSmuzy.this.thisAccessResolver.resolve(property, MapSmuzy.this);

            if (method.getParameterCount() == 0) {
                result = accessor.get((T) MapSmuzy.this);
            } else {
                result = accessor.set((T) MapSmuzy.this, (V) args[0]);
            }

            return result;
        }
    }

    public static class AccessResolver implements AccessorResolver {
        @Override
        public <V, T> Accessor<V, T> resolve(final Property property, final Class<T> clazz) {
            final Accessor<V, T> result;

            if (MapSmuzy.class.isAssignableFrom(clazz)) {
                result = (Accessor<V, T>) new Accessor<V, MapSmuzy>() {
                    @Override
                    public V get(MapSmuzy object) {
                        return (V) object.fields.get(property);
                    }

                    @Override
                    public V set(MapSmuzy object, V value) {
                        return (V) object.fields.put(property, value);
                    }
                };
            } else {
                result = null;
            }

            return result;
        }
    }
}
