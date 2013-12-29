/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 8, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.ci;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.gwt.server.IOUtils;

@SuppressWarnings("serial")
public class TestTimeoutServlet extends HttpServlet {

    private void o(OutputStream out, String... messages) throws IOException {
        out.write("<pre>".getBytes());
        for (String message : messages) {
            out.write(message.getBytes());
        }

        out.write("</pre>".getBytes());
        out.flush();
    }

    private void h(OutputStream out, String... messages) throws IOException {
        for (String message : messages) {
            out.write(message.getBytes());
        }
        out.flush();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setDateHeader("Expires", System.currentTimeMillis());
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-control", "no-cache, no-store, must-revalidate");
        response.setContentType("text/html");
        OutputStream out = response.getOutputStream();
        h(out, "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"></head><body>");

        long start = System.currentTimeMillis();

        int sec = 60 * 7;
        String secParm = request.getParameter("sec");
        if (CommonsStringUtils.isStringSet(secParm)) {
            sec = Integer.valueOf(secParm);
        }
        while (sec > 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }

            o(out, "time: ", TimeUtils.secSince(start));
            sec--;
        }
        h(out, "<p style=\"background-color:33FF33\">DONE</p>");
        h(out, "</body></html>");
        IOUtils.closeQuietly(out);
    }
}
