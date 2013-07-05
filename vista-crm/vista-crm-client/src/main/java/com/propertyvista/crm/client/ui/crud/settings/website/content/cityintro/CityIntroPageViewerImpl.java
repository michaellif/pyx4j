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
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.settings.website.content.cityintro;

import com.pyx4j.commons.Key;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.domain.site.CityIntroPage;

public class CityIntroPageViewerImpl extends CrmViewerViewImplBase<CityIntroPage> implements CityIntroPageViewer {

    public CityIntroPageViewerImpl() {
        setForm(new CityIntroPageForm(this));
    }

    @Override
    public void viewPage(Key id) {
        ((CityIntroPageViewer.Presenter) getPresenter()).viewPage(id);
    }

    @Override
    public void newPage(Key parentId) {
        ((CityIntroPageViewer.Presenter) getPresenter()).editNew(parentId);
    }

}
