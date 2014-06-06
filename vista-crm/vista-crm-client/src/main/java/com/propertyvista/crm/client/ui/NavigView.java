/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.crm.client.ui;

import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.site.client.IsView;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.shared.i18n.CompiledLocale;

public interface NavigView extends IsWidget, IsView {

    public interface NavigPresenter {

        boolean isAdminPlace();

        void getSatisfaction();

        void logout();

        void setLocale(CompiledLocale locale);

    }

    void setPresenter(NavigPresenter presenter);

    void select(AppPlace appPlace);

    void updateDashboards(Vector<DashboardMetadata> metadataList);

    void setAvailableLocales(List<CompiledLocale> localeList);

    void updateUserName(String name);
}