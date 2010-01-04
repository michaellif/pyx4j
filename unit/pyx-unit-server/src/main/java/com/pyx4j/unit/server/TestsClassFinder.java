/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 4, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.unit.server;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestsClassFinder extends ClassFinder {

    private static final Logger log = LoggerFactory.getLogger(TestsClassFinder.class);

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
            urls = cld.getResources("server-side-tests.marker");
        } catch (IOException e) {
            log.error("unable to find tests markers", e);
            return classes;
        }
        while (urls.hasMoreElements()) {
            URL u = urls.nextElement();
            TestsClassFinder cf = new TestsClassFinder(u.toExternalForm());
            classes.addAll(cf.getClasses());
        }
        return classes;
    }

}
