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

import java.util.Arrays;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.ria.client.crud.EntitySearchCriteriaPart;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CForm.LabelAlignment;
import com.pyx4j.ria.client.view.AbstractView;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.tabpanelnew.Tab;

public class SearchCriteriaView extends AbstractView {

    private final CForm otherForm;

    public SearchCriteriaView() {
        super(RiaEntityImageBundle.INSTANCE.searchCriteria(), false);

        setTabTitle("Find");

        HorizontalPanel toolbarPanel = new HorizontalPanel();
        toolbarPanel.setWidth("100%");

        Toolbar toolbar = new Toolbar();
        toolbarPanel.add(toolbar);

        setToolbarPane(toolbarPanel);

        CComboBox<String> sortBy = new CComboBox<String>("Sort by");
        sortBy.setOptions(Arrays.asList(new String[] { "Name", "Phone" }));

        CComboBox<String> perPage = new CComboBox<String>("Results per page");
        perPage.setOptions(Arrays.asList(new String[] { "10", "25", "50", "100" }));

        otherForm = new CForm(LabelAlignment.TOP);
        CComponent<?>[][] components = new CComponent<?>[][] {

        { sortBy },

        { perPage },

        };
        otherForm.setComponents(components);

    }

    class Toolbar extends com.pyx4j.ria.client.Toolbar {

        public Toolbar() {

            addItem(RiaEntityImageBundle.INSTANCE.searchRun(), "Search", new Command() {

                @Override
                public void execute() {
                    // TODO Auto-generated method stub

                }
            });

            addItem(new Anchor("Clear"));
        }
    }

    public void setSearchCriteriaPart(EntitySearchCriteriaPart<?> part) {
        addPage(new Tab(new ScrollPanel(part.initNativeComponent()), "Criteria", null, false));
        addPage(new Tab(new ScrollPanel((Widget) otherForm.initNativeComponent()), "Properties", null, false));
    }
}
