/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 23, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.communication;

import java.util.List;

import com.pyx4j.site.client.IsView;

import com.propertyvista.portal.rpc.portal.resident.communication.CommunicationMessageDTO;

public interface CommunicationView extends IsView {

    interface CommunicationPresenter {
    }

    void populate(List<CommunicationMessageDTO> messages);
}
