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
package com.propertyvista.crm.client.ui.crud.settings.content.site;

import com.pyx4j.commons.Key;
import com.pyx4j.site.client.ui.crud.form.IViewer;

import com.propertyvista.crm.rpc.CrmCrudAppPlace;
import com.propertyvista.dto.SiteDescriptorDTO;

public interface SiteViewer extends IViewer<SiteDescriptorDTO> {

    interface Presenter extends IViewer.Presenter {

        void viewChild(Key id);

        void viewChild(Key id, Class<? extends CrmCrudAppPlace> openPlaceClass);

        void editNew(Key parentid);

        void editNew(Key parentid, Class<? extends CrmCrudAppPlace> openPlaceClass);
    }

    void viewChild(Key id);

    void viewChild(Key id, Class<? extends CrmCrudAppPlace> openPlaceClass);

    void newChild(Key parentid);
}
