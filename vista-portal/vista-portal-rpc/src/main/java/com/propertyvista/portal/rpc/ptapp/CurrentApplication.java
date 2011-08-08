/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 24, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.rpc.ptapp;

import java.io.Serializable;

import com.propertyvista.domain.tenant.ptapp.Application;
import com.propertyvista.portal.domain.ptapp.ApplicationProgress;

@SuppressWarnings("serial")
public class CurrentApplication implements Serializable {

    public Application application;

    public ApplicationProgress progress;

}
