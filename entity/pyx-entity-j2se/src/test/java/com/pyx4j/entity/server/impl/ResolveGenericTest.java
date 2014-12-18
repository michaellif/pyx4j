/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Feb 16, 2012
 * @author vlads
 */
package com.pyx4j.entity.server.impl;

import java.lang.reflect.Method;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Assert;

public class ResolveGenericTest extends TestCase {

    private static interface A<E, T> {

        E getAE();

        T getAT();
    }

    private static interface C<E> {
        E getCE();
    }

    private static interface B extends A<Boolean, Long>, C<Integer> {
    }

    public void testMethodReturnType() throws NoSuchMethodException, SecurityException {
        Class<?> interfaceClass = B.class;
        Method method = interfaceClass.getMethod("getAE", (Class[]) null);
        Assert.assertEquals(Boolean.class, EntityImplReflectionHelper.resolveGenericType(method.getGenericReturnType(), interfaceClass));

        method = interfaceClass.getMethod("getAT", (Class[]) null);
        Assert.assertEquals(Long.class, EntityImplReflectionHelper.resolveGenericType(method.getGenericReturnType(), interfaceClass));

        method = interfaceClass.getMethod("getCE", (Class[]) null);
        Assert.assertEquals(Integer.class, EntityImplReflectionHelper.resolveGenericType(method.getGenericReturnType(), interfaceClass));
    }

    private static interface GN<E extends Number> {
        E getNumber();
    }

    public void testGenericTypeMethodReturnType() throws NoSuchMethodException, SecurityException {
        Class<?> interfaceClass = GN.class;

        Method method = interfaceClass.getMethod("getNumber", (Class[]) null);
        Assert.assertEquals(Number.class, EntityImplReflectionHelper.resolveGenericType(method.getGenericReturnType(), interfaceClass));
    }

    private static interface D1 extends C<Integer> {

    }

    private static interface D2 extends D1 {
    }

    public void testInheritanceMethodReturnType() throws NoSuchMethodException, SecurityException {
        Class<?> interfaceClass = D2.class;
        Method method = interfaceClass.getMethod("getCE", (Class[]) null);
        Assert.assertEquals(Integer.class, EntityImplReflectionHelper.resolveGenericType(method.getGenericReturnType(), interfaceClass));
    }

    private static interface S1<EofS1 extends Number> {

        EofS1 getS1();

        List<EofS1> getListS1();

    }

    private static interface S2<EofS2 extends Number> extends S1<EofS2> {

    }

    private static interface S3 extends S2<Integer> {

    }

    public void testInheritance2LevelMethodUnresolvedReturnType() throws NoSuchMethodException, SecurityException {
        Class<?> interfaceClass = S2.class;
        Method method = interfaceClass.getMethod("getS1", (Class[]) null);
        Assert.assertEquals(Number.class, EntityImplReflectionHelper.resolveGenericType(method.getGenericReturnType(), interfaceClass));

        Method collectionMethod = interfaceClass.getMethod("getListS1", (Class[]) null);
        Assert.assertEquals(Number.class, EntityImplReflectionHelper.resolveTypeGenericArgumentType(collectionMethod.getGenericReturnType(), interfaceClass));
    }

    public void testInheritance2LevelMethodReturnType() throws NoSuchMethodException, SecurityException {
        Class<?> interfaceClass = S3.class;
        Method method = interfaceClass.getMethod("getS1", (Class[]) null);
        Assert.assertEquals(Integer.class, EntityImplReflectionHelper.resolveGenericType(method.getGenericReturnType(), interfaceClass));

        Method collectionMethod = interfaceClass.getMethod("getListS1", (Class[]) null);
        Assert.assertEquals(Integer.class, EntityImplReflectionHelper.resolveTypeGenericArgumentType(collectionMethod.getGenericReturnType(), interfaceClass));
    }

    private static interface HasUnknownWildcard {

        List<C<?>> getListC();

    }

    public void testUnknownWildcard() throws NoSuchMethodException, SecurityException {
        Class<?> interfaceClass = HasUnknownWildcard.class;

        Method collectionMethod = interfaceClass.getMethod("getListC", (Class[]) null);
        Assert.assertEquals(C.class, EntityImplReflectionHelper.resolveTypeGenericArgumentType(collectionMethod.getGenericReturnType(), interfaceClass));
    }
}
