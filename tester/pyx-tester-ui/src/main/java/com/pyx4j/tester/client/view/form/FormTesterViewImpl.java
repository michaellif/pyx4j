/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Nov 2, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.tester.client.view.form;

import java.util.HashMap;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.tester.client.domain.test.EntityI;
import com.pyx4j.tester.client.view.form.folder.EntityIForm;
import com.pyx4j.widgets.client.Button;

public class FormTesterViewImpl extends ScrollPanel implements FormTesterView {

    private final HTML lbl;

    private Presenter presenter;

    private final Button populateButton;

    private final Button cleanButton;

    private final Button repopulateButton;

    private final SimplePanel formPanel;

    private final HashMap<TestFormType, CEntityEditor<EntityI>> map = new HashMap<TestFormType, CEntityEditor<EntityI>>();

    private CEntityEditor<EntityI> currentForm;

    public FormTesterViewImpl() {
        setSize("100%", "100%");

        DockPanel container = new DockPanel();
        container.setSize("100%", "100%");

        lbl = new HTML();
        container.add(lbl, DockPanel.NORTH);

        HorizontalPanel buttonPanel = new HorizontalPanel();
        //buttonPanel.setSize("100%", "100%");

        populateButton = new Button("Populate");
//        //container.add(populateButton, DockPanel.SOUTH);
        populateButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.onClickPopulate();

            }
        });

        buttonPanel.add(populateButton);

        cleanButton = new Button("Clean");
        cleanButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.onClickClean();

            }
        });
        buttonPanel.add(cleanButton);

        repopulateButton = new Button("Repopulate");
        repopulateButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.onClickRepopulate();
            }
        });
        buttonPanel.add(repopulateButton);

        container.add(buttonPanel, DockPanel.NORTH);

        formPanel = new SimplePanel();
        formPanel.setSize("100%", "100%");
        container.add(formPanel, DockPanel.NORTH);

        setWidget(container);

        //EntityIFormWithoutLists mainForm = new EntityIFormWithoutLists();
        //mainForm.initContent();

        //setWidget(mainForm);

        //mainForm.populate(DomainFactory.createEntityI());

    }

    @Override
    public void installForm(TestFormType formType) {
        lbl.setHTML(formType.name());

        if (!map.containsKey(formType)) {
            if (TestFormType.FormBasic == formType) {
                EntityIFormWithoutLists form = new EntityIFormWithoutLists();
                form.initContent();
                currentForm = form;
                map.put(formType, form);
            } else if (TestFormType.FormVisibility == formType) {
                EntityIIFormWithVisibilityChange form = new EntityIIFormWithVisibilityChange();
                form.initContent();
                //map.put(formType, form);
            } else if (TestFormType.FolderLayout == formType) {
                EntityIForm form = new EntityIForm();
                form.initContent();
                map.put(formType, form);
            }
        }

        formPanel.setWidget(map.get(formType));
        formPanel.setSize("100%", "100%");
    }

    @Override
    public void populate(EntityI entity) {
        currentForm.populate(entity);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;

    }

    @Override
    public void clean() {
        currentForm.populateNew();

    }
}
