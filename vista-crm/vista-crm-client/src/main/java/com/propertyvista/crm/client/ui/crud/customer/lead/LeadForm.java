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
package com.propertyvista.crm.client.ui.crud.customer.lead;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorTableDialog;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.client.ui.prime.misc.CEntityCrudHyperlink;
import com.pyx4j.site.client.ui.prime.misc.CEntitySelectorHyperlink;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.services.selections.SelectFloorplanListService;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lead.Guest;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lease.Lease;

public class LeadForm extends CrmEntityForm<Lead> {

    private static final I18n i18n = I18n.get(LeadForm.class);

    public LeadForm(IForm<Lead> view) {
        super(Lead.class, view);

        Tab tab = addTab(createGuestsTab(i18n.tr("Guests")));
        selectTab(tab);

        addTab(createDetailsTab(i18n.tr("Details")));

        tab = addTab(createAppointmentsTab(), i18n.tr("Appointments"));
        setTabEnabled(tab, !isEditable());

    }

    @Override
    public void addValidations() {
        super.addValidations();

        get(proto().guests()).addValueValidator(new EditableValueValidator<List<Guest>>() {





            @Override
            public ValidationError isValid(CComponent<List<Guest>> component, List<Guest> value) {
                Boolean hasContact = false;
                if (value != null) {
                    for (Guest g : value) {
                        //@formatter:off
                        if (!g.person().email().isNull() || 
                            !g.person().homePhone().isNull() || 
                            !g.person().workPhone().isNull() || 
                            !g.person().mobilePhone().isNull()) {
                        //@formatter:on
                            hasContact = true;
                            break;
                        }
                    }
                }
                return hasContact ? null : new ValidationError(component, i18n.tr("No contact information (email and/or phone #) has been provided"));
            }
        });
    }

