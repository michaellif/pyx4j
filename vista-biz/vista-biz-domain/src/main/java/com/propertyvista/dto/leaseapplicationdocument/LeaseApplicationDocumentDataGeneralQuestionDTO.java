/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 21, 2014
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.dto.leaseapplicationdocument;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

@Transient
public interface LeaseApplicationDocumentDataGeneralQuestionDTO extends IEntity {

    IPrimitive<String> question();

    IPrimitive<Boolean> answerYes();

    IPrimitive<Boolean> answerNo();
}
