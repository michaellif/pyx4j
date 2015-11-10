/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Nov 10, 2015
 * @author vlads
 */
package com.pyx4j.utils;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;

public class ErrorMessagesBuilder {

    private final static Logger log = LoggerFactory.getLogger(ErrorMessagesBuilder.class);

    private static Map<Class<? extends Throwable>, String> exceptionNames = new HashMap<>();

    private static String DEJAVU = "...";

    private final ErrorMessageOptions options;

    private final StringBuilder buf = new StringBuilder();

    private int count = 0;

    private String prevMessage = null;

    private final Set<Throwable> dejaVu = Collections.newSetFromMap(new IdentityHashMap<Throwable, Boolean>());

    private final Set<String> dejaVuMessages = new HashSet<String>();

    public static class ErrorMessageOptions {

        public boolean skipPackage = true;

    }

    public static String getAllErrorMessages(Throwable e, ErrorMessageOptions options) {
        ErrorMessagesBuilder b = new ErrorMessagesBuilder(options);
        b.extractErrorMessages(e);
        return b.buf.toString();
    }

    public static void registerExceptionName(Class<? extends Throwable> exceptionClass, String userMessagName) {
        exceptionNames.put(exceptionClass, userMessagName);
    }

    private ErrorMessagesBuilder(ErrorMessageOptions options) {
        this.options = options;
    }

    protected void extractErrorMessages(Throwable e) {
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
                log.warn("Can't completely extract error message {}", ExceptionUtils.getSafeErrorMessage(ignore));
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

    protected String exceptionClassName(Class<? extends Throwable> exceptionClass) {
        String hasName = exceptionNames.get(exceptionClass);
        if (hasName != null) {
            return hasName;
        }
        if (options.skipPackage) {
            return exceptionClass.getSimpleName();
        } else {
            return exceptionClass.getName();
        }
    }

    private String getErrorMessage(Throwable e) {
        String message = e.getMessage();
        if (message == null) {
            message = e.toString();
        }
        if (message != null) {
            String className = e.getClass().getName();
            if (className.equals(message)) {
                return exceptionClassName(e.getClass());
            } else if (message.startsWith(className)) {
                return exceptionClassName(e.getClass()) + message.substring(className.length());
            } else if (message.startsWith(e.getClass().getSimpleName())) {
                return message;
            } else {
                if (className.endsWith("_Exception")) {
                    String faultMessage = extractWebServiceFaultMessage(e);
                    if (faultMessage != null) {
                        message = faultMessage;
                    }
                }
                if (dejaVuMessages.contains(message)) {
                    return DEJAVU;
                }
                dejaVuMessages.add(message);
                return exceptionClassName(e.getClass()) + ": " + message;
            }
        } else {
            return exceptionClassName(e.getClass());
        }
    }

    private static List<String> faultMessageNames = Arrays.asList("msg", "message", "text");

    private static boolean isNameApplicable(String beanName) {
        String name = beanName.toLowerCase(Locale.ENGLISH);
        for (String nameFragment : faultMessageNames) {
            if (name.contains(nameFragment)) {
                return true;
            }
        }
        return false;
    }

    private boolean debugWebService = false;

    /**
     * Read different fields e.g. errorMsg
     */
    private String extractWebServiceFaultMessage(Throwable e) {
        try {
            Method getFaultInfoMethod = e.getClass().getMethod("getFaultInfo");
            if (getFaultInfoMethod == null) {
                if (debugWebService) {
                    log.debug("Exception {} does not have Method 'getFaultInfo'; {}", e.getClass().getSimpleName(), e.getClass().getMethods());
                }
                return null;
            }
            Object fault = getFaultInfoMethod.invoke(e);

            if (fault == null) {
                if (debugWebService) {
                    log.debug("Exception {} has empty fault", e.getClass().getSimpleName());
                }
                return null;
            }

            String message = null;
            for (PropertyDescriptor descriptor : Introspector.getBeanInfo(fault.getClass(), Object.class).getPropertyDescriptors()) {
                if (String.class.equals(descriptor.getPropertyType()) && (descriptor.getReadMethod() != null)) {
                    if (isNameApplicable(descriptor.getName())) {
                        Object value = descriptor.getReadMethod().invoke(fault);
                        if (value != null) {
                            message = value.toString();
                            if (debugWebService) {
                                log.debug("property {} value '{}' in {}", descriptor.getName(), message, e.getClass().getSimpleName());
                            }
                        } else if (debugWebService) {
                            log.debug("property {} value is null", descriptor.getName());
                        }
                    } else if (debugWebService) {
                        log.debug("property {} not applicable for message extraction from {}", descriptor.getName(), e.getClass().getSimpleName());
                    }
                } else if (debugWebService) {
                    log.debug("property {} not applicable for message extraction from {}", descriptor.getName(), e.getClass().getSimpleName());
                }
                if (CommonsStringUtils.isStringSet(message)) {
                    break;
                }
            }
            return message;
        } catch (Throwable x) {
            log.error("Error reading WebService Fault", x);
            return null;
        }

    }
}
