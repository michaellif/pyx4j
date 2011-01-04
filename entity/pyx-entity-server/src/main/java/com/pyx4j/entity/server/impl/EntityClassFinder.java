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
 * Created on Jan 6, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server.impl;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.gwt.server.ClassFinder;

public class EntityClassFinder extends ClassFinder {

    private static final Logger log = LoggerFactory.getLogger(EntityClassFinder.class);

    public static final String MARKER_RESOURCE_NAME = "META-INF/persistence-domain.marker";

    protected EntityClassFinder(String urlExternalForm) {
        super(urlExternalForm.substring(0, urlExternalForm.lastIndexOf(MARKER_RESOURCE_NAME)));
        log.debug("domain path {}", getBaseURL());
    }

    public static List<String> findEntityClasses() {
        List<String> classes = new Vector<String>();
        ClassLoader cld = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> urls;
        try {
            urls = cld.getResources(MARKER_RESOURCE_NAME);
        } catch (IOException e) {
            log.error("unable to find domain markers", e);
            return classes;
        }
        while (urls.hasMoreElements()) {
            URL u = urls.nextElement();
            EntityClassFinder cf = new EntityClassFinder(u.toExternalForm());
            for (String c : cf.getClasses()) {
                if (!classes.contains(c)) {
                    classes.add(c);
                }
            }
        }
        return classes;
    }

    @Override
    protected boolean acceptClass(String className) {
        Class<?> candidate;
        try {
            candidate = Class.forName(className, false, Thread.currentThread().getContextClassLoader());
        } catch (Throwable e) {
            log.warn("Can't load class {} {}", className, e);
            return false;
        }
        return candidate.isInterface() && IEntity.class.isAssignableFrom(candidate);// && (candidate.getAnnotation(AbstractEntity.class) == null);
    }
}
