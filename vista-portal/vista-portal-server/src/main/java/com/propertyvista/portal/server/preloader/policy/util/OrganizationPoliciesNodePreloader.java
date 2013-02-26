/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-26
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader.policy.util;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;

public class OrganizationPoliciesNodePreloader extends AbstractDataPreloader {

    @Override
    public String create() {
        Persistence.service().persist(EntityFactory.create(OrganizationPoliciesNode.class));
        return null;
    }

    @Override
    public String delete() {
        return null;
    }

}
