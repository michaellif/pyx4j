/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.settings.financial.tax;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.financial.tax.Tax;

public class TaxEditorForm extends CrmEntityForm<Tax> {

    public TaxEditorForm() {
        this(false);
    }

    public TaxEditorForm(boolean viewMode) {
        super(Tax.class, viewMode);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name()), 25).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().authority()), 25).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().rate()), 7).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().compound()), 5).build());

        return new ScrollPanel(main);
    }
}