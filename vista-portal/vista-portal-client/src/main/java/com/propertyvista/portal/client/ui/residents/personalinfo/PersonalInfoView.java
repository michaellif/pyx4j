/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 14, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.personalinfo;

import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.portal.domain.dto.ResidentDTO;

public interface PersonalInfoView extends IsWidget {
    void setPresenter(Presenter presenter);

    void populate(ResidentDTO personalInfo);

    public void showError(String msg);

    public void showNote(String msg);

    interface Presenter {
        public void save(ResidentDTO personalInfo);
    }
}
