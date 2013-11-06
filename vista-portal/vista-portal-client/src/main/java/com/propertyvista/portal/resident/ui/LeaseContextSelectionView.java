/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 24, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui;

import java.util.List;

import com.pyx4j.site.client.IsView;

import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.domain.dto.LeaseContextChoiceDTO;

/**
 * This view is used to resolve ambiguity when a customer with more than one lease logs in to a portal.
 */
public interface LeaseContextSelectionView extends IsView {

    interface Presenter {

        void populate();

        void setLeaseContext();

    }

    void setPresenter(Presenter presenter);

    void populate(List<LeaseContextChoiceDTO> leaseChoices);

    Lease getSelectedLeaseIdStub();

    void showMessage(String message);
}
