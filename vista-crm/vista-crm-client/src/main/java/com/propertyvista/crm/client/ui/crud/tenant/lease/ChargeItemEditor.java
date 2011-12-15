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
package com.propertyvista.crm.client.ui.crud.tenant.lease;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CNumberLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.VistaTableFolder;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.common.client.ui.components.editors.PetDataEditor;
import com.propertyvista.common.client.ui.components.editors.VehicleDataEditor;
import com.propertyvista.domain.financial.offering.ChargeItem;
import com.propertyvista.domain.financial.offering.ChargeItemAdjustment;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.extradata.Pet;
import com.propertyvista.domain.financial.offering.extradata.Vehicle;

class ChargeItemEditor extends CEntityDecoratableEditor<ChargeItem> {

    private static final I18n i18n = I18n.get(ChargeItemEditor.class);

    private final SimplePanel extraDataPanel = new SimplePanel();

    private final FormFlexPanel adjustmentPanel = new FormFlexPanel();

    public ChargeItemEditor() {
        super(ChargeItem.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();
        int row = -1;

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().item().type().name(), new CLabel())).customLabel("").useLabelSemicolon(false).build());
        get(proto().item().type().name()).asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);

        main.setWidget(row, 1, new DecoratorBuilder(inject(proto().agreedPrice()), 6).customLabel(i18n.tr("Agreed Price")).build());
        get(proto().agreedPrice()).asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);

        CNumberLabel nl;
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().originalPrice(), nl = new CNumberLabel()), 6).build());
        nl.setNumberFormat(proto().originalPrice().getMeta().getFormat(), proto().originalPrice().getMeta().useMessageFormat());

        main.setWidget(++row, 0, extraDataPanel);
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.setWidget(++row, 0, adjustmentPanel);
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.getColumnFormatter().setWidth(0, "50%");
        main.getColumnFormatter().setWidth(1, "50%");

        adjustmentPanel.setH3(0, 0, 1, i18n.tr("Adjustments"));
        adjustmentPanel.setWidget(1, 0, inject(proto().adjustments(), new ChargeItemAdjustmentFolder()));

        return main;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void populate(ChargeItem value) {
        super.populate(value);
        if (!value.item().isEmpty()) {
            if (value.item().type().featureType().getValue() == Feature.Type.utility) {
                // TODO - how to ?
//                setRemovable(false);
            }

            if (!isEditable()) {
                adjustmentPanel.setVisible(!value.adjustments().isEmpty());
            }

            CEntityEditor editor = null;
            // add extraData editor if necessary:
            switch (value.item().type().type().getValue()) {
            case feature:
                switch (value.item().type().featureType().getValue()) {
                case parking:
                    editor = new VehicleDataEditor();
                    if (value.extraData().isNull()) {
                        value.extraData().set(EntityFactory.create(Vehicle.class));
                    }
                    break;
                case pet:
                    editor = new PetDataEditor();
                    if (value.extraData().isNull()) {
                        value.extraData().set(EntityFactory.create(Pet.class));
                    }
                    break;
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

    @Override
    public void addValidations() {
//        get(proto().agreedPrice()).addValueValidator(new EditableValueValidator<Double>() {
//            @Override
//            public ValidationFailure isValid(CComponent<Double, ?> component, Double value) {
//                Double originalPrice = getValue().originalPrice().getValue();
//                return ((value > originalPrice * 0.5 && originalPrice < value * 1.5) ? null : new ValidationFailure(i18n
//                        .tr("The price should not be differ +-50% of original price")));
//            }
//        });

        super.addValidations();
    }

    private class ChargeItemAdjustmentFolder extends VistaTableFolder<ChargeItemAdjustment> {

        public ChargeItemAdjustmentFolder() {
            super(ChargeItemAdjustment.class, ChargeItemEditor.this.isEditable());
        }

        @Override
        public List<EntityFolderColumnDescriptor> columns() {
            ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
            columns.add(new EntityFolderColumnDescriptor(proto().type(), "8em"));
            columns.add(new EntityFolderColumnDescriptor(proto().chargeType(), "9em"));
            columns.add(new EntityFolderColumnDescriptor(proto().termType(), "8em"));
            columns.add(new EntityFolderColumnDescriptor(proto().value(), "5em"));
            return columns;
        }
    }
}