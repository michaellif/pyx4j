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
package com.propertyvista.crm.client.ui.crud.settings.content;

import com.pyx4j.commons.Key;
import com.pyx4j.site.client.ui.crud.IViewerView;

import com.propertyvista.dto.SiteDescriptorDTO;

public interface SiteViewer extends IViewerView<SiteDescriptorDTO> {

    interface Presenter extends IViewerView.Presenter {

        void editNew(Key parentid);
    }

    void editNew(Key parentid);
}
