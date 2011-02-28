/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on 2010-07-28
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.dev;

import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.pyx4j.gwt.server.RequestDebug;

/*
 <pre>
 <servlet>
 <servlet-name>DebugServlet</servlet-name>
 <servlet-class>com.pyx4j.essentials.server.dev.DebugServlet</servlet-class>
 </servlet>

 <servlet-mapping>
 <servlet-name>DebugServlet</servlet-name>
 <url-pattern>/debug/*</url-pattern>
 </servlet-mapping>
 </pre>
 */
@SuppressWarnings("serial")
public class DebugServlet extends GenericServlet {

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        if (req instanceof HttpServletRequest) {
            HttpServletRequest hrequest = (HttpServletRequest) req;
            if (hrequest.getParameter("error") != null) {
                throw new Error();
            }
        }
        RequestDebug.debug(req);
    }

}
