/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-10-23
 * @author artyom
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.vacancyreport;

import java.util.Set;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReportSummaryDTO;

// TODO this is a basis for future (i. e. responding on clicks on various summary parts), currently there's no really need for that
public interface SummaryView extends IsWidget {
    public interface SummaryFilteringCriteria {
        /**
         * @return property codes of the buildings that are to be shown
         */
        Set<String> getBuildingsFilteringCriteria();

        LogicalDate getFrom();

        LogicalDate getTo();
    }

    public interface Presenter {
        void populateSummary();
    }

    void setPresenter(Presenter presenter);

    /**
     * Fill the view with data.
     * 
     * @param summary
     */
    void populateSummary(UnitVacancyReportSummaryDTO summary);

    /**
     * 
     * @return summary filtering criteria or <code>null</code> if there no was no criteria defined, in the later case presenter should ingnor popluateSummary()
     *         requests
     */
    SummaryFilteringCriteria getSummaryFilteringCriteria();

    boolean isEnabled();

    void reportError(Throwable error);
}
