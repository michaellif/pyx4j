/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 18, 2011
 * @author Vlad
 */
/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 9, 2011
 * @author vadims
 */
package com.propertyvista.crm.client.ui.crud.administration.website.content.pages;

import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeEditorView;

import com.propertyvista.domain.site.PageDescriptor;

public interface PageEditor extends IPrimeEditorView<PageDescriptor> {

    interface Presenter extends IPrimeEditorView.IPrimeEditorPresenter {
        /**
         * used as url parameter name in Site/PageViewerActivities and PageEditorActivity
         */
        public static final String URL_PARAM_PAGE_PARENT = "pp";
    }
}
