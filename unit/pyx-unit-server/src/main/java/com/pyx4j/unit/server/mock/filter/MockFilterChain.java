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
 * Created on Oct 6, 2014
 * @author ernestog
 */
package com.pyx4j.unit.server.mock.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.junit.Assert;

public class MockFilterChain implements FilterChain {

    private boolean shouldBeInvoked;

    private boolean wasInvoked;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res) throws IOException, ServletException {
        this.wasInvoked = true;
    }

    public void setExpectedInvocation(boolean shouldBeInvoked) {
        this.shouldBeInvoked = shouldBeInvoked;
    }

    public boolean wasInvoked() {
        return this.wasInvoked;
    }

    public void verify() {
        if (this.shouldBeInvoked) {
            Assert.assertTrue("MockFilterChain should be invoked", this.wasInvoked);
        } else {
            Assert.assertTrue("MockFilterChain should not be invoked", !this.wasInvoked);
        }
    }

}
