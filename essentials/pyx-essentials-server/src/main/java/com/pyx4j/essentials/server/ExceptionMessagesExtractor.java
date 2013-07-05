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
 * Created on Jul 4, 2013
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server;

import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionMessagesExtractor {

    private final static Logger log = LoggerFactory.getLogger(ExceptionMessagesExtractor.class);

    private static String DEJAVU = "...";

    private final StringBuilder buf = new StringBuilder();

    private int count = 0;

    private String prevMessage = null;

    private final Set<Throwable> dejaVu = Collections.newSetFromMap(new IdentityHashMap<Throwable, Boolean>());

    private final Set<String> dejaVuMessages = new HashSet<String>();

    /**
     * Extract messages from all nested exceptions
     * 
     * @param e
     * @return string \n separated
     */
    public static String getAllMessages(Throwable e) {
        ExceptionMessagesExtractor b = new ExceptionMessagesExtractor();
        b.extractErrorMessages(e);
        return b.buf.toString();
    }

    public static String getMessageSafe(Throwable e) {
        String className = "n/a";
        try {
            className = e.getClass().getName();
            String msg = e.getMessage();
            if (msg != null) {
                if (className.equals(msg) || msg.startsWith(className)) {
                    return msg;
                } else {
                    return className + ": " + msg;
                }
            } else {
                return e.toString();
            }
        } catch (Throwable ignore) {
            return className;
        }
    }

    public static String getExceptionClass(Throwable e) {
        Throwable cause = e;
        try {
            // Find first non wrapper exception
            while ((cause.getCause() != null) && (cause.getCause() != cause)) {
                break;
            }
        } catch (Throwable ignoreUnwarpError) {
        }
        return cause.getClass().getName();
    }

    private ExceptionMessagesExtractor() {
    }

    private void extractErrorMessages(Throwable e) {
        Throwable cause = e;
        do {
            dejaVu.add(cause);
            try {
                addMessage(getErrorMessage(cause));
                if (cause == cause.getCause()) {
                    break;
                }
                cause = cause.getCause();
            } catch (Throwable ignore) {
                log.warn("Can't completely extract error message {}", getMessageSafe(ignore));
            }
            if (dejaVu.contains(cause)) {
                break;
            }
        } while ((cause != null) && (count < 4));
    }

    private void addMessage(String message) {
        if (dejaVuMessages.contains(message)) {
            message = DEJAVU;
        } else {
            if (buf.toString().contains(message)) {
                message = DEJAVU;
            }
        }
        if ((prevMessage != null) && (prevMessage.equals(message))) {
            message = DEJAVU;
        }
        if ((DEJAVU.equals(prevMessage)) && (DEJAVU.equals(message))) {
            return;
        }
        count++;
        if (buf.length() > 0) {
            buf.append("\n");
        }
        buf.append(message);
        dejaVuMessages.add(message);
        prevMessage = message;
    }

    private String getErrorMessage(Throwable e) {
        String message = e.getMessage();
        if (message != null) {
            String className = e.getClass().getName();
            if (className.equals(message) || message.startsWith(className)) {
                return message;
            } else {
                if (dejaVuMessages.contains(message)) {
                    return DEJAVU;
                }
                dejaVuMessages.add(message);
                return e.getClass().getSimpleName() + ": " + message;
            }
        } else {
            return e.toString();
        }
    }
}
