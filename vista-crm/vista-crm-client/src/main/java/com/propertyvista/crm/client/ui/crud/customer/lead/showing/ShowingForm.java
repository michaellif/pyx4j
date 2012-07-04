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

import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.crud.misc.CEntityCrudHyperlink;
import com.pyx4j.site.client.ui.crud.misc.CEntitySelectorHyperlink;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorTableDialog;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.crm.client.ui.components.boxes.UnitSelectorDialog;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.dto.tenant.ShowingDTO;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.domain.tenant.lead.Showing;
import com.propertyvista.domain.tenant.lead.Showing.Result;

public class ShowingForm extends CrmEntityForm<ShowingDTO> {

    private static final I18n i18n = I18n.get(ShowingForm.class);

    public ShowingForm() {
        this(false);
    }

    public ShowingForm(boolean viewMode) {
        super(ShowingDTO.class, viewMode);
    }

    @Override
    public void createTabs() {
        FormFlexPanel content = new FormFlexPanel();

        int row = -1;

        if (isEditable()) {
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().building(), new CEntityLabel<Building>()), 20).build());
        } else {
            content.setWidget(++row, 0,
                    new DecoratorBuilder(inject(proto().building(), new CEntityCrudHyperlink<Building>(AppPlaceEntityMapper.resolvePlace(Building.class))), 20)
                            .build());
        }
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().unit(), new CEntitySelectorHyperlink<AptUnit>() {
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

                        Province province = ShowingForm.this.getValue().province();
                        String city = ShowingForm.this.getValue().city().getValue();

                        filters.add(PropertyCriterion.eq(proto().building().info().address().province(), province));
                        filters.add(PropertyCriterion.eq(proto().building().info().address().city(), city));

                        super.setFilters(filters);
                    }
                };
            }

        }), 20).build());

        row = -1;
        content.setWidget(++row, 1, new DecoratorBuilder(inject(proto().status()), 12).build());
        content.setWidget(++row, 1, new DecoratorBuilder(inject(proto().result()), 12).build());
        content.setWidget(++row, 1, new DecoratorBuilder(inject(proto().reason()), 12).build());

        // tweak UI:
        get(proto().result()).addValueChangeHandler(new ValueChangeHandler<Showing.Result>() {
            @Override
            public void onValueChange(ValueChangeEvent<Result> event) {
                get(proto().reason()).setVisible(Showing.Result.notInterested.equals(event.getValue()));
            }
        });

        content.getColumnFormatter().setWidth(0, "50%");
        content.getColumnFormatter().setWidth(1, "50%");

        Tab tab = addTab(content, i18n.tr("General"));
        selectTab(tab);
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();

        get(proto().reason()).setVisible(Showing.Result.notInterested.equals(getValue().result().getValue()));
    }
}
