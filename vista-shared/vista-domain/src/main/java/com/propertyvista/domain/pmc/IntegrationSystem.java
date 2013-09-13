/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 12, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.pmc;

import com.pyx4j.commons.Key;

/**
 * TODO in future we may have different systems defined as entity, right now we have only yardi and internal
 * 
 * YArdi identified as PK of PmcYardiCredential
 * Internal as -1
 */

public interface IntegrationSystem {

    public static final Key internal = new Key(-1);

}
