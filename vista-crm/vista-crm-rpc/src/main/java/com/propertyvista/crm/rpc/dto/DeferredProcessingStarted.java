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
 */
package com.propertyvista.crm.rpc.dto;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.shared.AbstractIFileBlob;
import com.pyx4j.i18n.annotations.I18n;

/**
 * As a result of Blob Upload a new DeferredProcess started. Its process should be monitored separately.
 * 
 */
@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface DeferredProcessingStarted extends AbstractIFileBlob {

    IPrimitive<String> deferredCorrelationId();

}
