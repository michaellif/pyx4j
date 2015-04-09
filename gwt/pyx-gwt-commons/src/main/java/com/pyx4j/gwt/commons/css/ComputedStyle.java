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
 * Created on May 7, 2014
 * @author michaellif
 */
package com.pyx4j.gwt.commons.css;

import com.google.gwt.dom.client.Element;

public class ComputedStyle {

    public static native String getStyleProperty(Element el, String prop) /*-{ 
                                                                          var computedStyle;
                                                                          if (document.defaultView && document.defaultView.getComputedStyle) { // standard (includes ie9)
                                                                          computedStyle = document.defaultView.getComputedStyle(el, null)[prop];
                                                                          
                                                                          } else if (el.currentStyle) { // IE older
                                                                          computedStyle = el.currentStyle[prop];
                                                                          
                                                                          } else { // inline style
                                                                          computedStyle = el.style[prop];
                                                                          }
                                                                          return computedStyle;
                                                                          }-*/;

}