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
 * Created on 2011-02-21
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.log4j;

import java.util.Map;

import org.apache.log4j.spi.LoggingEvent;

public class RollingFileAppender extends org.apache.log4j.RollingFileAppender {

    private static final boolean debug = false;

    /**
     * start new file on every restart
     */
    protected boolean initialyRollOver = true;

    protected boolean activateOnFirstEvent = true;

    protected boolean firstEvent = true;

    public boolean isInitialyRollOver() {
        return initialyRollOver;
    }

    public void setInitialyRollOver(boolean initialyRollOver) {
        this.initialyRollOver = initialyRollOver;
    }

    @Override
    public void activateOptions() {
        if (!activateOnFirstEvent) {
            executeActivateOptions();
        }
    }

    private void executeActivateOptions() {
        String fileName = getFile();
        if (fileName == null) {
            System.err.println("activate NULL file name ");
            (new Throwable()).printStackTrace();
        }
        for (Map.Entry<String, String> entry : LoggerConfig.nameVariables.entrySet()) {
            String name = "%{" + entry.getKey().toString() + "}";
            if ((fileName.contains(name)) && (entry.getValue() != null)) {
                fileName = fileName.replace(name, entry.getValue());
            }
        }
        fileName = fileName.replaceAll("%\\{.*\\}", "");
        if (debug) {
            System.out.println("activate file name " + fileName);
            (new Throwable()).printStackTrace();
        }
        super.setFile(fileName);

        if (isInitialyRollOver()) {
            if (debug) {
                System.out.println("Initialy RollOver");
            }
            rollOver();
        }
        super.activateOptions();
    }

    @Override
    public void append(LoggingEvent event) {
        if (activateOnFirstEvent && firstEvent) {
            firstEvent = false;
            executeActivateOptions();
        }
        super.append(event);
    }

}
