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
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
import com.pyx4j.commons.HtmlUtils;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.gwt.server.ServletUtils;
import com.pyx4j.log4j.LoggerConfig;
import com.pyx4j.server.contexts.ServerContext;

@SuppressWarnings("serial")
public class LogViewServlet extends HttpServlet {

    protected File rootDirectory;

    protected boolean attachment = false;

    protected boolean containerLogsEnabled = true;

    private static String containerLogs = "container-logs";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        initRootDirectory(config);
        attachment = "true".equalsIgnoreCase(config.getInitParameter("attachment"));
    }

    protected void initRootDirectory(ServletConfig config) {
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
    }

    protected boolean searchEnabled() {
        return true;
    }

    private void verifyPath(String path) throws ServletException {
        File f = new File(rootDirectory, path);
        if (!f.getAbsolutePath().startsWith(rootDirectory.getAbsolutePath())) {
            throw new ServletException("Permission denied");
        }
    }

    protected void authentication(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if ((ServerContext.getVisit() == null) || (!ServerContext.getVisit().isUserLoggedIn())) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new ServletException("Request requires authentication.");
        }
    }

    protected boolean isContainerLogsEnabled() {
        return containerLogsEnabled;
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
            // Make absolute URL
            urlPrefix = "/" + ServletUtils.getRelativeServletPath(request, request.getServletPath()).substring(1) + "/";
        } else {
            // URL decode on Tomcat is not working properly
            path = path.replace("%20", " ");
        }
        verifyPath(path);

        if (request.getParameterValues("download") != null) {
            sendFilesZip(path, response, request.getParameterValues("download"));
        } else if (path.endsWith("/")) {
            listDirectory(request, path, urlPrefix, response);
        } else {
            sendFile(path, response);
        }
    }

    protected File getSpecialPath(String path) {
        if (isContainerLogsEnabled() && (path.startsWith("/" + containerLogs))) {
            return new File(rootDirectory.getParentFile(), path.substring(containerLogs.length() + 2));
        } else {
            return new File(rootDirectory, path);
        }
    }

    private void listDirectory(HttpServletRequest request, String path, String urlPrefix, HttpServletResponse response) throws ServletException, IOException {
        File dir = getSpecialPath(path);
        if (!dir.isDirectory()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            //throw new ServletException("No such directory: " + dir);
            return;
        }
        response.setContentType("text/html");
        final PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>LogView</title>");
        out.println("<META HTTP-EQUIV=\"PRAGMA\" CONTENT=\"NO-CACHE\">");
        out.println("</head>");
        out.println("<body>");

        if (searchEnabled()) {
            searchForm(request, out);
        }

        out.println("<h1> Index of ");
        out.println(dir.getAbsolutePath());
        out.println("</h1>");

        out.println("<span id =\"searchProcess\">");

        long start = System.currentTimeMillis();

        List<File> files;
        if (searchEnabled()) {
            files = FileSearch.searchFileNio(new FileSearchFilter(request), dir.toPath(), new FileSearchProgressCallback() {

                long keepAlive = System.currentTimeMillis();

                @Override
                public void onVisitDirectory() {
                    if (System.currentTimeMillis() - keepAlive > Consts.SEC2MILLISECONDS) {
                        out.println(".");
                        keepAlive = System.currentTimeMillis();
                        out.flush();
                    }
                }

            });
        } else {
            files = Arrays.asList(dir.listFiles());
        }

        out.println("</span>");

        out.println("<form method=\"get\">");
        out.println("<table>");

        out.println("<tr><th></th><th align=\"left\">Name</th><th align=\"left\">Last Modified</th><th align=\"right\">Size</th></tr>");

        if (path.length() != 1) {
            out.println("<tr><td></td><td>");
            out.println("<a href=\"../\">..</a>");
            out.println("</td><td>-</td></tr>");
        }

        Comparator<File> fileByNameComparator = new Comparator<File>() {

            @Override
            public int compare(File f1, File f2) {
                // keep folders at the top of the list
                if (f1.getParentFile() == f2.getParentFile()) {
                    if (f1.isDirectory() && !f2.isDirectory()) {
                        return -1;
                    } else if (!f1.isDirectory() && f2.isDirectory()) {
                        return 1;
                    }
                }
                return f1.getAbsolutePath().compareTo(f2.getAbsolutePath());

            }
        };

        Collections.sort(files, fileByNameComparator);

        if (isContainerLogsEnabled() && (path.length() == 1)) {
            out.println("<tr><td></td><td>");
            out.print("<a href=\"");
            out.print(urlPrefix);
            out.print(containerLogs);
            out.print("/\">");
            out.print(containerLogs);
            out.print("/</a>");
            out.println("</td></tr>");
        }

        SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS z");

        int idx = 0;
        for (File file : files) {
            out.println("<tr><td>");

            if (file.isFile()) {
                out.println("<input name=\"download\" id=\"" + (idx++) + "\" type=\"checkbox\" value=\"" + file.getName() + "\">");
            }

            out.println("</td><td>");

            out.print("<a href=\"");
            out.print(urlPrefix);
            String name;
            if (file.getParentFile() == dir) {
                name = file.getName();
            } else {
                name = Paths.get(dir.toURI()).relativize(Paths.get(file.toURI())).toString().replace('\\', '/');
            }
            if (file.isDirectory()) {
                name += "/";
            }
            out.print(name);
            out.print("\">");
            out.print(name);
            out.print("</a>");
            out.print("</td><td align=\"right\">");
            out.print(df.format(new Date(file.lastModified())));
            out.print("</td><td align=\"right\">");
            if (file.isDirectory()) {
                out.print("-");
            } else {
                out.print(formatSize(file.length()));
            }
            out.println("</td></tr>");
        }

        out.println("</table>");

        out.println("<br/>");

        out.println("<input type=\"submit\" value=\"Download Selected as zip\" />");

        out.println("</form>");

        out.println("Generated in <i>" + TimeUtils.secSince(start) + "</i>");

        out.println("<script>document.getElementById(\"searchProcess\").style.visibility = \"hidden\";</script>");

        out.println("</body>");
        out.println("</html>");

    }

    protected void searchForm(HttpServletRequest request, PrintWriter out) {
        out.println("<form method=\"get\">");
        out.println("Search File Date From:<input name=\"fd\" placeholder=\"yyyy-MM-dd\" type=\"date\" value=\"" + dateValue(request, "fd") + "\" />");
        out.println("<input name=\"ft\" placeholder=\"HH:mm\" type=\"time\" value=\"" + timeValue(request, "ft") + "\" />");
        out.println(" To:<input name=\"td\" placeholder=\"yyyy-MM-dd\" type=\"date\" value=\"" + dateValue(request, "td") + "\" />");
        out.println("<input name=\"tt\" placeholder=\"HH:mm\" type=\"time\" value=\"" + timeValue(request, "tt") + "\" />");
        out.println(" Containing:<input name=\"text\" type=\"text\" value=\"" + textValue(request) + "\" />");
        out.println("<input type=\"checkbox\" name=\"recursive\" value=\"true\"" + checkedValue(request, "recursive") + " />Recursive");
        out.println("<input type=\"submit\" value=\"Submit\" />");
        out.println("</form>");
    }

    private String timeValue(HttpServletRequest request, String name) {
        String text = request.getParameter(name);
        if (text == null) {
            return "";
        } else {
            Date date;
            try {
                date = new SimpleDateFormat("HH:mm", Locale.ENGLISH).parse(text.trim());
            } catch (ParseException e) {
                return "";
            }
            return HtmlUtils.escapeText(new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(date));
        }
    }

    private String dateValue(HttpServletRequest request, String name) {
        String text = request.getParameter(name);
        if (text == null) {
            return "";
        } else {
            Date date;
            try {
                date = DateUtils.detectDateformat(text.trim());
            } catch (RuntimeException e) {
                return "";
            }
            return HtmlUtils.escapeText(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(date));
        }
    }

    private String textValue(HttpServletRequest request) {
        String text = request.getParameter("text");
        if (text == null) {
            return "";
        } else {
            return HtmlUtils.escapeText(text);
        }
    }

    private String checkedValue(HttpServletRequest request, String name) {
        String text = request.getParameter(name);
        if (text == null) {
            return "";
        } else {
            return Boolean.valueOf(text) ? "checked" : "";
        }
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

    private void sendFilesZip(String path, HttpServletResponse response, String[] fileNameArrays) throws ServletException, IOException {
        List<String> filesNames = Arrays.asList(fileNameArrays);
        File dir = getSpecialPath(path);
        File[] files = dir.listFiles();

        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=\"logs.zip\"");

        ServletOutputStream out = response.getOutputStream();
        ZipOutputStream zip = new ZipOutputStream(out);
        try {
            for (File file : files) {
                if (!filesNames.contains(file.getName())) {
                    continue;
                }
                ZipEntry ze = new ZipEntry(file.getName());
                ze.setTime(file.lastModified());
                zip.putNextEntry(ze);

                FileInputStream is = null;
                DataInputStream in = null;
                byte[] bbuf = new byte[1024];
                try {
                    in = new DataInputStream(is = new FileInputStream(file));
                    int length;
                    while ((in != null) && ((length = in.read(bbuf)) != -1)) {
                        zip.write(bbuf, 0, length);
                    }
                } finally {
                    IOUtils.closeQuietly(in);
                    IOUtils.closeQuietly(is);
                }
                zip.closeEntry();
            }
            zip.close();
            out.flush();
        } finally {
            IOUtils.closeQuietly(zip);
            IOUtils.closeQuietly(out);
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
        File file = getSpecialPath(path);
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
