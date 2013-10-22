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

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.essentials.client.ConfirmCommand;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog.Formatter;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.client.ui.crud.customer.lead.appointment.AppointmentListerView;
import com.propertyvista.crm.client.ui.crud.customer.lead.appointment.AppointmentListerViewImpl;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lead.Lead.ConvertToLeaseAppraisal;
import com.propertyvista.domain.tenant.lead.Lead.Status;
import com.propertyvista.shared.config.VistaFeatures;

public class LeadViewerViewImpl extends CrmViewerViewImplBase<Lead> implements LeadViewerView {

    private static final I18n i18n = I18n.get(LeadViewerViewImpl.class);

    private final MenuItem convertAction;

    private final MenuItem closeAction;

    private final AppointmentListerView appointmentLister = new AppointmentListerViewImpl();

    public LeadViewerViewImpl() {
        convertAction = new MenuItem(i18n.tr("Convert to Lease"), new Command() {
            @Override
            public void execute() {
                ((LeadViewerView.Presenter) getPresenter()).convertToLeaseApprisal(new DefaultAsyncCallback<ConvertToLeaseAppraisal>() {
                    @Override
                    public void onSuccess(ConvertToLeaseAppraisal result) {
                        switch (result) {
                        case Positive:
                            ((LeadViewerView.Presenter) getPresenter()).getInterestedUnits(new DefaultAsyncCallback<List<AptUnit>>() {
                                @Override
                                public void onSuccess(List<AptUnit> result) {
                                    if (!result.isEmpty()) {
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
                                            public int defineWidth() {
                                                return 600;
                                            }
                                        }.show();
                                    } else {
                                        MessageDialog.info(ConvertToLeaseAppraisal.NoUnits.toString());
                                    }
                                }
                            });
                            break;

                        default:
                            MessageDialog.info(result.toString());
                            break;
                        }
                    }
                });
            }
        });
        if (!VistaFeatures.instance().yardiIntegration()) {
            addAction(convertAction);
        }

        closeAction = new MenuItem(i18n.tr("Close"), new ConfirmCommand(i18n.tr("Confirm"), i18n.tr("Do you really want to close the Lead?"), new Command() {
            @Override
            public void execute() {
                ((LeadViewerView.Presenter) getPresenter()).close();
            }
        }));

        addAction(closeAction);

        // set main form here:
        setForm(new LeadForm(this));
    }

    @Override
    public void reset() {
        setActionVisible(convertAction, false);
        setActionVisible(closeAction, false);
        super.reset();
    }

    @Override
    public void populate(Lead value) {
        super.populate(value);

        setActionVisible(convertAction, VistaFeatures.instance().leases() && value.status().getValue() != Status.closed && value.lease().isNull());
        setActionVisible(closeAction, value.status().getValue() != Status.closed);

        setEditingVisible(value.status().getValue() != Lead.Status.closed);
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