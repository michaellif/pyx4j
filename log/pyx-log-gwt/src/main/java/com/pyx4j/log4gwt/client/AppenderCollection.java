/**
 * Pyx4j framework
 * Copyright (C) 2006-2010 pyx4j.com.
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
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.log4gwt.client;

import java.util.List;
import java.util.Vector;

import com.pyx4j.log4gwt.shared.LogEvent;

public class AppenderCollection {

    protected List<Appender> appenderList;

    /**
     * Attach an appender. If the appender is already in the list in won't be added again.
     */
    public void addAppender(Appender newAppender) {
        // Null values for newAppender parameter are strictly forbidden.
        if (newAppender == null) {
            return;
        }

        if (appenderList == null) {
            appenderList = new Vector<Appender>();
        }
        if (!appenderList.contains(newAppender)) {
            appenderList.add(newAppender);
        }
    }

    /**
     * Call the <code>doAppend</code> method on all attached appenders.
     */
    public int callAppenders(LogEvent event) {
        int size = 0;
        Appender appender;

        if (appenderList != null) {
            size = appenderList.size();
            for (int i = 0; i < size; i++) {
                appender = appenderList.get(i);
                appender.doAppend(event);
            }
        }
        return size;
    }

    /**
     * Send the buffer to storage or remote computer.
     */
    public void flush() {
        if (appenderList != null) {
            int size = appenderList.size();
            for (int i = 0; i < size; i++) {
                Appender appender = appenderList.get(i);
                if (appender instanceof AppenderRemote) {
                    ((AppenderRemote) appender).flush();
                }
            }
        }
    }

    /**
     * Get all attached appenders as an Enumeration. If there are no attached appenders
     * <code>null</code> is returned.
     * 
     * @return List of attached appenders.
     */
    public List<Appender> getAllAppenders() {
        if (appenderList == null) {
            return null;
        } else {
            return appenderList;
        }
    }

    /**
     * Look for an attached appender named as <code>name</code>.
     * 
     * <p>
     * Return the appender with that name if in the list. Return null otherwise.
     * 
     */
    public Appender getAppender(String name) {
        if (appenderList == null || name == null) {
            return null;
        }

        int size = appenderList.size();
        Appender appender;
        for (int i = 0; i < size; i++) {
            appender = appenderList.get(i);
            if (name.equals(appender.getAppenderName())) {
                return appender;
            }
        }
        return null;
    }

    /**
     * Returns <code>true</code> if the specified appender is in the list of attached
     * appenders, <code>false</code> otherwise.
     * 
     * @since 1.2
     */
    public boolean isAttached(Appender appender) {
        if (appenderList == null || appender == null) {
            return false;
        }

        int size = appenderList.size();
        Appender a;
        for (int i = 0; i < size; i++) {
            a = appenderList.get(i);
            if (a == appender) {
                return true;
            }
        }
        return false;
    }

    /**
     * Remove and close all previously attached appenders.
     */
    public void removeAllAppenders() {
        if (appenderList != null) {
            appenderList.clear();
            appenderList = null;
        }
    }

    /**
     * Remove the appender passed as parameter form the list of attached appenders.
     */
    public void removeAppender(Appender appender) {
        if (appender == null || appenderList == null) {
            return;
        }
        appenderList.remove(appender);
    }

    /**
     * Remove the appender with the name passed as parameter form the list of appenders.
     */
    public void removeAppender(String name) {
        if (name == null || appenderList == null) {
            return;
        }
        int size = appenderList.size();
        for (int i = 0; i < size; i++) {
            if (name.equals((appenderList.get(i)).getAppenderName())) {
                appenderList.remove(i);
                break;
            }
        }
    }

}
