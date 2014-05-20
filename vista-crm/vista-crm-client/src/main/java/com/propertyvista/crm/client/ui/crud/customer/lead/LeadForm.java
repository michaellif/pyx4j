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
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.DualColumnForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.activity.EntitySelectorTableVisorController;
import com.pyx4j.site.client.ui.IPane;
import com.pyx4j.site.client.ui.IShowable;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.client.ui.prime.misc.CEntityCrudHyperlink;
import com.pyx4j.site.client.ui.prime.misc.CEntitySelectorHyperlink;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.policy.ClientPolicyManager;
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

        Tab tab = addTab(createGuestsTab(), i18n.tr("Guests"));
        selectTab(tab);

        addTab(createDetailsTab(), i18n.tr("Details"));

        tab = addTab(createAppointmentsTab(), i18n.tr("Appointments"));
        setTabEnabled(tab, !isEditable());

    }

    @Override
    public void addValidations() {
        super.addValidations();

        get(proto().guests()).addComponentValidator(new AbstractComponentValidator<List<Guest>>() {

            @Override
            public BasicValidationError isValid() {
                Boolean hasContact = false;
                if (getComponent().getValue() != null) {
                    for (Guest g : getComponent().getValue()) {
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
                return hasContact ? null : new BasicValidationError(getComponent(), i18n.tr("No contact information (email and/or phone #) has been provided"));
            }
        });
    }

    private IsWidget createGuestsTab() {
        DualColumnForm formPanel = new DualColumnForm(this);

        formPanel.append(Location.Dual, proto().guests(), new GuestFolder(isEditable()));

        formPanel.br();

        formPanel.append(Location.Left, proto().refSource()).decorate().componentWidth(180);
        formPanel.append(Location.Dual, proto().comments()).decorate();

        return formPanel;
    }

    private IsWidget createDetailsTab() {
        DualColumnForm formPanel = new DualColumnForm(this);

        formPanel.append(Location.Left, proto().leadId()).decorate().componentWidth(220);
        formPanel.append(Location.Left, proto().leaseType()).decorate().componentWidth(220);
        formPanel.append(Location.Left, proto().moveInDate()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().leaseTerm()).decorate().componentWidth(120);
        if (isEditable()) {
            formPanel.append(Location.Right, proto().floorplan().building(), new CEntityLabel<Building>()).decorate().componentWidth(220);
        } else {
            formPanel
                    .append(Location.Right, proto().floorplan().building(),
                            new CEntityCrudHyperlink<Building>(AppPlaceEntityMapper.resolvePlace(Building.class))).decorate().componentWidth(220);
        }
        formPanel.append(Location.Right, proto().floorplan(), new CEntitySelectorHyperlink<Floorplan>() {
            @Override
            protected AppPlace getTargetPlace() {
                return AppPlaceEntityMapper.resolvePlace(Floorplan.class).formViewerPlace(getValue().getPrimaryKey());
            }

            @Override
            protected IShowable getSelectorDialog() {
                return new FloorplanSelectorDialogDialog(getParentView());
            }
        }).decorate().componentWidth(220);

        formPanel.append(Location.Right, proto().agent()).decorate().componentWidth(220);
        formPanel.append(Location.Right, proto().createDate()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().status()).decorate().componentWidth(120);

        if (isEditable()) {
            formPanel.append(Location.Dual, proto().lease(), new CEntityLabel<Lease>()).decorate();
        } else {
            formPanel.append(Location.Dual, proto().lease(), new CEntityCrudHyperlink<Lease>(AppPlaceEntityMapper.resolvePlace(Lease.class))).decorate();

        }

        formPanel.h4(i18n.tr("Preferred Appointment Times") + ":");
        formPanel.append(Location.Left, proto().appointmentDate1()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().appointmentTime1()).decorate().componentWidth(120);

        formPanel.append(Location.Right, proto().appointmentDate2()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().appointmentTime2()).decorate().componentWidth(120);

        get(proto().status()).setEditable(false);
        get(proto().createDate()).setEditable(false);

        return formPanel;
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

    private class FloorplanSelectorDialogDialog extends EntitySelectorTableVisorController<Floorplan> {

        public FloorplanSelectorDialogDialog(IPane parentView) {
            super(parentView, Floorplan.class, false, Collections.<Floorplan> emptyList(), i18n.tr("Building/Floorplan Selection"));
        }

        @Override
        public void onClickOk() {
            ((LeadEditorView.Presenter) ((LeadEditorView) getParentView()).getPresenter()).setSelectedFloorplan(getSelectedItems().get(0));
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
                    new MemberColumnDescriptor.Builder(proto().building().info().address().addressLine1(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().building().info().address().addressLine2(), false).build(),
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