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
 * Created on Dec 10, 2013
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.server.mail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.pyx4j.commons.IStringView;
import com.pyx4j.gwt.server.IOUtils;

public class MessageTemplate {

    private String subject;

    private String body;

    private final Map<String, String> variables = new HashMap<String, String>();

    public MessageTemplate(String textResourceName) {
        try {
            body = IOUtils.getTextResource(textResourceName);
        } catch (IOException e) {
            throw new Error(e);
        }
        if (body == null) {
            throw new Error("Email template " + textResourceName + " not found");
        }
    }

    public MessageTemplate() {
    }

    public void setBodyTemplate(String body) {
        this.body = body;
    }

    public void variable(String name, Object value) {
        variables.put(name, toString(value));
    }

    protected String toString(Object arg) {
        if (arg == null) {
            return "";
        } else if (arg instanceof IStringView) {
            return ((IStringView) arg).getStringView();
        } else {
            return arg.toString();
        }
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        String text = body;
        for (Entry<String, String> me : variables.entrySet()) {
            text = text.replace(me.getKey(), me.getValue());
        }
        return text;
    }

    public String getWrappedBody(String wrapperTextResourceName) {
        String html;
        try {
            html = IOUtils.getTextResource(wrapperTextResourceName);
        } catch (IOException e) {
            throw new Error(e);
        }
        if (body == null) {
            throw new Error("Email wrapper template " + wrapperTextResourceName + " not found");
        }
        return html.replace("{MESSAGE}", getBody());
    }

}
