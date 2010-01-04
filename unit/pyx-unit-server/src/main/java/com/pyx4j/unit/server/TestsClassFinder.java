/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 4, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.unit.server;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestsClassFinder extends ClassFinder {

    private static final Logger log = LoggerFactory.getLogger(TestsClassFinder.class);

    public static final String TESTS_MARKER_RESOURCE_NAME = "server-side-tests.marker";

    public TestsClassFinder(String urlExternalForm) {
        super(urlExternalForm);
        include(".*Test\\.class");
        exclude(".*GWTTest\\.class");
    }

    public static List<String> findTestClasses() {
        List<String> classes = new Vector<String>();
        ClassLoader cld = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> urls;
        try {
            urls = cld.getResources(TESTS_MARKER_RESOURCE_NAME);
        } catch (IOException e) {
            log.error("unable to find tests markers", e);
            return classes;
        }
        while (urls.hasMoreElements()) {
            URL u = urls.nextElement();
            TestsClassFinder cf = new TestsClassFinder(u.toExternalForm());
            classes.addAll(cf.getClasses());
        }

        //classes.addAll(findTestClassesDevMode());

        return classes;
    }

    /**
     * TODO now this only creates java.security.AccessControlException: access denied
     */
    private static List<String> findTestClassesDevMode() {
        List<String> classes = new Vector<String>();
        String cp = System.getProperty("java.class.path");
        if (cp == null) {
            return classes;
        }
        StringTokenizer st = new StringTokenizer(cp, File.pathSeparator);
        nextPathToken: while (st.hasMoreTokens()) {
            String path = st.nextToken();
            if (path.contains("appengine") || path.endsWith(File.separator + "java")) {
                continue nextPathToken;
            }
            try {
                File classesDir = new File(path);
                if (!classesDir.isDirectory()) {
                    continue nextPathToken;
                }
                if (!(new File(classesDir, TESTS_MARKER_RESOURCE_NAME)).canRead()) {
                    continue nextPathToken;
                }
                TestsClassFinder cf;
                try {
                    cf = new TestsClassFinder(classesDir.toURI().toURL().toExternalForm());
                } catch (MalformedURLException e) {
                    continue nextPathToken;
                }
                classes.addAll(cf.getClasses());
            } catch (Throwable e) {
                log.error("unable to read tests markers", e);
            }
        }
        return classes;
    }
}