    private TwoColumnFlexFormPanel createGuestsTab(String title) {
        TwoColumnFlexFormPanel flexPanel = new TwoColumnFlexFormPanel(title);

        int row = -1;
        flexPanel.setWidget(++row, 0, 2, inject(proto().guests(), new GuestFolder(isEditable())));

        flexPanel.setBR(++row, 0, 2);

        flexPanel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().refSource()), 15, true).build());
        flexPanel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().comments()), 55, true).build());

        return flexPanel;
    }

    private TwoColumnFlexFormPanel createDetailsTab(String title) {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel(title);

        int row = -1;
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().leadId()), 20).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().leaseType()), 20).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().moveInDate()), 9).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().leaseTerm()), 9).build());
        if (isEditable()) {
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().floorplan().building(), new CEntityLabel<Building>()), 20).build());
        } else {
            main.setWidget(
                    ++row,
                    0,
                    new FormDecoratorBuilder(inject(proto().floorplan().building(),
                            new CEntityCrudHyperlink<Building>(AppPlaceEntityMapper.resolvePlace(Building.class))), 20).build());
        }
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().floorplan(), new CEntitySelectorHyperlink<Floorplan>() {
            @Override
            protected AppPlace getTargetPlace() {
                return AppPlaceEntityMapper.resolvePlace(Floorplan.class).formViewerPlace(getValue().getPrimaryKey());
            }

            @Override
            protected EntitySelectorTableDialog<Floorplan> getSelectorDialog() {
                return new FloorplanSelectorDialogDialog() {
                    @Override
                    public boolean onClickOk() {
                        if (!getSelectedItems().isEmpty()) {
                            ((LeadEditorView.Presenter) ((LeadEditorView) getParentView()).getPresenter()).setSelectedFloorplan(getSelectedItems().get(0));
                            return true;
                        } else {
                            return false;
                        }
                    }

                };

            }
        }), 20).build());

        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().agent()), 20).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().createDate()), 9).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().status()), 10).build());

        if (isEditable()) {
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().lease(), new CEntityLabel<Lease>()), 40).build());
        } else {
            main.setWidget(++row, 0,
                    new FormDecoratorBuilder(inject(proto().lease(), new CEntityCrudHyperlink<Lease>(AppPlaceEntityMapper.resolvePlace(Lease.class))), 40)
                            .build());
            main.getFlexCellFormatter().setColSpan(row, 0, 2);
        }

        row = -1;
        main.setH4(++row, 1, 1, i18n.tr("Preferred Appointment Times") + ":");
        main.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().appointmentDate1()), 9).build());
        main.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().appointmentTime1()), 9).build());
        main.setBR(++row, 1, 1);
        main.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().appointmentDate2()), 9).build());
        main.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().appointmentTime2()), 9).build());

        get(proto().status()).setEditable(false);
        get(proto().createDate()).setEditable(false);

        return main;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().lease()).setVisible(!getValue().lease().isNull());

        if (isEditable()) {
            ClientPolicyManager.setIdComponentEditabilityByPolicy(IdTarget.lead, get(proto().leadId()), getValue().getPrimaryKey());
        }
    }

    private Widget createAppointmentsTab() {
        if (!isEditable()) {
            return ((LeadViewerView) getParentView()).getAppointmentsListerView().asWidget();
        }
        return new HTML(); // just stub - not necessary for editing mode!..
    }

    private class FloorplanSelectorDialogDialog extends EntitySelectorTableDialog<Floorplan> {

        public FloorplanSelectorDialogDialog() {
            super(Floorplan.class, false, Collections.<Floorplan> emptyList(), i18n.tr("Building/Floorplan Selection"));
            setDialogPixelWidth(600);

        }

        @Override
        public boolean onClickOk() {
            return false;
        }

        @Override
        protected List<ColumnDescriptor> defineColumnDescriptors() {
            return Arrays.asList(//@formatter:off
                    // building columns
                    new MemberColumnDescriptor.Builder(proto().building().propertyCode(), true).build(),
                    new MemberColumnDescriptor.Builder(proto().building().complex(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().building().propertyManager(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().building().info().name(), true).title(i18n.tr("Building")).build(),
                    new MemberColumnDescriptor.Builder(proto().building().info().type(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().building().info().shape(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().building().info().address().streetNumber(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().building().info().address().streetNumberSuffix(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().building().info().address().streetName(), true).build(),
                    new MemberColumnDescriptor.Builder(proto().building().info().address().streetType(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().building().info().address().streetDirection(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().building().info().address().city(), true).build(),
                    new MemberColumnDescriptor.Builder(proto().building().info().address().province(), true).build(),
                    new MemberColumnDescriptor.Builder(proto().building().info().address().country(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().building().info().totalStoreys(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().building().info().residentialStoreys(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().building().info().structureType(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().building().info().structureBuildYear(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().building().info().constructionType(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().building().info().foundationType(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().building().info().floorType(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().building().info().landArea(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().building().info().waterSupply(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().building().info().centralAir(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().building().info().centralHeat(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().building().contacts().website(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().building().financial().dateAcquired(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().building().financial().purchasePrice(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().building().financial().marketPrice(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().building().financial().lastAppraisalDate(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().building().financial().lastAppraisalValue(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().building().financial().currency().name(), false).title(proto().building().financial().currency())
                            .build(),
                    new MemberColumnDescriptor.Builder(proto().building().marketing().name(), false).title(i18n.tr("Building Marketing Name")).build(),

                    // floorplan columns
                    new MemberColumnDescriptor.Builder(proto().name(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().marketingName(), true).build(),
                    new MemberColumnDescriptor.Builder(proto().floorCount(), true).build(),
                    new MemberColumnDescriptor.Builder(proto().bedrooms(), true).build(),
                    new MemberColumnDescriptor.Builder(proto().dens(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().bathrooms(), true).build()
            );//@formatter:on
        }

        @Override
        public List<Sort> getDefaultSorting() {
            return Arrays.asList(new Sort(proto().building().propertyCode(), false), new Sort(proto().marketingName(), false));
        }

        @Override
        protected AbstractListService<Floorplan> getSelectService() {
            return GWT.<AbstractListService<Floorplan>> create(SelectFloorplanListService.class);
        }
    }
}