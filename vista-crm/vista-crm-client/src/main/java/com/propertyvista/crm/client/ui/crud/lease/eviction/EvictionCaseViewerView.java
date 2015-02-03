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

import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeViewerView;

import com.propertyvista.dto.EvictionCaseDTO;

public interface EvictionCaseViewerView extends IPrimeViewerView<EvictionCaseDTO> {

    interface Presenter extends IPrimeViewerView.IPrimeViewerPresenter {

        void issueN4(EvictionCaseDTO evictionCase);

        void downloadAttachments(EvictionCaseDTO evictionCase);
    }

}
