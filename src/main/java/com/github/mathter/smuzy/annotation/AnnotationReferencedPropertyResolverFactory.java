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
package com.github.mathter.smuzy.annotation;

import com.github.mathter.smuzy.AnnotationProperty;
import com.github.mathter.smuzy.BeanProperty;
import com.github.mathter.smuzy.ChainPropertyResolver;
import com.github.mathter.smuzy.MemberResolver;
import com.github.mathter.smuzy.Property;
import com.github.mathter.smuzy.PropertyResolver;
import com.github.mathter.smuzy.PropertyResolver.Factory;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnnotationReferencedPropertyResolverFactory implements Factory {

    private final Collection<Factory> factories;

    public AnnotationReferencedPropertyResolverFactory(AnnotatedElement object) {
        this.factories = Stream.of(object)
                .flatMap(e -> {
                    Stream<AnnotatedElement> result;

                    if (e instanceof Class) {
                        result = Stream.of(e);
                    } else if (e instanceof Method) {
                        result = Stream.of(e);
                    } else {
                        throw new IllegalArgumentException(object + " must be instance of " + Class.class + " or " + Method.class);
                    }

                    return result;
                })
                .flatMap(e -> {
                    final Stream<Factory> result;
                    final boolean annotaionFirst = Optional.ofNullable(e.getAnnotation(ReferencedProperties.class))
                            .map(a -> a.annotaionFirst())
                            .orElse(true);
                    final Factory annotationBasedFactory = () -> AnnotationReferencedPropertyResolverFactory.referenced(e);

                    if (e instanceof Class) {
                        if (annotaionFirst) {
                            result = Stream.of(annotationBasedFactory, AnnotationReferencedPropertyResolverFactory.classBasedCreatingMethod((Class) e));
                        } else {
                            result = Stream.of(AnnotationReferencedPropertyResolverFactory.classBasedCreatingMethod((Class) e), annotationBasedFactory);
                        }
                    } else if (e instanceof Method) {
                        if (annotaionFirst) {
                            result = Stream.of(annotationBasedFactory, AnnotationReferencedPropertyResolverFactory.classMethodCreatingMethod((Method) e));
                        } else {
                            result = Stream.of(AnnotationReferencedPropertyResolverFactory.classMethodCreatingMethod((Method) e), annotationBasedFactory);
                        }
                    } else {
                        throw new IllegalArgumentException(object + " must be instance of " + Class.class + " or " + Method.class);
                    }

                    return result;
                })
                .collect(Collectors.toList());
    }

    @Override
    public PropertyResolver get() {
        return new ChainPropertyResolver(this.factories.stream().map(Factory::get).collect(Collectors.toList()));
    }

    private static <T> Factory classMethodCreatingMethod(Method method) {
        final Factory result;
        final Class<T> clazz = (Class<T>) method.getReturnType();

        if (Factory.class.isAssignableFrom(clazz)) {
            try {
                result = ((Factory) clazz.getConstructor().newInstance());
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException("Can't build PropertyResolver! There is no default constructor for " + clazz + "!", e);
            } catch (Exception e) {
                throw new IllegalStateException("Can't build PropertyResolver!", e);
            }
        } else if (PropertyResolver.class.isAssignableFrom(clazz)) {

            result = () -> {
                try {
                    return (PropertyResolver) clazz.getDeclaredConstructor().newInstance();
                } catch (NoSuchMethodException e) {
                    throw new IllegalStateException("Can't build PropertyResolver! There is no default constructor for " + clazz + "!", e);
                } catch (Exception e) {
                    throw new IllegalStateException("Can't build PropertyResolver!", e);
                }
            };
        } else {
            result = null;
        }

        return result;
    }

    private static <T> Factory classBasedCreatingMethod(Class<T> clazz) {
        final Factory result;

        if (Factory.class.isAssignableFrom(clazz)) {
            try {
                result = (Factory) clazz.getConstructor().newInstance();
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException("Can't build PropertyResolver! There is no default constructor for " + clazz + "!", e);
            } catch (Exception e) {
                throw new IllegalStateException("Can't build PropertyResolver!", e);
            }
        } else if (PropertyResolver.class.isAssignableFrom(clazz)) {
            result = () -> {
                try {
                    return (PropertyResolver) clazz.getDeclaredConstructor().newInstance();
                } catch (NoSuchMethodException e) {
                    throw new IllegalStateException("Can't build PropertyResolver! There is no default constructor for " + clazz + "!", e);
                } catch (Exception e) {
                    throw new IllegalStateException("Can't build PropertyResolver!", e);
                }
            };
        } else {
            result = null;
        }

        return result;
    }

    private static AnnotationReferencedPropertyResolver referenced(AnnotatedElement object) {
        return Optional.ofNullable(object.getAnnotation(ReferencedProperties.class))
                .map(rps -> {
                    final MemberResolver.Factory memberResolverFactory;
                    final ReferencedProperties.PropertyType propertyType = rps.propertyType();

                    try {
                        memberResolverFactory = (MemberResolver.Factory) rps.memberResolverFactory().getDeclaredConstructor().newInstance();
                    } catch (NoSuchMethodException e) {
                        throw new IllegalStateException("Can't build PropertyResolver!" + rps.referencedClass() + " has no default constructor!", e);
                    } catch (Exception e) {
                        throw new IllegalStateException("Can't build PropertyResolver!", e);
                    }

                    return new AnnotationReferencedPropertyResolver(
                            rps.referencedClass(),
                            Stream.of(rps.properties())
                                    .flatMap(p ->
                                            memberResolverFactory.get().resolve(rps.referencedClass(), p.beanProp())
                                                    .perfectStream()
                                                    .map(m -> Pair.of(m, AnnotationReferencedPropertyResolverFactory.build(p.value(), propertyType)))
                                    )
                                    .filter(e -> e.getLeft() != null)
                                    .collect(Collectors.toMap(p -> p.getLeft(), p -> p.getRight()))
                    );
                })
                .get();
    }

    private static Property build(String value, ReferencedProperties.PropertyType propertyType) {
        final Property property;

        switch (propertyType) {
            case ANNOTATION:
                property = new AnnotationProperty(value);
                break;

            case BEAN:
                property = new BeanProperty(value);
                break;

            default:
                throw new IllegalArgumentException(propertyType + " is undefined!");
        }

        return property;
    }

    private static class AnnotationReferencedPropertyResolver<T> implements PropertyResolver {

        private final Class<T> referencedClass;

        private final Map<Member, com.github.mathter.smuzy.Property> memberPropertyMap;

        AnnotationReferencedPropertyResolver(Class<T> referencedClass, Map<Member, com.github.mathter.smuzy.Property> memberPropertyMap) {
            this.referencedClass = referencedClass;
            this.memberPropertyMap = memberPropertyMap;
        }

        @Override
        public com.github.mathter.smuzy.Property resolve(Member member) {
            final com.github.mathter.smuzy.Property result;

            if (this.referencedClass.equals(member.getDeclaringClass())) {
                result = this.memberPropertyMap.get(member);
            } else {
                result = null;
            }

            return result;
        }

        @Override
        public <T> Collection<com.github.mathter.smuzy.Property> resolve(Class<T> clazz) {
            final Collection<Property> result;

            if (this.referencedClass.equals(clazz)) {
                result = this.memberPropertyMap.values().stream().distinct().collect(Collectors.toList());
            } else {
                result = Collections.emptyList();
            }

            return result;
        }
    }
}
