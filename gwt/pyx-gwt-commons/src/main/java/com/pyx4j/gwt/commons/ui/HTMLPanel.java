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

import com.google.gwt.safehtml.shared.SafeHtml;

public class HTMLPanel extends com.google.gwt.user.client.ui.HTMLPanel implements Panel, HasStyle {

    public HTMLPanel(String html) {
        super(html);
    }

    public HTMLPanel(SafeHtml safeHtml) {
        super(safeHtml);
    }

    public HTMLPanel(String tag, String html) {
        super(tag, html);
    }

}
