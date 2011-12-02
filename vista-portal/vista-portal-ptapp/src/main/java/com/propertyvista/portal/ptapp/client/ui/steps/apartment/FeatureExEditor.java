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

import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CNumberLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.common.client.ui.components.editors.PetDataEditor;
import com.propertyvista.common.client.ui.components.editors.VehicleDataEditor;
import com.propertyvista.domain.financial.offering.ChargeItem;
import com.propertyvista.domain.financial.offering.extradata.Pet;
import com.propertyvista.domain.financial.offering.extradata.Vehicle;

class FeatureExEditor extends CEntityDecoratableEditor<ChargeItem> {

    private static final I18n i18n = I18n.get(FeatureExEditor.class);

    private final SimplePanel extraDataPanel = new SimplePanel();

    public FeatureExEditor() {
        super(ChargeItem.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();
        int row = -1;

        CLabel lb;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().item().type().name(), lb = new CLabel())).customLabel("").useLabelSemicolon(false).build());
        lb.asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);

        CNumberLabel nl;
        main.setWidget(row, 1, new DecoratorBuilder(inject(proto().originalPrice(), nl = new CNumberLabel()), 6).build());
        nl.setNumberFormat(proto().originalPrice().getMeta().getFormat(), proto().originalPrice().getMeta().useMessageFormat());

        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().adjustedPrice(), nl = new CNumberLabel()), 6).build());
        nl.setNumberFormat(proto().adjustedPrice().getMeta().getFormat(), proto().adjustedPrice().getMeta().useMessageFormat());
        nl.asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);

        main.setWidget(++row, 0, extraDataPanel);
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.getColumnFormatter().setWidth(0, "40%");
        main.getColumnFormatter().setWidth(1, "60%");

        return main;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void populate(ChargeItem value) {
        super.populate(value);

        CEntityEditor editor = null;
        switch (value.item().type().featureType().getValue()) {
        case parking:
            editor = new VehicleDataEditor(new VistaEditorsComponentFactory()) {
                @Override
                public CComponent<?, ?> create(IObject<?> member) {
                    return factory.create(member); // use own (editor) factory instead of parent (viewer) one!..
                }
            };

            if (value.extraData().isNull()) {
                value.extraData().set(EntityFactory.create(Vehicle.class));
            }
            break;
        case pet:
            editor = new PetDataEditor(new VistaEditorsComponentFactory()) {
                @Override
                public CComponent<?, ?> create(IObject<?> member) {
                    return factory.create(member); // use own (editor) factory instead of parent (viewer) one!..
                }
            };

            if (value.extraData().isNull()) {
                value.extraData().set(EntityFactory.create(Pet.class));
            }
            break;
        }

        if (editor != null) {
            this.inject(proto().extraData(), editor);
            editor.populate(value.extraData().cast());
            extraDataPanel.setWidget(editor);
        }
    }
}