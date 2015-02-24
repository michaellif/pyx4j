/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 22, 2014
 * @author stanp
 */
package com.propertyvista.crm.client.ui.crud.lease.eviction;

import com.google.gwt.user.client.Command;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.ActionPermission;
import com.pyx4j.widgets.client.Button.SecureMenuItem;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.services.legal.eviction.ac.ServiceN4;
import com.propertyvista.domain.eviction.EvictionCaseStatus;
import com.propertyvista.domain.eviction.EvictionStatusN4;
import com.propertyvista.domain.policy.policies.domain.EvictionFlowStep.EvictionStepType;
import com.propertyvista.dto.EvictionCaseDTO;

public class EvictionCaseViewerViewImpl extends CrmViewerViewImplBase<EvictionCaseDTO> implements EvictionCaseViewerView {

    private static final I18n i18n = I18n.get(EvictionCaseViewerViewImpl.class);

    private final SecureMenuItem issueN4Action;

    private final SecureMenuItem downloadN4Action;

    public EvictionCaseViewerViewImpl() {
        setForm(new EvictionCaseForm(this, true));

        // Issue N4
        addAction(issueN4Action = new SecureMenuItem(i18n.tr("Issue N4"), new Command() {
            @Override
            public void execute() {
                ((EvictionCaseViewerView.Presenter) getPresenter()).issueN4(getForm().getValue());
            }
        }, new ActionPermission(ServiceN4.class)));
        setActionVisible(issueN4Action, false);

        addAction(downloadN4Action = new SecureMenuItem(i18n.tr("Print Attachments"), new Command() {
            @Override
            public void execute() {
                ((EvictionCaseViewerView.Presenter) getPresenter()).downloadAttachments(getForm().getValue());
            }
        }, new ActionPermission(ServiceN4.class)));
        setActionVisible(downloadN4Action, false);
    }

    @Override
    public void populate(EvictionCaseDTO value) {
        super.populate(value);

        boolean canIssueN4 = canIssueN4(value);
        setActionVisible(issueN4Action, canIssueN4);
        setActionVisible(downloadN4Action, canIssueN4);
    }

    private boolean canIssueN4(EvictionCaseDTO evictionCase) {
        if (!evictionCase.closedOn().isNull()) {
            return false;
        }

        boolean hasArrears = false;
        for (EvictionCaseStatus status : evictionCase.history()) {
            if (EvictionStepType.N4.equals(status.evictionStep().stepType().getValue())) {
                EvictionStatusN4 statusN4 = status.<EvictionStatusN4> cast();
                hasArrears = !statusN4.leaseArrears().isEmpty();
                break;
            }
        }
        return hasArrears;
    }
}
