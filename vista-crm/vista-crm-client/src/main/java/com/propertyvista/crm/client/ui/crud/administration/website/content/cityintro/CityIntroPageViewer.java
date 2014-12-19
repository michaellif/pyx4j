/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 11, 2012
 * @author stanp
 */
package com.propertyvista.crm.client.ui.crud.administration.website.content.cityintro;

import com.pyx4j.commons.Key;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeViewerView;

import com.propertyvista.domain.site.CityIntroPage;

public interface CityIntroPageViewer extends IPrimeViewerView<CityIntroPage> {

    interface Presenter extends IPrimeViewerView.IPrimeViewerPresenter {

        void viewPage(Key id);

        void editNew(Key parentId);
    }

    void viewPage(Key id);

    void newPage(Key parentId);
}
