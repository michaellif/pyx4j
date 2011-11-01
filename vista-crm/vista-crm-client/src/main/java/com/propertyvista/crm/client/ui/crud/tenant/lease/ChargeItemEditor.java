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
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.entity.client.ui.OptionsFilter;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CNumberLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.common.client.ui.VistaTableFolder;
import com.propertyvista.common.client.ui.components.editors.CEntityDecoratableEditor;
import com.propertyvista.common.client.ui.validators.ProvinceContryFilters;
import com.propertyvista.domain.financial.offering.ChargeItem;
import com.propertyvista.domain.financial.offering.ChargeItemAdjustment;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.extradata.Pet;
import com.propertyvista.domain.financial.offering.extradata.Vehicle;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;

class ChargeItemEditor extends CEntityDecoratableEditor<ChargeItem> {

    private final SimplePanel extraDataPanel = new SimplePanel();

    private final FormFlexPanel adjustmentPanel = new FormFlexPanel();

    private final ChargeItemFolder chargeItemFolder;

    public ChargeItemEditor(ChargeItemFolder chargeItemFolder) {
        super(ChargeItem.class);
        this.chargeItemFolder = chargeItemFolder;
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();
        int row = -1;

        CLabel lb;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().item().type().name(), lb = new CLabel())).build());
        lb.asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);

        CNumberLabel nl;
        main.setWidget(row, 1, new DecoratorBuilder(inject(proto().price(), nl = new CNumberLabel()), 6).build());
        nl.setNumberFormat(proto().price().getMeta().getFormat());

        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().adjustedPrice(), nl = new CNumberLabel()), 6).build());
        nl.setNumberFormat(proto().adjustedPrice().getMeta().getFormat());
        nl.asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);

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

        if (value.item().type().featureType().getValue() == Feature.Type.utility) {
            // TODO - how to ?
//                    setRemovable(false);
        }

        if (!isEditable()) {
            adjustmentPanel.setVisible(!value.adjustments().isEmpty());
            get(proto().adjustedPrice()).setVisible(!value.adjustments().isEmpty());
        }

        CEntityEditor editor = null;
        switch (value.item().type().featureType().getValue()) {
        case parking:
            editor = new CEntityDecoratableEditor<Vehicle>(Vehicle.class) {
                @Override
                public IsWidget createContent() {
                    FormFlexPanel panel = new FormFlexPanel();

                    int row = -1;
                    panel.setH3(++row, 0, 2, i18n.tr("Vehicle data"));

                    panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().year()), 5).build());
                    panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().make()), 10).build());
                    panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().model()), 10).build());

                    row = 0; // skip header
                    panel.setWidget(++row, 1, new DecoratorBuilder(inject(proto().plateNumber()), 10).build());
                    CEditableComponent<Country, ?> country;
                    panel.setWidget(++row, 1, new DecoratorBuilder(country = (CEditableComponent<Country, ?>) inject(proto().country()), 13).build());
                    CEditableComponent<Province, ?> province;
                    panel.setWidget(++row, 1, new DecoratorBuilder(province = (CEditableComponent<Province, ?>) inject(proto().province()), 17).build());

                    ProvinceContryFilters.attachFilters(province, country, new OptionsFilter<Province>() {
                        @Override
                        public boolean acceptOption(Province entity) {
                            if (getValue() == null) {
                                return true;
                            } else {
                                Country country = (Country) getValue().getMember(proto().country().getPath());
                                return country.isNull() || EqualsHelper.equals(entity.country().name(), country.name());
                            }
                        }
                    });

                    panel.getColumnFormatter().setWidth(0, "50%");
                    panel.getColumnFormatter().setWidth(1, "50%");

                    return panel;
                }
            };

            if (value.extraData().isNull()) {
                value.extraData().set(EntityFactory.create(Vehicle.class));
            }
            break;
        case pet:
            editor = new CEntityDecoratableEditor<Pet>(Pet.class) {
                @Override
                public IsWidget createContent() {
                    FormFlexPanel panel = new FormFlexPanel();

                    int row = -1;
                    panel.setH3(++row, 0, 2, i18n.tr("Pet data"));

                    panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name()), 15).build());
                    panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().color()), 15).build());
                    panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().breed()), 15).build());

                    row = 0; // skip header
                    panel.setWidget(++row, 1, new DecoratorBuilder(inject(proto().weight()), 4).build());
                    panel.setWidget(++row, 1, new DecoratorBuilder(inject(proto().weightUnit()), 4).build());
                    panel.setWidget(++row, 1, new DecoratorBuilder(inject(proto().birthDate()), 8.2).build());

                    panel.getColumnFormatter().setWidth(0, "50%");
                    panel.getColumnFormatter().setWidth(1, "50%");

                    return panel;
                }
            };

            if (value.extraData().isNull()) {
                value.extraData().set(EntityFactory.create(Pet.class));
            }
            break;
        }

        if (editor != null) {
            editor.onBound(this);
            editor.populate(value.extraData().cast());
            extraDataPanel.setWidget(editor);
        }
    }

    @Override
    public void addValidations() {
        super.addValidations();
        addValueChangeHandler(new ValueChangeHandler<ChargeItem>() {
            @Override
            public void onValueChange(ValueChangeEvent<ChargeItem> event) {
                calculateAdjustments();
            }
        });
    }

    private void calculateAdjustments() {
        if (chargeItemFolder.parent.isEditable()) {
            LeaseEditorView.Presenter presenter = (LeaseEditorView.Presenter) ((LeaseEditorView) chargeItemFolder.parent.getParentView()).getPresenter();
            presenter.calculateChargeItemAdjustments(new AsyncCallback<Double>() {
                @Override
                public void onSuccess(Double result) {
                    get(proto().adjustedPrice()).setValue(result);
                }

                @Override
                public void onFailure(Throwable caught) {
                    // TODO Auto-generated method stub
                }
            }, getValue());
        }
    }

    private class ChargeItemAdjustmentFolder extends VistaTableFolder<ChargeItemAdjustment> {

        public ChargeItemAdjustmentFolder() {
            super(ChargeItemAdjustment.class, chargeItemFolder.isEditable());
        }

        @Override
        protected List<EntityFolderColumnDescriptor> columns() {
            ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
            columns.add(new EntityFolderColumnDescriptor(proto().type(), "8em"));
            columns.add(new EntityFolderColumnDescriptor(proto().chargeType(), "9em"));
            columns.add(new EntityFolderColumnDescriptor(proto().termType(), "8em"));
            columns.add(new EntityFolderColumnDescriptor(proto().value(), "5em"));
            return columns;
        }
    }
}