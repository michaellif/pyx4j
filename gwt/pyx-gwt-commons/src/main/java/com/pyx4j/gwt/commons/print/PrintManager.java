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
 * Created on Dec 16, 2013
 * @author michaellif
 */
package com.pyx4j.gwt.commons.print;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadElement;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.Node;
import com.google.gwt.user.client.ui.RootPanel;

public class PrintManager {

    private final IFrameElement iframe;

    private static class SingletonHolder {
        public static final PrintManager INSTANCE = new PrintManager();
    }

    public static PrintManager instance() {
        return SingletonHolder.INSTANCE;
    }

    private PrintManager() {
        iframe = Document.get().createIFrameElement();
        iframe.setAttribute("tabIndex", "-1");
        iframe.setAttribute("style", "position: absolute; width: 0; height: 0; border: 0");
        iframe.setAttribute("src", "javascript:''");
        RootPanel.get().getElement().appendChild(iframe);
    }

    public static void print(Element element) {
        Element headElement = Document.get().getElementsByTagName("head").getItem(0);
        Node headClone = HeadElement.as(headElement).cloneNode(true);
        StringBuilder contentBuilder = new StringBuilder("<html>");
        contentBuilder.append(headClone.toString()).append("<body>").append(element.getInnerHTML()).append("</body>").append("</html>");
        fillIframe(instance().iframe, contentBuilder.toString());
        printIframe(instance().iframe);
    }

    public static void preview(Element element) {
        Element headElement = Document.get().getElementsByTagName("head").getItem(0);
        Node headClone = HeadElement.as(headElement).cloneNode(true);
        StringBuilder contentBuilder = new StringBuilder("<html>");
        contentBuilder.append(headClone.toString()).append("<body>").append(element.getInnerHTML()).append("</body>").append("</html>");

        fillIframe(instance().iframe, contentBuilder.toString());
        previewIframe(instance().iframe);
    }

    private static final native void fillIframeHead(IFrameElement iframe, String content)// @formatter:off
    /*-{
        var doc = iframe.contentWindow.document;
        doc.open();
        doc.writeln(content);
        doc.close();
      }-*/;
    // @formatter:on

    private static final native void fillIframe(IFrameElement iframe, String content)// @formatter:off
    /*-{
      var doc = iframe.contentDocument;
        doc.open();
        doc.writeln(content);
        doc.close();
      }-*/;
    // @formatter:on

    private static final native void printIframe(IFrameElement iframe)// @formatter:off
    /*-{
       frame = iframe.contentWindow;
       frame.focus();
       frame.print();
       }-*/;
    // @formatter:on

    private static final native void previewIframe(IFrameElement iframe)// @formatter:off
    /*-{
     var child = $wnd.open();
     child.document.writeln('<!doctype html>');
     child.document.writeln(iframe.contentWindow.document.head.innerHTML);
     child.document.writeln(iframe.contentWindow.document.body.innerHTML);
     child.document.close();
     }-*/;
    // @formatter:on

}
