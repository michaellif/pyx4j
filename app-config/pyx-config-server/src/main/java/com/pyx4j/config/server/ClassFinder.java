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
 * Created on Jan 3, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.config.server;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassFinder {

    private static final Logger log = LoggerFactory.getLogger(ClassFinder.class);

    private final List<Pattern> includePatterns = new Vector<Pattern>();

    private final List<Pattern> excludePatterns = new Vector<Pattern>();

    private String baseURL;

    private List<String> classes;

    public ClassFinder(String urlExternalForm) {
        if (urlExternalForm.endsWith("/")) {
            baseURL = urlExternalForm;
        } else {
            baseURL = urlExternalForm.substring(0, urlExternalForm.lastIndexOf("/") + 1);
        }
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void include(String regex) {
        includePatterns.add(Pattern.compile(regex));
    }

    public void exclude(String regex) {
        excludePatterns.add(Pattern.compile(regex));
    }

    public synchronized List<String> getClasses() {
        if (classes == null) {
            classes = new Vector<String>();
            if (baseURL.startsWith("jar:")) {
                readJarLocation();
            } else if (baseURL.startsWith("file:")) {
                readFileLocation();
            } else if (baseURL.startsWith("bundleresource:")) {
                readOSGiBundle();
            } else {
                log.error("unsupporte URL {}", baseURL);
            }
        }
        return classes;
    }

    private void readJarLocation() {
        URL jarURL = null;
        JarFile jar = null;
        try {
            jarURL = new URL(baseURL);
            JarURLConnection connection = (JarURLConnection) jarURL.openConnection();
            connection.setUseCaches(false);
            jar = connection.getJarFile();
        } catch (Throwable e) {
            log.error("Can't open Jar URL [" + baseURL + "]", e);
            return;
        }
        if (jar == null) {
            log.error("JarFile is not available");
            return;
        }
        try {
            Enumeration<JarEntry> iter = jar.entries();
            while (iter.hasMoreElements()) {
                JarEntry entry = iter.nextElement();
                if (entry.isDirectory() || (!entry.getName().endsWith(".class"))) {
                    continue;
                }
                processEntry(entry.getName());
            }
        } finally {
            try {
                jar.close();
            } catch (IOException ignore) {
            }
        }
    }

    private void readOSGiBundle() {
        try {
            Class<?> fileLocatorClass = this.getClass().getClassLoader().loadClass("org.eclipse.core.runtime.FileLocator");
            Method equinoxResolveMethod = fileLocatorClass.getMethod("resolve", new Class[] { URL.class });
            URL url = new URL(baseURL);
            URL resolvedUrl = (URL) equinoxResolveMethod.invoke(null, url);
            log.debug("resolved bundle Url {} ", resolvedUrl);
            String urlExternalForm = resolvedUrl.toExternalForm();
            baseURL = urlExternalForm.substring(0, urlExternalForm.lastIndexOf("/") + 1);
            if (baseURL.startsWith("jar:")) {
                readJarLocation();
            } else if (baseURL.startsWith("file:")) {
                readFileLocation();
            } else {
                log.error("unsupporte URL {}", baseURL);
            }
        } catch (Throwable e) {
            log.error("Can't open OSGi URL [" + baseURL + "]", e);
        }
    }

    private void readFileLocation() {
        File root;
        try {
            root = new File(new URI(baseURL));
        } catch (URISyntaxException e) {
            log.error("Can't open File URL [" + baseURL + "]", e);
            return;
        }
        processFiles(root, root);
    }

    private String relativeUnixFileName(File root, File file) {
        String relative = file.getAbsolutePath().substring(root.getAbsolutePath().length() + 1);
        return relative.replace(File.separatorChar, '/');
    }

    private void processFiles(File root, File file) {
        if (file.getName().endsWith(".class")) {
            processEntry(relativeUnixFileName(root, file));
        } else {
            File[] list = file.listFiles();
            if (list != null) {
                for (File f : list) {
                    processFiles(root, f);
                }
            }
        }
    }

    protected void processEntry(String entryName) {
        if (includePatterns.size() == 0) {
            processMatches(entryName);
            return;
        } else {
            for (Pattern inc : includePatterns) {
                if (inc.matcher(entryName).matches()) {
                    processMatches(entryName);
                    return;
                }
            }
        }
    }

    protected void processMatches(String entryName) {
        for (Pattern exc : excludePatterns) {
            if (exc.matcher(entryName).matches()) {
                return;
            }
        }
        String name = getClassName(entryName);
        if (acceptClass(name)) {
            classes.add(name);
        }
    }

    protected boolean acceptClass(String className) {
        return true;
    }

    public static String getClassName(String name) {
        String className = name.replace('/', '.');
        return className.substring(0, className.lastIndexOf('.'));
    }

    public static void debugClassLoader(String message, Object obj) {
        if (obj == null) {
            log.debug(message + " no class, no object");
            return;
        }
        Class<?> klass;
        StringBuffer buf = new StringBuffer();
        buf.append(message).append(" ");
        if (obj instanceof Class<?>) {
            klass = (Class<?>) obj;
            buf.append("class ");
        } else {
            klass = obj.getClass();
            buf.append("instance ");
        }
        buf.append(klass.getName() + " loaded by ");
        if (klass.getClassLoader() != null) {
            buf.append(klass.getClassLoader().getClass().getName());
            buf.append('@').append(Integer.toHexString(klass.getClassLoader().hashCode()));
        } else {
            buf.append("system");
        }
        log.debug(buf.toString());
    }

    public static void debugClassLoader(String message, ClassLoader classLoader) {
        StringBuffer buf = new StringBuffer();
        buf.append(message).append(" ClassLoader ");
        if (classLoader != null) {
            buf.append(classLoader.getClass().getName());
            buf.append('@').append(Integer.toHexString(classLoader.hashCode()));
        } else {
            buf.append("system");
        }
        log.debug(buf.toString());
    }
}
