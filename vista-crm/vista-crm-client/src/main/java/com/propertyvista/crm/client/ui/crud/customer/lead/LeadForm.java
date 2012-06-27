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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.crud.misc.CEntitySelectorHyperlink;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorTableDialog;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.lease.common.CLeaseHyperlink;
import com.propertyvista.crm.rpc.services.selections.SelectFloorplanListService;
import com.propertyvista.domain.policy.policies.IdAssignmentPolicy;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lease.Lease;

public class LeadForm extends CrmEntityForm<Lead> {

    private static final I18n i18n = I18n.get(LeadForm.class);

    public LeadForm() {
        this(false);
    }

    public LeadForm(boolean viewMode) {
        super(Lead.class, viewMode);
    }

    @Override
    public void createTabs() {

        Tab tab = addTab(createGuestsTab(i18n.tr("Guests")));
        selectTab(tab);

        addTab(createDetailsTab(i18n.tr("Details")));

        tab = addTab(createAppointmentsTab(), i18n.tr("Appointments"));
        setTabEnabled(tab, !isEditable());

    }

    private FormFlexPanel createGuestsTab(String title) {
        FormFlexPanel main = new FormFlexPanel(title);

        int row = -1;
        main.setWidget(++row, 0, inject(proto().guests(), new GuestFolder(isEditable())));

        main.setBR(++row, 0, 1);
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().refSource()), 20).build());

        main.setBR(++row, 0, 1);
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().comments()), 50).build());

        return main;
    }

    private FormFlexPanel createDetailsTab(String title) {
        FormFlexPanel main = new FormFlexPanel(title);

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leadId()), 20).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseType()), 20).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().moveInDate()), 9).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseTerm()), 9).build());
        if (isEditable()) {
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().building(), new CEntityLabel<Building>()), 20).build());
        } else {
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().building()), 20).build());
        }
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().floorplan(), new CEntitySelectorHyperlink<Floorplan>() {
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

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().agent()), 20).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().createDate()), 9).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().status()), 10).build());

        if (isEditable()) {
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().lease(), new CEntityLabel<Lease>()), 40).build());
        } else {
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().lease(), new CLeaseHyperlink()), 40).build());
            main.getFlexCellFormatter().setColSpan(row, 0, 2);
        }

        row = -1;
        main.setH4(++row, 1, 1, i18n.tr("Preferred Appointment Times") + ":");
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().appointmentDate1()), 9).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().appointmentTime1()), 9).build());
        main.setBR(++row, 1, 1);
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().appointmentDate2()), 9).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().appointmentTime2()), 9).build());

        main.getColumnFormatter().setWidth(0, "50%");
        main.getColumnFormatter().setWidth(1, "50%");

        get(proto().status()).setViewable(true);
        get(proto().createDate()).setViewable(true);

        return main;
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();

        get(proto().lease()).setVisible(!getValue().lease().isNull());

        get(proto().leadId()).setViewable(false);
        ClientPolicyManager.obtainEffectivePolicy(ClientPolicyManager.getOrganizationPoliciesNode(), IdAssignmentPolicy.class,
                new DefaultAsyncCallback<IdAssignmentPolicy>() {
                    @Override
                    public void onSuccess(IdAssignmentPolicy result) {
                        IdAssignmentItem targetItem = null;
                        for (IdAssignmentItem item : result.itmes()) {
                            if (item.target().getValue() == IdTarget.lead) {
                                targetItem = item;
                                break;
                            }
                        }

                        if (targetItem != null) {
                            switch (targetItem.type().getValue()) {
                            case generatedAlphaNumeric:
                            case generatedNumber:
                                get(proto().leadId()).setViewable(true);
                                break;
                            case userEditable:
                                get(proto().leadId()).setViewable(false);
                                break;
                            case userAssigned:
                                get(proto().leadId()).setViewable(getValue().getPrimaryKey() != null);
                                break;
                            }
                        }
                    }
                });
    }

    private Widget createAppointmentsTab() {
        if (!isEditable()) {
            return ((LeadViewerView) getParentView()).getAppointmentsListerView().asWidget();
        }
        return new HTML(); // just stub - not necessary for editing mode!..
    }

    private class FloorplanSelectorDialogDialog extends EntitySelectorTableDialog<Floorplan> {

        public FloorplanSelectorDialogDialog() {
            super(Floorplan.class, false, new ArrayList<Floorplan>(1), i18n.tr("Building/Floorplan Selection"));
            setWidth("600px");
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
        protected AbstractListService<Floorplan> getSelectService() {
            return GWT.<AbstractListService<Floorplan>> create(SelectFloorplanListService.class);
        }
    }
}