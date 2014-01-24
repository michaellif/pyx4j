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
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;

import com.propertyvista.domain.person.Name;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class NameEditor extends CEntityForm<Name> {

    private final CComponent<Name> nameComp;

    private final String customViewLabel;

    public NameEditor() {
        this(null);
    }

    public NameEditor(String customViewLabel) {
        super(Name.class);
        this.customViewLabel = customViewLabel;

        nameComp = new CEntityLabel<Name>();
        nameComp.asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);
    }

    /**
     * overwrite to supply real entity key for hyperlink
     */
    public Key getLinkKey() {
        return getValue().getPrimaryKey();
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel main = new BasicFlexFormPanel();
        int row = -1;

        main.setWidget(0, 0, 1, new FormWidgetDecoratorBuilder(nameComp, 200).customLabel(customViewLabel).build());

        main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().firstName()), 200).build());
        main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().lastName()), 200).build());
        main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().middleName()), 60).build());
        main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().namePrefix()), 60).build());
        main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().nameSuffix()), 60).build());
//        main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().maidenName()), 200).build());

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

    private void calculateFieldsStatus() {
        if (isViewable()) {
            nameComp.setVisible(true);

            get(proto().firstName()).setVisible(false);
            get(proto().lastName()).setVisible(false);
            get(proto().namePrefix()).setVisible(false);
            get(proto().nameSuffix()).setVisible(false);
        } else {
            nameComp.setVisible(false);

            get(proto().firstName()).setVisible(true);
            get(proto().lastName()).setVisible(true);
            get(proto().namePrefix()).setVisible(true);
            get(proto().nameSuffix()).setVisible(true);
        }
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (!isEditable()) {
            nameComp.setValue(getValue());
        }
    }
}
