/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 14, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.domain.mail;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.shared.AbstractOutgoingMailQueue;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;

@AbstractEntity
@Table(namespace = VistaNamespace.operationsNamespace)
@Inheritance(strategy = Inheritance.InheritanceStrategy.SINGLE_TABLE)
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface OutgoingMailQueue extends AbstractOutgoingMailQueue {

}
