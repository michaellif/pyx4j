/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-06-27
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.reports.autopayreviewer;

import java.util.List;

import com.google.gwt.view.client.Range;

import com.pyx4j.site.client.ui.prime.IPrimePane;

import com.propertyvista.crm.rpc.dto.financial.autopayreview.PapReviewDTO;
import com.propertyvista.domain.reports.AutoPayChangesReportMetadata;

public interface AutoPayReviewView extends IPrimePane {

    interface Presenter extends IPrimePane.Presenter {

        void acceptAll();

        void acceptMarked();

        void onRangeChanged();

    }

    void setLoading(boolean isLoading);

    void setPresenter(Presenter presenter);

    void setRowData(int start, int total, List<PapReviewDTO> values);

    Range getVisibleRange();

    List<PapReviewDTO> getMarkedPapReviews();

    AutoPayChangesReportMetadata getAutoPayFilterSettings();

}
