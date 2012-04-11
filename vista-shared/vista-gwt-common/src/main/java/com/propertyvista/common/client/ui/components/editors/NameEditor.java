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
package com.propertyvista.common.client.ui.components.editors;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.domain.person.Name;

public class NameEditor extends CEntityDecoratableEditor<Name> {

    private final CLabel viewComp = new CLabel();

    private final String customViewLabel;

    public NameEditor() {
        this(null);
    }

    public NameEditor(String customViewLabel) {
        super(Name.class);
        this.customViewLabel = customViewLabel;
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        if (isEditable()) {
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().namePrefix()), 5).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().firstName()), 15).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().middleName()), 5).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().lastName()), 25).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().maidenName()), 25).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().nameSuffix()), 5).build());
        } else {
            main.setWidget(++row, 0, new DecoratorBuilder(viewComp, 25).customLabel(customViewLabel).useLabelSemicolon(customViewLabel != null).build());
            viewComp.asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);
        }

        return main;
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();

        if (!isEditable()) {
            viewComp.setValue(getValue().getStringView());
        }
    }
}
