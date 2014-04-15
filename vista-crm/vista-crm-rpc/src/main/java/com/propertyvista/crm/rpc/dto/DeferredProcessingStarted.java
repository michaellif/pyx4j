/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 15, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto;

import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.shared.AbstractIFileBlob;

/**
 * As a result of Blob Upload a new DeferredProcess started. Its process should be monitored separately.
 * 
 */
public interface DeferredProcessingStarted extends AbstractIFileBlob {

    IPrimitive<String> deferredCorrelationId();

}
