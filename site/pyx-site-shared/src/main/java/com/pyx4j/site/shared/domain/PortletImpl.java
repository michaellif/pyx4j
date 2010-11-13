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
 * Created on 2010-11-12
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.site.shared.domain;

import java.util.Collection;
import java.util.Vector;

import com.pyx4j.site.shared.meta.NavigNode;

public class PortletImpl implements Portlet {

    private final String portletId;

    private final String caption;

    private final String styleName;

    private final String actionLabel;

    private final Class<? extends NavigNode> navigNode;

    private final String html;

    private Collection<String> inlineWidgetIds;

    public PortletImpl(String portletId, String caption, String html, String styleName, String actionLabel, Class<? extends NavigNode> navigNode) {
        this.portletId = portletId;
        this.caption = caption;
        this.styleName = styleName;
        this.html = html;
        this.actionLabel = actionLabel;
        this.navigNode = navigNode;
    }

    @Override
    public String portletId() {
        return portletId;
    }

    @Override
    public String caption() {
        return caption;
    }

    @Override
    public String styleName() {
        return styleName;
    }

    @Override
    public String actionLabel() {
        return actionLabel;
    }

    @Override
    public Class<? extends NavigNode> navigNode() {
        return navigNode;
    }

    @Override
    public String html() {
        return html;
    }

    @Override
    public Collection<String> inlineWidgetIds() {
        return inlineWidgetIds;
    }

    public void addInlineWidgetId(String inlineWidgetId) {
        if (this.inlineWidgetIds == null) {
            this.inlineWidgetIds = new Vector<String>();
        }
        this.inlineWidgetIds.add(inlineWidgetId);
    }

}
