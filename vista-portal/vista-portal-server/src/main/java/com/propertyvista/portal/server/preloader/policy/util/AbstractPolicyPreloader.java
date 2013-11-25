/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader.policy.util;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.biz.preloader.AbstractVistaDataPreloader;
import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.framework.Policy;
import com.propertyvista.domain.policy.framework.PolicyNode;

public abstract class AbstractPolicyPreloader<P extends Policy> extends AbstractVistaDataPreloader {

    private final Class<P> policyClass;

    private final List<Class<? extends IEntity>> whatToDelete;

    private PolicyNode topNode;

    protected AbstractPolicyPreloader(Class<P> policyClass) {
        this(policyClass, new ArrayList<Class<? extends IEntity>>(1));
    }

    protected AbstractPolicyPreloader(Class<P> policyClass, List<Class<? extends IEntity>> helperDomain) {
        this.policyClass = policyClass;
        this.whatToDelete = new ArrayList<Class<? extends IEntity>>(helperDomain.size() + 1);
        this.whatToDelete.add(policyClass);
        this.whatToDelete.addAll(helperDomain);
        this.topNode = null;
    }

    protected abstract P createPolicy(StringBuilder log);

    public void setTopNode(PolicyNode topNode) {
        this.topNode = topNode;
    }

    public PolicyNode getTopNode() {
        if (topNode != null) {
            return topNode;
        } else {
            return Persistence.service().retrieve(EntityQueryCriteria.create(OrganizationPoliciesNode.class));
        }
    }

    @Override
    public String create() {
        StringBuilder log = new StringBuilder();
        PolicyNode topNode = getTopNode();
        Policy policy = createPolicy(log);
        policy.node().set(topNode);
        Persistence.service().merge(policy);

        String policyCreationLog = log.toString();
        return policyClass.getSimpleName() + " was successfully assigned to '" + topNode.getStringView() + "'"
                + ("".equals(policyCreationLog) ? "" : ": " + policyCreationLog);
    }

    @Override
    public String delete() {
        if (ApplicationMode.isDevelopment()) {
            StringBuilder deleteLog = new StringBuilder();
            for (Class<? extends IEntity> x : whatToDelete) {
                String d = deleteAll(x);
                if (d != null) {
                    deleteLog.append(d);
                }
            }
            return !deleteLog.toString().equals("") ? deleteLog.toString() : null;
        } else {
            return "This is production";
        }
    }

}
