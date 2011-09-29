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
 * Created on 8-Apr-08
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.download;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Consts;
import com.pyx4j.log4j.LoggerConfig;
import com.pyx4j.server.contexts.Context;

@SuppressWarnings("serial")
public class LogViewServlet extends HttpServlet {

    protected File rootDirectory;

    protected boolean attachment = false;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        File containerHome = new File(LoggerConfig.getContainerHome());
        String root = config.getInitParameter("root");
        if (!CommonsStringUtils.isStringSet(root)) {
            File logsDirectory = new File(containerHome, "logs");
            if (LoggerConfig.getContextName() != null) {
                rootDirectory = new File(logsDirectory, LoggerConfig.getContextName());
            } else {
                rootDirectory = logsDirectory;
            }
        } else {
            this.rootDirectory = new File(containerHome, root);
        }
        attachment = "true".equalsIgnoreCase(config.getInitParameter("attachment"));
    }

    private void verifyPath(String path) throws ServletException {
        File f = new File(rootDirectory, path);
        if (!f.getAbsolutePath().startsWith(rootDirectory.getAbsolutePath())) {
            throw new ServletException("Permission denied");
        }
    }

    protected void authentication(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if ((Context.getVisit() == null) || (!Context.getVisit().isUserLoggedIn())) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new ServletException("Request requires authentication.");
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        authentication(request, response);

        // Set to expire soon.
        long fileExpires = System.currentTimeMillis() + Consts.MIN2MSEC * 2;
        response.setDateHeader("Expires", fileExpires);
        // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");

        String path = request.getPathInfo();
        //System.out.println("PathInfo:"  + path);
        //System.out.println("ContextPath:" + request.getContextPath());
        //System.out.println("ServletPath:" + request.getServletPath());

        String urlPrefix = "";

        if (path == null) {
            path = "/";
            urlPrefix = request.getServletPath().substring(1) + "/";
        } else {
            // URL decode on Tomcat is not working properly
            path = path.replace("%20", " ");
        }
        verifyPath(path);

        if (path.endsWith("/")) {
            listDirectory(path, urlPrefix, response);
        } else {
            sendFile(path, response);
        }
    }

    private void listDirectory(String path, String urlPrefix, HttpServletResponse response) throws ServletException, IOException {
        File dir = new File(rootDirectory, path);
        if (!dir.isDirectory()) {
            throw new ServletException("No such directory: " + dir);
        }
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><head><title>LogView</title>");
        out.println("<META HTTP-EQUIV=\"PRAGMA\" CONTENT=\"NO-CACHE\">");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1> Index of ");
        out.println(dir.getAbsolutePath());
        out.println("</h1>");

        out.println("<table>");

        out.println("<tr><th align=\"left\">Name</th><th align=\"left\">Last Modified</th><th align=\"right\">Size</th></tr>");

        if (path.length() != 1) {
            out.println("<tr><td>");
            out.println("<a href=\"../\">..</a>");
            out.println("</td><td>-</td></tr>");
        }

        File files[] = dir.listFiles();
        List<File> filesSorted = Arrays.asList(files);

        Comparator<File> fileByNameComparator = new Comparator<File>() {

            @Override
            public int compare(File f1, File f2) {
                // keep folders at the top of the list
                if (f1.isDirectory() && !f2.isDirectory()) {
                    return -1;
                } else if (!f1.isDirectory() && f2.isDirectory()) {
                    return 1;
                }
                return f1.getName().compareTo(f2.getName());
            }
        };

        Collections.sort(filesSorted, fileByNameComparator);

        SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS z");

        for (File file : filesSorted) {
            out.println("<tr><td>");
            out.println("<a href=\"");
            out.println(urlPrefix);
            String name = file.getName();
            if (file.isDirectory()) {
                name += "/";
            }
            out.println(name);
            out.println("\">");
            out.println(name);
            out.println("</a>");
            out.println("</td><td align=\"right\">");
            out.println(df.format(new Date(file.lastModified())));
            out.println("</td><td align=\"right\">");
            if (file.isDirectory()) {
                out.println("-");
            } else {
                out.println(formatSize(file.length()));
            }
            out.println("</td></tr>");
        }

        out.println("</table>");

        out.println("</body>");
        out.println("</html>");

    }

    private String formatSize(long length) {
        if (length < 1024) {
            return String.valueOf(length);
        } else if (length < 1024 * 1024) {
            return String.valueOf(length / 1024) + "K";
        } else {
            return String.valueOf(length / (1024 * 1024)) + "M";
        }
    }

    protected String fileMimeType(String filename) {
        String mime = MimeMap.getContentType(FilenameUtils.getExtension(filename));
        if (mime != null) {
            return mime;
        } else {
            return "text/plain";
        }
    }

    protected void setFileResponseHeaders(File file, HttpServletResponse response) {
        response.setContentType(fileMimeType(file.getName()));
        response.setContentLength((int) file.length());
        if (attachment) {
            response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
        }
    }

    private void sendFile(String path, HttpServletResponse response) throws ServletException, IOException {
        File file = new File(rootDirectory, path);
        if (!file.canRead() || file.isDirectory()) {
            throw new ServletException("No such file: " + file);
        }

        setFileResponseHeaders(file, response);

        FileInputStream is = null;
        DataInputStream in = null;
        ServletOutputStream out = response.getOutputStream();
        byte[] bbuf = new byte[1024];
        try {
            in = new DataInputStream(is = new FileInputStream(file));
            int length;
            while ((in != null) && ((length = in.read(bbuf)) != -1)) {
                out.write(bbuf, 0, length);
            }
            out.flush();
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(out);
        }
    }

}
