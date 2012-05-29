/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 28, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.reports.util;

import java.util.Deque;
import java.util.LinkedList;

public class XMLBuilder {

    private boolean isElementDefinitionInProgress = false;

    private final Deque<String> elementStack = new LinkedList<String>();

    private String indentation = "";

    private final StringBuffer doc = new StringBuffer();

    private void identInc() {
        indentation = indentation + "    ";
    }

    private void identDec() {
        indentation = indentation.substring(0, indentation.length() - 4);
    }

    public void line(String str) {
        doc.append(indentation).append(str).append("\n");
    }

    public void CDATA(String expression) {
        line("<![CDATA[" + expression + "]]>");
    }

    public ElementBuilder elo(String name) {
        return new ElementBuilder(name, false);
    }

    public ElementBuilder el(String name) {
        return new ElementBuilder(name, true);
    }

    public void elc(String element) {
        elc();
    }

    public void elc() {
        if (XMLBuilder.this.isElementDefinitionInProgress) {
            throw new IllegalStateException(
                    "attempting to close element definition before finishing element openning: please end previous definition with 'add()'");
        }
        String element = elementStack.pop();
        identDec();
        line("</" + element + ">");
    }

    public String build() {
        return doc.toString();
    }

    public class ElementBuilder {

        private final StringBuffer element;

        private final boolean isClosed;

        private ElementBuilder(String element, boolean isClosed) {
            if (XMLBuilder.this.isElementDefinitionInProgress) {
                throw new IllegalStateException(
                        "attempting to start new element definition before finishing another one: please end previous definition with 'add()'");
            }
            XMLBuilder.this.isElementDefinitionInProgress = true;

            this.isClosed = isClosed;
            this.element = new StringBuffer();
            this.element.append("<").append(element);

            if (!isClosed) {
                XMLBuilder.this.elementStack.push(element);
            }
        }

        /**
         * add an attribute if <code>value != null</code>
         * 
         * @param attribute
         * @param value
         * @return
         */
        public ElementBuilder attr(String attribute, String value) {
            if (value != null) {
                element.append(" ").append(attribute).append("=\"").append(value).append("\"");
            }
            return this;
        }

        public void add() {
            if (isClosed) {
                element.append("/");
            }
            element.append(">");

            XMLBuilder.this.line(element.toString());
            if (!isClosed) {
                identInc();
            }
            XMLBuilder.this.isElementDefinitionInProgress = false;
        }
    }

}
