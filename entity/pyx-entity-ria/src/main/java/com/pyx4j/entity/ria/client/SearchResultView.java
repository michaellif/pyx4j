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
 * Created on Apr 28, 2009
 * @author michaellif
 * @version $Id: TestView.java 7492 2010-11-13 17:57:45Z michaellif $
 */
package com.pyx4j.entity.ria.client;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.entity.ria.client.crud.EntityListPart;
import com.pyx4j.ria.client.view.AbstractView;
import com.pyx4j.widgets.client.tabpanel.Tab;

public class SearchResultView extends AbstractView {

    public SearchResultView() {
        super();

        setTabTitle("Search");
        setTabImage(RiaEntityImageBundle.INSTANCE.searchResult());

        HorizontalPanel toolbarPanel = new HorizontalPanel();
        toolbarPanel.setWidth("100%");

        Toolbar toolbar = new Toolbar();
        toolbarPanel.add(toolbar);

        setToolbarPane(toolbarPanel);
    }

    class Toolbar extends com.pyx4j.ria.client.Toolbar {

        public Toolbar() {

            addItem(RiaEntityImageBundle.INSTANCE.next(), null, "Next", true);

            HTML items = new HTML("1-8");
            items.getElement().getStyle().setProperty("verticalAlign", "middle");

            addItem(items, true);

            addItem(RiaEntityImageBundle.INSTANCE.previous(), null, "Previous", true);

        }
    }

    public void setEntityListPart(EntityListPart<?> part) {
        addPage(new Tab(new ScrollPanel(part), "List", null));
    }

}
