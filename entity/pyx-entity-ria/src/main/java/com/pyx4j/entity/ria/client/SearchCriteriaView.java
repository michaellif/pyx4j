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

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.entity.ria.client.crud.EntitySearchCriteriaPart;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.ria.client.view.AbstractView;
import com.pyx4j.widgets.client.tabpanel.Tab;

public class SearchCriteriaView extends AbstractView {

    public SearchCriteriaView() {
        super();

        setTabTitle("Find");
        setTabImage(RiaEntityImageBundle.INSTANCE.searchCriteria());

        HorizontalPanel toolbarPanel = new HorizontalPanel();
        toolbarPanel.setWidth("100%");

        Toolbar toolbar = new Toolbar();
        toolbarPanel.add(toolbar);

        setToolbarPane(toolbarPanel);
    }

    class Toolbar extends com.pyx4j.ria.client.Toolbar {

        public Toolbar() {

            addItem(RiaEntityImageBundle.INSTANCE.searchRun(), "Search", new Command() {

                @Override
                public void execute() {
                    // TODO Auto-generated method stub

                }
            });

        }
    }

    public void setSearchCriteriaPart(EntitySearchCriteriaPart<? extends IEntity> searchCriteriaPart) {
        addPage(new Tab(searchCriteriaPart, "page 1", null));
    }
}
