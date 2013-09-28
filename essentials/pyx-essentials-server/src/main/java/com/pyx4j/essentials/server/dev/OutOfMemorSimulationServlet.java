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
 * Created on Sep 28, 2013
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.dev;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

@SuppressWarnings("serial")
public class OutOfMemorSimulationServlet extends HttpServlet {

    private final static Logger log = LoggerFactory.getLogger(OutOfMemorSimulationServlet.class);

    @I18n(strategy = I18n.I18nStrategy.IgnoreAll)
    private static enum Action {

        refresh,

        clearConsumed,

        consumeHeapMemory(true),

        consumePermGen(true);

        private final boolean countParam;

        Action() {
            this.countParam = false;
        }

        Action(boolean countParam) {
            this.countParam = countParam;
        }

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setDateHeader("Expires", System.currentTimeMillis());
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-control", "no-cache, no-store, must-revalidate");
        response.setContentType("text/html");
        if (!ApplicationMode.isDevelopment()) {
            return;
        }

        PrintWriter out = response.getWriter();

        Action action = null;
        String tp = request.getParameter("action");
        if (CommonsStringUtils.isStringSet(tp)) {
            try {
                action = Action.valueOf(tp);
            } catch (IllegalArgumentException e) {
                out.println("Invalid request action=" + tp);
            }
        }
        if (action == null) {
            help(out);
            info(out);
        } else {

            int count = 100;
            String countS = request.getParameter("count");
            if (CommonsStringUtils.isStringSet(countS)) {
                count = Integer.valueOf(countS);
            }
            info(out);

            w(out, action.name(), "<br/>");

            try {
                switch (action) {
                case refresh:
                    break;
                case clearConsumed:
                    clearConsumed();
                    break;
                case consumeHeapMemory:
                    consumeHeapMemory(out, count);
                    break;
                case consumePermGen:
                    consumePermGen(out, count);
                    break;
                }
            } catch (Throwable e) {
                w(out, "<br/><font color='red'>", "error", e.toString(), "</font>");
                log.error("error", e);

            }
            if (action != Action.refresh) {
                info(out);
            }

            help(out);
        }
        out.flush();
    }

    private void w(PrintWriter out, String... messages) {
        for (String message : messages) {
            out.print(message);
        }
        out.flush();
    }

    private void help(PrintWriter out) {
        w(out, "Usage:<br/><table>");
        for (Action t : EnumSet.allOf(Action.class)) {
            if (t.countParam) {
                w(out, "<tr><td><a href=\"");
                w(out, "?action=", t.name(), "&count=100", "\">");
                w(out, "?action=", t.name(), "&count=100");
                w(out, "</a></td><td>", t.toString());
                w(out, "</td></tr>");
            } else {
                w(out, "<tr><td><a href=\"");
                w(out, "?action=", t.name(), "\">");
                w(out, "?action=", t.name());
                w(out, "</a></td><td>", t.toString());
                w(out, "</td></tr>");
            }
        }
        w(out, "</table>");
    }

    private void info(PrintWriter out) {
        NumberFormat format = new DecimalFormat("#,##0.00");
        double mb = 1024 * 1024;
        w(out, "<pre>");
        {
            double free = Runtime.getRuntime().freeMemory();
            double total = Runtime.getRuntime().totalMemory();
            double used = total - free;
            w(out, "JVM    Heap: used ", format.format(used / mb), " MB");
            w(out, " (", format.format(100.0 * used / total), "%)");
            w(out, ", total ", format.format(total / mb), " MB");
            w(out, ", free ", format.format(free / mb), " MB");
        }
        w(out, "\n");
        {
            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            MemoryUsage mu = memoryMXBean.getNonHeapMemoryUsage();
            double total = mu.getMax();
            double used = mu.getUsed();
            double free = total - used;

            w(out, "JVM NonHeap: used ", format.format(used / mb), " MB");
            w(out, " (", format.format(100.0 * used / total), "%)");
            w(out, ", total ", format.format(total / mb), " MB");
            w(out, ", free ", format.format(free / mb), " MB");
        }
        w(out, "</pre>");
    }

    private static Set<Object> heap = new HashSet<Object>();

    private static Set<Object> permGen = new HashSet<Object>();

    private void clearConsumed() {
        heap.clear();
        permGen.clear();
        System.gc();
        try {
            Thread.sleep(200);
        } catch (InterruptedException ignore) {
        }
        System.gc();
    }

    static class MemoryEaterSimulationHeap {
        byte b[] = new byte[1024 * 1024];
    }

    private void consumeHeapMemory(PrintWriter out, int count) {
        int created = 0;
        try {
            ArrayList<Object> holder = new ArrayList<Object>();
            for (int i = 0; i < count; i++) {
                holder.add(new MemoryEaterSimulationHeap());
                created++;
            }
            heap.add(holder);
        } finally {
            w(out, "Created ", String.valueOf(created), " Heap objects of 1MB each");
        }
    }

    private void consumePermGen(PrintWriter out, int count) {
        int created = 0;
        NumberFormat format = new DecimalFormat("#,##0");
        GenClassLoader cl = new GenClassLoader();
        try {
            Class<?> cls[] = new Class[1000 * count];
            for (int i = 0; i < 1000 * count; i++) {
                try {
                    cls[i] = cl.loadClass("test.MemoryEaterSimulationClass" + i);
                    created++;
                } catch (ClassNotFoundException e) {
                    log.error("Error", e);
                }
            }
            permGen.add(cls);
        } finally {
            w(out, "Created ", format.format(created), " Classes");
        }

    }

    public static class GenClassLoader extends ClassLoader {

        @Override
        protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            // First, check if the class has already been loaded
            Class<?> result = findLoadedClass(name);
            if (result == null) {
                try {
                    result = super.loadClass(name, false);
                } catch (ClassNotFoundException e) {
                    result = findClass(name);
                }
            }
            if (resolve) {
                resolveClass(result);
            }
            return result;
        }

        @Override
        protected Class<?> findClass(final String name) throws ClassNotFoundException {
            byte[] byteCode = generateClasse(name);
            return defineClass(name, byteCode, 0, byteCode.length);
        }
    }

    private static byte[] generateClasse(final String name) {
        ClassWriter cw = new ClassWriter(0);

        cw.visit(Opcodes.V1_4, Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT + Opcodes.ACC_INTERFACE,
        // class name
                name.replace('.', '/'),
                // signature
                null,
                // super class
                "java/lang/Object",
                // interfaces
                null);

        cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT, "notify", // method name
                "(Ljava/lang/String;)V", // method descriptor
                null, // exceptions
                null); // method attributes

        cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT, "addListener", // method name
                "(Ljava/lang/String;)V", // method descriptor
                null, // exceptions       
                null); // method attributes

        cw.visitEnd();

        return cw.toByteArray();
    }

}
