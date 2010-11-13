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
import java.util.List;
import java.util.Vector;

import com.google.gwt.resources.client.ExternalTextResource;

public class PageDataImpl implements PageData {

    private final String html;

    private final String help;

    private ExternalTextResource helpResource;

    private List<String> inlineWidgetIds;

    private List<Portlet> leftPortlets;

    private List<Portlet> rightPortlets;

    public PageDataImpl(String html, String helpHtml) {
        this.html = html;
        this.help = helpHtml;
    }

    @Override
    public String html() {
        return html;
    }

    @Override
    public String help() {
        return help;
    }

    @Override
    public ExternalTextResource helpResource() {
        return helpResource;
    }

    public void setHelpResource(ExternalTextResource helpResource) {
        this.helpResource = helpResource;
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

    @Override
    public List<Portlet> leftPortlets() {
        return leftPortlets;
    }

    @Override
    public List<Portlet> rightPortlets() {
        return rightPortlets;
    }

    public void addLeftPortlet(Portlet portlet) {
        if (this.leftPortlets == null) {
            this.leftPortlets = new Vector<Portlet>();
        }
        this.leftPortlets.add(portlet);
    }

    public void addRightPortlet(Portlet portlet) {
        if (this.rightPortlets == null) {
            this.rightPortlets = new Vector<Portlet>();
        }
        this.rightPortlets.add(portlet);
    }
}
