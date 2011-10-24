/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-10-22
 * @author artyom
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.vacancyreport;

import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;

import com.propertyvista.domain.dashboard.gadgets.UnitVacancyStatus;

public interface UnitStatusListView extends IsWidget {
    // currently this is the buildings are the only criterion that is can be applied,
    // but future implementations might include more (i.e. displaying only vacant units or notice units, or even vacant/rented)
    public interface UnitStatusListViewFilteringCriteria {
        Set<String> getBuildingsFilteringCriteria();

        LogicalDate getFrom();

        LogicalDate getTo();
    }

    // TODO optional performance optimization:
    //  if the list doesn't request to display all of the columns, in particular transient ones that are
    //  computed on server, maybe it's worth to notify the server that these computations are not required.
    public interface Presenter {

        void populateUnitStatusList();

        void nextUnitStatusListPage();

        void prevUnitStatusListPage();

    }

    /**
     * Bind presenter to the view.
     * 
     * @param presenter
     */
    void setPresenter(Presenter presenter);

    // TODO do I really need 'hasMoreData' if I have totalRows?
    void setPageData(List<UnitVacancyStatus> data, int pageNumber, int totalRows, boolean hasMoreData);

    int getPageNumber();

    int getPageSize();

    UnitStatusListViewFilteringCriteria getUnitStatusListFilterCriteria();

    List<Sort> getUnitStatusListSortingCriteria();

    /**
     * @return properties of the {@link UnitVacancyStatus} that UI was allowed to show.
     */
    List<Path> getVisibleProperties();

    /**
     * @return <code>true</code> if the widget is visible, <code>false</code> otherwise.
     */
    boolean isEnabled();

    void reportError(Throwable error);
}