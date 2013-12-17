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

    public static native void preview(String html) /*-{
                                                   var child = $wnd.open();
                                                   child.document.writeln('<!doctype html>');
                                                   child.document.writeln(html);
                                                   child.document.close();
                                                   }-*/;

}
