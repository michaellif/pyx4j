/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 17, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.communication;

import java.util.Set;

import com.propertyvista.domain.communication.EmailTemplateType;

public interface CommunicationTemplateFacade {

    public Set<String> getTemplateDataObjectSelection(EmailTemplateType template);

}
