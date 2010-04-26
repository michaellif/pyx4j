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
 * Created on Apr 26, 2010
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.gwt.commons;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.UIObject;

/**
 * <> Generic printing class can be used to print the Window it self, DOM.Elements,
 * UIObjects (Widgets) and plain HTML
 * 
 * Usage: You must insert this iframe in your host page: < id="__printingFrame"
 * style="width:0;height:0;border:0">< /iframe>
 * 
 * Window: Print.it();
 * 
 * Objects/HTML: Print.it(RootPanel.get("myId")); Print.it(DOM.getElementById("myId"));
 * Print.it("Just <b>Print.it()</b>!");
 * 
 * Objects/HTML using styles: Print.it("< rel="'StyleSheet'" type="'text/css'"
 * media="'paper'" href="'/paperStyle.css'">", RootPanel.get('myId'));
 * Print.it("< type="'text/css'" media="'paper'"> .newPage { page-break-after: always; } <
 * /style>",
 * 
 * "Hi< class="'newPage'">< /p>By"); < /pre>
 */

public class Print {

    public static native void it() /*-{
                                   $wnd.print();
                                   }-*/;

    public static native void buildFrame(String html) /*-{
                                                      var frame = $doc.getElementById('__printingFrame');
                                                      if (!frame) {
                                                      $wnd.alert("Error: Can't find printing frame.");
                                                      return;
                                                      }
                                                      var doc = frame.contentWindow.document;
                                                      doc.open();
                                                      doc.write(html);
                                                      doc.close();

                                                      }-*/;

    public static native void printFrame() /*-{
                                           var frame = $doc.getElementById('__printingFrame');
                                           frame = frame.contentWindow;
                                           frame.focus();
                                           frame.print();
                                           }-*/;

    public static class PrintFrame implements Command {
        public void execute() {
            printFrame();
        }
    }

    public static PrintFrame printFrameCommmand = new PrintFrame();

    public static void it(String html) {
        try {
            buildFrame(html);
            DeferredCommand.addCommand(printFrameCommmand);
        } catch (Throwable exc) {
            exc.printStackTrace();
        }
    }

    public static void it(UIObject obj) {
        it("", obj.toString());
    }

    public static void it(Element element) {
        it("", element.toString());
    }

    public static void it(String style, String it) {
        System.out.println(it);
        it("<html><head>" + style + "</head>\n<body>" + it + "</body></html>");
    }

    public static void it(String style, UIObject obj) {
        it(style, obj.getElement().toString());
    }

    public static void it(String style, Element element) {
        it(style, element.toString());
    }
}
