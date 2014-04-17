/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-04-11
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.shared.ui.util.editors;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;

import com.propertyvista.domain.person.Name;
import com.propertyvista.portal.shared.ui.AccessoryEntityForm;
import com.propertyvista.portal.shared.ui.util.decorators.FieldDecoratorBuilder;

public class NameEditor extends AccessoryEntityForm<Name> {

    private final CComponent<Name> nameComp;

    public NameEditor() {
        this(null);
    }

    public NameEditor(String customViewLabel) {
        super(Name.class);

        nameComp = new CEntityLabel<Name>();
        nameComp.asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);
        nameComp.setDecorator(new FieldDecoratorBuilder(200).customLabel(customViewLabel).build());

        adopt(nameComp);
    }

    /**
     * overwrite to supply real entity key for hyperlink
     */
    public Key getLinkKey() {
        return getValue().getPrimaryKey();
    }

    @Override
    protected IsWidget createContent() {
        BasicFlexFormPanel main = new BasicFlexFormPanel();
        int row = -1;

        main.setWidget(++row, 0, nameComp);
        main.setWidget(++row, 0, injectAndDecorate(proto().firstName(), 200));
        main.setWidget(++row, 0, injectAndDecorate(proto().lastName(), 200));
        main.setWidget(++row, 0, injectAndDecorate(proto().middleName(), 60));
        main.setWidget(++row, 0, injectAndDecorate(proto().namePrefix(), 80));
        main.setWidget(++row, 0, injectAndDecorate(proto().nameSuffix(), 60));

        calculateFieldsStatus();

        addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                if (event.isEventOfType(PropertyName.viewable)) {
                    calculateFieldsStatus();
                }
            }
        });

        return main;
    }

    @Override
    public void generateMockData() {
        get(proto().firstName()).setMockValue("Jane");
        get(proto().lastName()).setMockValue("Doe");
    }

    private void calculateFieldsStatus() {
        if (isViewable()) {
            nameComp.setVisible(true);

            get(proto().firstName()).setVisible(false);
            get(proto().lastName()).setVisible(false);
            get(proto().middleName()).setVisible(false);
            get(proto().namePrefix()).setVisible(false);
            get(proto().nameSuffix()).setVisible(false);
        } else {
            nameComp.setVisible(false);

            get(proto().firstName()).setVisible(true);
            get(proto().lastName()).setVisible(true);
            get(proto().middleName()).setVisible(true);
            get(proto().namePrefix()).setVisible(true);
            get(proto().nameSuffix()).setVisible(true);
        }
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        if (isViewable()) {
            nameComp.setValue(getValue());
        }
    }
}
