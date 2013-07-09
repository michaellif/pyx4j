/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 1, 2011
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.steps.apartment;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CNumberLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.editors.PetDataEditor;
import com.propertyvista.common.client.ui.components.editors.VehicleDataEditor;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemExtraData;
import com.propertyvista.domain.tenant.lease.extradata.Pet;
import com.propertyvista.domain.tenant.lease.extradata.Vehicle;

class FeatureExEditor extends CEntityDecoratableForm<BillableItem> {

    private static final I18n i18n = I18n.get(FeatureExEditor.class);

    private final SimplePanel extraDataPanel = new SimplePanel();

    public FeatureExEditor() {
        super(BillableItem.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();
        int row = -1;

        CLabel lb;
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().item().code().name(), lb = new CLabel()), 23).customLabel("").useLabelSemicolon(false)
                .build());
        lb.asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);

        CNumberLabel nl;
        main.setWidget(row, 1, new FormDecoratorBuilder(inject(proto().agreedPrice(), nl = new CNumberLabel()), 6).build());
        nl.setNumberFormat(proto().agreedPrice().getMeta().getFormat(), proto().agreedPrice().getMeta().useMessageFormat());
        nl.asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);

        main.setWidget(++row, 0, extraDataPanel);
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.getColumnFormatter().setWidth(0, "50%");
        main.getColumnFormatter().setWidth(1, "50%");

        return main;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (!getValue().item().isEmpty()) {
            CEntityForm editor = null;
            BillableItemExtraData extraData = getValue().extraData();

            // add extraData editor if necessary:
            if (ARCode.Type.features().contains(getValue().item().code().type().getValue())) {
                switch (getValue().item().code().type().getValue()) {
                case Parking:
                    editor = new VehicleDataEditor();
                    if (extraData.isNull()) {
                        extraData.set(EntityFactory.create(Vehicle.class));
                    }
                    break;
                case Pet:
                    editor = new PetDataEditor();
                    if (extraData.isNull()) {
                        extraData.set(EntityFactory.create(Pet.class));
                    }
                    break;
                }
            }

            if (editor != null) {
                unbind(proto().extraData());
                inject(proto().extraData(), editor);
                editor.populate(extraData.cast());
                extraDataPanel.setWidget(editor);
            }
        }
    }

}