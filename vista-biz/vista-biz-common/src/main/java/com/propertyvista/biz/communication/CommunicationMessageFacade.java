/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 5, 2014
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.biz.communication;

import com.propertyvista.domain.communication.CommunicationGroup;
import com.propertyvista.domain.communication.CommunicationGroup.EndpointGroup;

public interface CommunicationMessageFacade {

    public CommunicationGroup getCommunicationGroupFromCache(EndpointGroup epType);
}
