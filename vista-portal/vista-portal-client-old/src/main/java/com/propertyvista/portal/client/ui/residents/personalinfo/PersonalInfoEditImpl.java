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

import com.propertyvista.portal.client.ui.residents.EditImpl;
import com.propertyvista.portal.domain.dto.ResidentDTO;

public class PersonalInfoEditImpl extends EditImpl<ResidentDTO> implements PersonalInfoEdit {

    public PersonalInfoEditImpl() {
        super(new PersonalInfoForm());
    }
}
