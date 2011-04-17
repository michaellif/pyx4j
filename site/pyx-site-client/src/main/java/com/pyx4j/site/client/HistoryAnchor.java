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
 * Created on Feb 17, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.site.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;

/**
 * Fix for "Local anchor link causes reload in IE"
 * 
 * @see <a href="http://code.google.com/p/google-web-toolkit/issues/detail?id=2152">issue</a>
 * 
 */
public class HistoryAnchor extends Anchor implements ClickHandler {

    private final String targetHistoryToken;

    public HistoryAnchor(Element element, String targetHistoryToken) {
        super(element);
        this.targetHistoryToken = targetHistoryToken;
        this.addClickHandler(this);
    }

    @Override
    public void onClick(ClickEvent event) {
        AbstractSiteDispatcher.show(targetHistoryToken);
    }

}
