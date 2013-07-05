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
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.settings.website.content.pages;

import com.pyx4j.commons.Key;
import com.pyx4j.site.client.ui.prime.form.IViewer;

import com.propertyvista.domain.site.PageDescriptor;

public interface PageViewer extends IViewer<PageDescriptor> {

    interface Presenter extends IViewer.Presenter {

        void viewChild(Key id);

        void editNew(Key parentid);
    }

    void viewChild(Key id);

    void newChild(Key parentid);
}
