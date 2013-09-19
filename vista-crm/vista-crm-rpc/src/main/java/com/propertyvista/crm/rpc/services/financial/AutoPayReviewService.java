/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-09-09
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.services.financial;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.shared.IService;

import com.propertyvista.crm.rpc.dto.financial.autopayreview.PapReviewDTO;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.ReviewedPapsHolderDTO;
import com.propertyvista.domain.reports.AutoPayChangesReportMetadata;

public interface AutoPayReviewService extends IService {

    void getAutoPayReviews(AsyncCallback<Vector<PapReviewDTO>> callback, AutoPayChangesReportMetadata filterSettings);

    void accept(AsyncCallback<String> callback, ReviewedPapsHolderDTO acceptedReviews);

}
