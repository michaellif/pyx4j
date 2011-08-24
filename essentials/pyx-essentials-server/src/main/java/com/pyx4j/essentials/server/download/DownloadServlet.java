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
 * Created on May 8, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.download;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Consts;
import com.pyx4j.gwt.server.IOUtils;

@SuppressWarnings("serial")
public class DownloadServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(DownloadServlet.class);

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String fileName = Downloadable.getDownloadableFileName(request.getPathInfo());
        if (fileName == null) {
            throw new ServletException("Can't find documents name in request.");
        }

        Downloadable d = Downloadable.getDownloadable(fileName);
        if (d == null) {
            throw new ServletException("No document [" + fileName + "] found on the HTTP session.");
        }
        log.debug("download", fileName);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        long fileExpires = System.currentTimeMillis() + Consts.MIN2MSEC * 2;
        response.setDateHeader("Expires", fileExpires);
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");

        response.setContentType(d.getContentType());
        response.setContentLength(d.getData().length);

        OutputStream output = response.getOutputStream();
        try {
            output.write(d.getData());
            d.remove();
        } finally {
            IOUtils.closeQuietly(output);
        }
    }

}
