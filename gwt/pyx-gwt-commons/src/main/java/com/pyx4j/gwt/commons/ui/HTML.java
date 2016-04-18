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
 * Created on Apr 17, 2016
 * @author vlads
 */
package com.pyx4j.gwt.commons.ui;

import com.google.gwt.dom.client.Element;
import com.google.gwt.i18n.shared.DirectionEstimator;
import com.google.gwt.safehtml.shared.SafeHtml;

public class HTML extends com.google.gwt.user.client.ui.HTML implements HasStyle {

    public HTML() {
        super();
    }

    public HTML(Element element) {
        super(element);
    }

    public HTML(SafeHtml html, Direction dir) {
        super(html, dir);
    }

    public HTML(SafeHtml html, DirectionEstimator directionEstimator) {
        super(html, directionEstimator);
    }

    public HTML(SafeHtml html) {
        super(html);
    }

    public HTML(String html, boolean wordWrap) {
        super(html, wordWrap);
    }

    public HTML(String html, Direction dir) {
        super(html, dir);
    }

    public HTML(String html) {
        super(html);
    }

}
