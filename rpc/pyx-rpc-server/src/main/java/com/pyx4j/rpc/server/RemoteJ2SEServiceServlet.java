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
 * Created on 2010-06-25
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.rpc.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.rpc.IServiceFactory;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.rpc.shared.RemoteService;

@SuppressWarnings("serial")
public class RemoteJ2SEServiceServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(RemoteServiceServlet.class);

    private RemoteService implementation;

    private static int requestCount = 0;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        IServiceFactory serviceFactory = null;
        if (config != null) {
            String serviceFactoryClassName = config.getInitParameter("serviceFactoryClass");
            if (CommonsStringUtils.isStringSet(serviceFactoryClassName)) {
                try {
                    serviceFactory = (IServiceFactory) Class.forName(serviceFactoryClassName).newInstance();
                } catch (Throwable e) {
                    log.error("J2SE ServiceFactory creation error", e);
                    throw new ServletException("J2SE ServiceFactory not avalable");
                }
            }
        }
        if (serviceFactory == null) {
            serviceFactory = ServerSideConfiguration.instance().getJ2SEServiceFactory();
        }
        if (serviceFactory == null) {
            serviceFactory = ServerSideConfiguration.instance().getRPCServiceFactory();
        }
        if (serviceFactory == null) {
            serviceFactory = new ReflectionServiceFactory();
        }
        implementation = new RemoteServiceImpl("J2SE", serviceFactory);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        ServletOutputStream os = null;
        ServletInputStream is = null;
        try {
            log.debug("request# {}", requestCount++);
            is = req.getInputStream();

            ObjectInputStream ois = new ObjectInputStream(is);
            String serviceDescriptor = ois.readUTF();
            Serializable serviceDO = (Serializable) ois.readObject();

            ois.close();
            is.close();
            is = null;
            Serializable reply = implementation.execute(serviceDescriptor, serviceDO, null);
            if (reply != null) {
                response.setContentType("application/binary");
                os = response.getOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(os);
                oos.writeObject(reply);
                oos.flush();
                oos.close();
                os.flush();
                os.close();
                os = null;
            } else {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
        } catch (Throwable e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.addHeader("message", e.getMessage());
            log.error("Fatal error", e);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<body>");
        out.println("<span>OK.</span>");

        out.println("<span>");
        out.println(requestCount);
        out.println("</span>");

        out.println("</body>");
        out.println("</html>");
    }

}
