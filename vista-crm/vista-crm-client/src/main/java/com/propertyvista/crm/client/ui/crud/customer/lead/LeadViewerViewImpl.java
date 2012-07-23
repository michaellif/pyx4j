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
package com.propertyvista.crm.client.ui.crud.customer.lead;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog.Formatter;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.client.ui.crud.customer.lead.appointment.AppointmentListerView;
import com.propertyvista.crm.client.ui.crud.customer.lead.appointment.AppointmentListerViewImpl;
import com.propertyvista.crm.rpc.CrmSiteMap.Marketing;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lead.Lead.Status;
import com.propertyvista.shared.config.VistaFeatures;

public class LeadViewerViewImpl extends CrmViewerViewImplBase<Lead> implements LeadViewerView {

    private static final I18n i18n = I18n.get(LeadViewerViewImpl.class);

    private final Button convertAction;

    private final Button closeAction;

    private final AppointmentListerView appointmentLister = new AppointmentListerViewImpl();

    public LeadViewerViewImpl() {
        super(Marketing.Lead.class);

        convertAction = new Button(i18n.tr("Convert to Lease"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ((LeadViewerView.Presenter) getPresenter()).getInterestedUnits(new DefaultAsyncCallback<List<AptUnit>>() {
                    @Override
                    public void onSuccess(List<AptUnit> result) {
                        int i = result.size();
                        new EntitySelectorListDialog<AptUnit>(i18n.tr("Select Unit To Lease"), false, result, new Formatter<AptUnit>() {
                            @Override
                            public String format(AptUnit entity) {
                                return entity.building().getStringView() + ", " + i18n.tr("Unit") + " " + entity.getStringView();
                            }
                        }) {
                            @Override
                            public boolean onClickOk() {
                                ((LeadViewerView.Presenter) getPresenter()).convertToLease(getSelectedItems().get(0).getPrimaryKey());
                                return true;
                            }

                            @Override
                            public String defineWidth() {
                                return "40em";
                            }
                        }.show();
                    }
                });

            }
        });
        addHeaderToolbarTwoItem(convertAction);

        closeAction = new Button(i18n.tr("Close"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                MessageDialog.confirm(i18n.tr("Confirm"), i18n.tr("Do you really want to close the Lead?"), new Command() {
                    @Override
                    public void execute() {
                        ((LeadViewerView.Presenter) getPresenter()).close();
                    }
                });
            }
        });
        addHeaderToolbarTwoItem(closeAction.asWidget());

        // set main form here:
        setForm(new LeadForm(true));
    }

    @Override
    public void populate(Lead value) {
        super.populate(value);

        convertAction.setVisible(VistaFeatures.instance().leases() && value.status().getValue() != Status.closed && value.lease().isNull());
        closeAction.setVisible(value.status().getValue() != Status.closed);

        getEditButton().setVisible(value.status().getValue() != Lead.Status.closed);
    }

    @Override
    public void onLeaseConvertionSuccess() {
        MessageDialog.info(i18n.tr("Information"), i18n.tr("Conversion is succeeded!"));
        convertAction.setVisible(false);
    }

    @Override
    public boolean onConvertionFail(Throwable caught) {
        if (caught instanceof UserRuntimeException) {
            MessageDialog.info(i18n.tr("Conversion failed"), caught.getMessage());
        } else {
            MessageDialog.error(i18n.tr("Error"), i18n.tr("Conversion failed due to system error"));
        }
        return true;
    }

    @Override
    public AppointmentListerView getAppointmentsListerView() {
        return appointmentLister;
    }
}