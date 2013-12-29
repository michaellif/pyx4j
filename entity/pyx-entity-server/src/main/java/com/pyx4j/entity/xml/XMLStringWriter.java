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
 * Created on Mar 27, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.xml;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Stack;

import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.xml.XMLEscape;
import com.pyx4j.entity.xml.XMLStringWriter;

public class XMLStringWriter {

    private final Charset charset;

    private final String namespace;

    private String schemaLocation;

    private final StringBuilder out = new StringBuilder();

    private int level = 0;

    private final Stack<String> openItems = new Stack<String>();

    public XMLStringWriter() {
        charset = null;
        namespace = null;
    }

    public XMLStringWriter(Charset charset) {
        this(charset, null);
    }

    public XMLStringWriter(Charset charset, String namespace) {
        this.charset = charset;
        this.namespace = namespace;
        out.append("<?xml version=\"1.0\" encoding=\"").append(charset.displayName()).append("\"?>\n");
    }

    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    public void idented() {
        for (int i = 0; i < level; i++) {
            out.append("  ");
        }
    }

    public XMLStringWriter append(String xml) {
        out.append(xml);
        return this;
    }

    public void start(String name) {
        beggining(name, null);
        out.append(">");
    }

    private void start(String name, Map<String, String> attributes) {
        beggining(name, attributes);
        out.append(">");
    }

    private void beggining(String name, Map<String, String> attributes) {
        out.append("<");
        if ((openItems.size() == 0) && (namespace != null)) {
            out.append("ns1:");
            out.append(name);
            out.append(" xmlns:ns1=\"").append(namespace).append("\"");
            if (schemaLocation != null) {
                out.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
                out.append(" xsi:schemaLocation=\"").append(namespace).append(' ').append(schemaLocation).append("\"");
            }
        } else {
            out.append(name);
        }
        if (attributes != null) {
            for (Map.Entry<String, String> me : attributes.entrySet()) {
                if (me.getValue() != null) {
                    out.append(' ').append(me.getKey()).append("=\"").append(me.getValue()).append('"');
                }
            }
        }
        openItems.push(name);
    }

    public void writeEmpty(String name, Map<String, String> attributes) {
        idented();
        beggining(name, attributes);
        out.append("/>\n");
        openItems.pop();
    }

    public void startIdented(String name) {
        startIdented(name, null);
    }

    public void startIdented(String name, Map<String, String> attributes) {
        idented();
        level++;
        start(name, attributes);
        out.append("\n");
    }

    public void end() {
        String name = openItems.pop();
        out.append("</");
        if ((openItems.size() == 0) && (namespace != null)) {
            out.append("ns1:");
        }
        out.append(name).append(">\n");
    }

    public void endIdented() {
        level--;
        idented();
        end();
    }

    public void write(String name, Object value) {
        write(name, null, value);
    }

    public void write(String name, Map<String, String> attributes, Object value) {
        idented();
        start(name, attributes);
        if (value != null) {
            XMLEscape.appendEscapeText(out, value.toString());
        }
        end();
    }

    public void write(String name, IPrimitive<?> value) {
        if (value.isNull()) {
            return;
        }
        //TODO use getStringView()
        write(name, value.getValue());
    }

    public void writeRaw(String name, Object value) {
        if (value == null) {
            return;
        }
        idented();
        start(name);
        append(value.toString());
        end();
    }

    public void writeRaw(String name, IPrimitive<?> value) {
        if (value.isNull()) {
            return;
        }
        //TODO use getStringView()
        writeRaw(name, value.getValue());
    }

    @Override
    public String toString() {
        return out.toString();
    }

    public byte[] getBytes() {
        if (charset != null) {
            return out.toString().getBytes(charset);
        } else {
            return out.toString().getBytes();
        }
    }

}
