/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.customer.lead.showing;

import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorTableDialog;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.client.ui.prime.misc.CEntityCrudHyperlink;
import com.pyx4j.site.client.ui.prime.misc.CEntitySelectorHyperlink;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.components.boxes.UnitSelectorDialog;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.dto.tenant.ShowingDTO;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.tenant.lead.Showing;
import com.propertyvista.domain.tenant.lead.Showing.Result;

public class ShowingForm extends CrmEntityForm<ShowingDTO> {

    private static final I18n i18n = I18n.get(ShowingForm.class);

    public ShowingForm(IForm<ShowingDTO> view) {
        super(ShowingDTO.class, view);

        FormFlexPanel content = new FormFlexPanel(i18n.tr("General"));

        int row = -1;

        content.setH3(++row, 0, 2, i18n.tr("Desired:"));

        if (isEditable()) {
            content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().building(), new CEntityLabel<Building>()), 20).build());
        } else {
            content.setWidget(++row, 0,
                    new FormDecoratorBuilder(inject(proto().building(), new CEntityCrudHyperlink<Building>(AppPlaceEntityMapper.resolvePlace(Building.class))),
                            20).build());
        }
        if (isEditable()) {
            content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().floorplan(), new CEntityLabel<Floorplan>()), 20).build());
        } else {
            content.setWidget(
                    ++row,
                    0,
                    new FormDecoratorBuilder(inject(proto().floorplan(),
                            new CEntityCrudHyperlink<Floorplan>(AppPlaceEntityMapper.resolvePlace(Floorplan.class))), 20).build());
        }

        content.setH3(++row, 0, 2, i18n.tr("Suggested:"));

        if (isEditable()) {
            content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().unit().building(), new CEntityLabel<Building>()), 20).build());
        } else {
            content.setWidget(
                    ++row,
                    0,
                    new FormDecoratorBuilder(inject(proto().unit().building(),
                            new CEntityCrudHyperlink<Building>(AppPlaceEntityMapper.resolvePlace(Building.class))), 20).build());
        }
        if (isEditable()) {
            content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().unit().floorplan(), new CEntityLabel<Floorplan>()), 20).build());
        } else {
            content.setWidget(
                    ++row,
                    0,
                    new FormDecoratorBuilder(inject(proto().unit().floorplan(),
                            new CEntityCrudHyperlink<Floorplan>(AppPlaceEntityMapper.resolvePlace(Floorplan.class))), 20).build());
        }

        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().unit(), new CEntitySelectorHyperlink<AptUnit>() {
            @Override
            protected AppPlace getTargetPlace() {
                return AppPlaceEntityMapper.resolvePlace(AptUnit.class, getValue().getPrimaryKey());
            }

            @Override
            protected EntitySelectorTableDialog<AptUnit> getSelectorDialog() {
                return new UnitSelectorDialog() {
                    @Override
                    public boolean onClickOk() {
                        if (!getSelectedItems().isEmpty()) {
                            ((ShowingEditorView.Presenter) ((ShowingEditorView) getParentView()).getPresenter()).setSelectedUnit(getSelectedItems().get(0));
                        }
                        return !getSelectedItems().isEmpty();
                    }

                    @Override
                    protected void setFilters(List<Criterion> filters) {
                        // Allow/disable unit selection from the same building:   
//                        filters.add(PropertyCriterion.eq(proto().building(), ShowingForm.this.getValue().building()));

                        filters.add(PropertyCriterion.eq(proto().unitOccupancySegments().$().status(), AptUnitOccupancySegment.Status.available));
                        filters.add(PropertyCriterion.eq(proto().unitOccupancySegments().$().dateTo(), new LogicalDate(1100, 0, 1)));
                        if (!ShowingForm.this.getValue().moveInDate().isNull()) {
                            filters.add(PropertyCriterion.le(proto().unitOccupancySegments().$().dateFrom(), ShowingForm.this.getValue().moveInDate()
                                    .getValue()));
                        } else {
                            filters.add(PropertyCriterion.le(proto().unitOccupancySegments().$().dateFrom(), ClientContext.getServerDate()));
                        }

                        // and finalized current Product only:
                        filters.add(PropertyCriterion.isNotNull(proto().productItems().$().product().fromDate()));
                        filters.add(PropertyCriterion.isNull(proto().productItems().$().product().toDate()));

                        super.setFilters(filters);
                    }
                };
            }
        }), 20).build());

        row = 3;
        content.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().status()), 12).build());
        content.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().result()), 12).build());
        content.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().reason()), 12).build());

        // tweak UI:
        get(proto().status()).setViewable(true);
        get(proto().result()).addValueChangeHandler(new ValueChangeHandler<Showing.Result>() {
            @Override
            public void onValueChange(ValueChangeEvent<Result> event) {
                get(proto().reason()).setVisible(Showing.Result.notInterested.equals(event.getValue()));
            }
        });

        Tab tab = addTab(content);
        selectTab(tab);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().reason()).setVisible(getValue().result().getValue() == Showing.Result.notInterested);

        if (isEditable()) {
            get(proto().unit()).setEditable(getValue().status().getValue() != Showing.Status.seen);

            get(proto().result()).setEditable(getValue().status().getValue() != Showing.Status.seen);
            get(proto().reason()).setEditable(getValue().status().getValue() != Showing.Status.seen);
        } else {
            get(proto().result()).setVisible(!getValue().result().isNull());
        }
    }
}
