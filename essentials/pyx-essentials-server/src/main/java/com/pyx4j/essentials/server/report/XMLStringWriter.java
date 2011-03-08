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
package com.pyx4j.essentials.server.report;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Stack;

import com.pyx4j.entity.shared.IPrimitive;

public class XMLStringWriter {

    private final Charset charset;

    private final StringBuilder out = new StringBuilder();

    private int level = 0;

    private final Stack<String> openItems = new Stack<String>();

    public XMLStringWriter() {
        charset = null;
    }

    public XMLStringWriter(Charset charset) {
        this.charset = charset;
        out.append("<?xml version=\"1.0\" encoding=\"").append(charset.displayName()).append("\"?>\n");
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
        out.append("<").append(name).append(">");
    }

    public void start(String name, Map<String, String> attributes) {
        out.append("<").append(name);
        if (attributes != null) {
            for (Map.Entry<String, String> me : attributes.entrySet()) {
                if (me.getValue() != null) {
                    out.append(' ').append(me.getKey()).append("=\"").append(me.getValue()).append('"');
                }
            }
        }
        out.append(">");
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

    public void open(String name) {
        start(name);
        openItems.push(name);
    }

    public void close() {
        end(openItems.pop());
    }

    public void end(String name) {
        out.append("</").append(name).append(">\n");
    }

    public void endIdented(String name) {
        level--;
        idented();
        end(name);
    }

    public void write(String name, Object value) {
        write(name, null, value);
    }

    public void write(String name, Map<String, String> attributes, Object value) {
        if (value == null && (attributes == null || attributes.size() == 0)) {
            return;
        }
        idented();
        start(name, attributes);
        if (value != null) {
            XMLEscape.appendEscapeText(out, value.toString());
        }
        end(name);
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
        end(name);
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
