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
 * Created on 2011-03-30
 * @author vlads
 */
package com.pyx4j.unit.server.mock.filter;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.pyx4j.unit.server.mock.MockHttpServletRequest;

public class MockHttpServletRequestFilter extends MockHttpServletRequest {

    protected String _forwardUrl;

    protected String _includeUrl;

    public MockHttpServletRequestFilter(String url) {
        super(url);
    }

    @Override
    public RequestDispatcher getRequestDispatcher(final String path) {

        return new RequestDispatcher() {

            @Override
            public void include(ServletRequest request, ServletResponse response) throws ServletException, IOException {
                _includeUrl = path;
            }

            @Override
            public void forward(ServletRequest request, ServletResponse response) throws ServletException, IOException {
                _forwardUrl = path;
            }
        };
    }

    public String getForwardUrl() {
        return _forwardUrl;
    }

    public String getIncludeUrl() {
        return _includeUrl;
    }
}
