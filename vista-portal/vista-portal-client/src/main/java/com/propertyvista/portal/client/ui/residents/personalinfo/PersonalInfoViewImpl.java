/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 15, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.personalinfo;

import com.propertyvista.common.client.events.UserMessageEvent.UserMessageType;
import com.propertyvista.portal.client.ui.residents.BasicViewImpl;
import com.propertyvista.portal.domain.dto.ResidentDTO;

public class PersonalInfoViewImpl extends BasicViewImpl<ResidentDTO> implements PersonalInfoView {

    public PersonalInfoViewImpl() {
        super(new PersonalInfoForm());
    }

    @Override
    public void showError(String msg) {
        messagePanel.setMessage(msg, UserMessageType.ERROR);
    }

    @Override
    public void showNote(String msg) {
        messagePanel.setMessage(msg, UserMessageType.INFO);
    }
}
