/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Jul 31, 2014
 * @author vlads
 */
package com.pyx4j.config.server;

import junit.framework.TestCase;

import org.junit.Assert;

import com.pyx4j.config.server.ut.UnderTestFacade1;
import com.pyx4j.config.server.ut.UnderTestFacade2;
import com.pyx4j.config.server.ut.UnderTestFacade2Factory;

public class ServerSideFactoryTest extends TestCase {

    public void testFacadeExceptions() {
        Assert.assertEquals("256", ServerSideFactory.create(UnderTestFacade1.class).echo("256"));

        Assert.assertEquals("256", ServerSideFactory.create(UnderTestFacade1.class).echoOrThrow("256", null));

        try {
            ServerSideFactory.createDefaultImplementation(UnderTestFacade1.class, true).echoOrThrow("257", ArithmeticException.class);
            Assert.fail("Exception expected");
        } catch (ArithmeticException ok) {
            Assert.assertEquals("257", ok.getMessage());
        } catch (Throwable e) {
            throw new AssertionError(e);
        }

        // On interface
        try {
            ServerSideFactory.createDefaultImplementation(UnderTestFacade1.class, true).echoOrThrow("257", IllegalMonitorStateException.class);
            Assert.fail("Exception expected");
        } catch (UnsupportedOperationException ok) {
            Assert.assertEquals("257", ok.getMessage());
        } catch (Throwable e) {
            throw new AssertionError(e);
        }

        // On class
        try {
            ServerSideFactory.createDefaultImplementation(UnderTestFacade1.class, true).echoOrThrowRedefinedOnClass("258", IllegalMonitorStateException.class);
            Assert.fail("Exception expected");
        } catch (ArrayStoreException ok) {
            Assert.assertEquals("258", ok.getMessage());
        } catch (Throwable e) {
            throw new AssertionError(e);
        }

        // On class again, debug InterceptorsCache
        try {
            ServerSideFactory.createDefaultImplementation(UnderTestFacade1.class, true).echoOrThrowRedefinedOnClass("258", IllegalMonitorStateException.class);
            Assert.fail("Exception expected");
        } catch (ArrayStoreException ok) {
            Assert.assertEquals("258", ok.getMessage());
        } catch (Throwable e) {
            throw new AssertionError(e);
        }
    }

    public void testFacadeFactoryExceptions() {
        // On interface
        UnderTestFacade2Factory.implVersion = 1;
        try {
            ServerSideFactory.createDefaultImplementation(UnderTestFacade2.class, true).echoOrThrowRedefinedOnClass("257", IllegalMonitorStateException.class);
            Assert.fail("Exception expected");
        } catch (UnsupportedOperationException ok) {
            Assert.assertEquals("257-.1", ok.getMessage());
        } catch (Throwable e) {
            throw new AssertionError(e);
        }

        // On class
        UnderTestFacade2Factory.implVersion = 2;
        try {
            ServerSideFactory.createDefaultImplementation(UnderTestFacade2.class, true).echoOrThrowRedefinedOnClass("258", IllegalMonitorStateException.class);
            Assert.fail("Exception expected");
        } catch (ArrayStoreException ok) {
            Assert.assertEquals("258-.2", ok.getMessage());
        } catch (Throwable e) {
            throw new AssertionError(e);
        }

        // On class again, debug InterceptorsCache
        UnderTestFacade2Factory.implVersion = 2;
        try {
            ServerSideFactory.createDefaultImplementation(UnderTestFacade2.class, true).echoOrThrowRedefinedOnClass("259", IllegalMonitorStateException.class);
            Assert.fail("Exception expected");
        } catch (ArrayStoreException ok) {
            Assert.assertEquals("259-.2", ok.getMessage());
        } catch (Throwable e) {
            throw new AssertionError(e);
        }

        // On interface
        UnderTestFacade2Factory.implVersion = 1;
        try {
            ServerSideFactory.createDefaultImplementation(UnderTestFacade2.class, true).echoOrThrowRedefinedOnClass("260", IllegalMonitorStateException.class);
            Assert.fail("Exception expected");
        } catch (UnsupportedOperationException ok) {
            Assert.assertEquals("260-.1", ok.getMessage());
        } catch (Throwable e) {
            throw new AssertionError(e);
        }
    }
}
