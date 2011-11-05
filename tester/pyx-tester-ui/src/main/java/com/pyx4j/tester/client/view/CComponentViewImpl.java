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
package com.pyx4j.tester.client.view;

import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.tester.client.domain.CComponentProperties;

public class CComponentViewImpl extends ScrollPanel implements CComponentView {

    private ConsolePresenter presenter;

    private final CComponentViewForm form;

    public CComponentViewImpl() {
        setSize("100%", "100%");

        form = new CComponentViewForm();
        form.initContent();

        setWidget(form);
    }

    @Override
    public void setPresenter(final ConsolePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void show(CComponent<?, ?> cComponent) {
        if (cComponent == null) {
            form.populate(null);
            form.setVisible(false);
        } else {
            form.setVisible(true);
            CComponentProperties properties = EntityFactory.create(CComponentProperties.class);

            properties.title().setValue(cComponent.getTitle());

            if (cComponent instanceof CComponent) {
                CComponent editableComponent = cComponent;

                properties.componentValue().setValue(editableComponent.getValue() == null ? "null" : editableComponent.getValue().toString());
                properties.title().setValue(editableComponent.getTitle());

                properties.mandatory().setValue(editableComponent.isMandatory());

                properties.enabled().setValue(editableComponent.isEnabled());
                properties.editable().setValue(editableComponent.isEditable());
                properties.visible().setValue(editableComponent.isVisible());
                properties.valid().setValue(editableComponent.isValid());
                properties.toolTip().setValue(editableComponent.getTooltip());
            }

            form.populate(properties);
        }
    }
}
