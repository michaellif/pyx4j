/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 3, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.unit.server;

import java.net.JarURLConnection;
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

    private static final Logger log = LoggerFactory.getLogger(UnitTestsServicesImpl.class);

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
            log.error("JarFile is not avalable");
            return;
        }
        Enumeration<JarEntry> iter = jar.entries();
        while (iter.hasMoreElements()) {
            JarEntry entry = iter.nextElement();
            if (entry.isDirectory() || (!entry.getName().endsWith(".class"))) {
                continue;
            }
            for (Pattern inc : includePatterns) {
                if (inc.matcher(entry.getName()).matches()) {
                    processMatches(entry.getName());
                    continue;
                }
            }
        }
    }

    private void processMatches(String entryName) {
        for (Pattern exc : excludePatterns) {
            if (exc.matcher(entryName).matches()) {
                return;
            }
        }
        classes.add(getClassName(entryName));
    }

    public static String getClassName(String name) {
        String className = name.replace('/', '.');
        return className.substring(0, className.lastIndexOf('.'));
    }
}
