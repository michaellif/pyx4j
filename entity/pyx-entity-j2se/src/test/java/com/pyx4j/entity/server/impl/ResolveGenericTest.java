package com.pyx4j.entity.server.impl;

import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.junit.Assert;

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
 * @version $Id$
 */

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

    private static interface D1 extends C<Integer> {

    }

    private static interface D2 extends D1 {
    }

    public void testInheritanceMethodReturnType() throws NoSuchMethodException, SecurityException {
        Class<?> interfaceClass = D2.class;
        Method method = interfaceClass.getMethod("getCE", (Class[]) null);
        Assert.assertEquals(Integer.class, EntityImplReflectionHelper.resolveGenericType(method.getGenericReturnType(), interfaceClass));
    }
}
