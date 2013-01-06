/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-04
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.system;

import java.io.Serializable;
import java.util.concurrent.Callable;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.admin.domain.vista2pmc.DefaultEquifaxFee;
import com.propertyvista.biz.financial.payment.MerchantTerminalSourceTenantSure;
import com.propertyvista.biz.financial.payment.MerchantTerminalSourceVista;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.fee.AbstractEquifaxFee;
import com.propertyvista.domain.pmc.fee.AbstractPaymentFees;
import com.propertyvista.domain.pmc.fee.PmcEquifaxFee;
import com.propertyvista.server.jobs.TaskRunner;

public class Vista2PmcFacadeImpl implements Vista2PmcFacade {

    @SuppressWarnings("unchecked")
    private static <S extends Serializable> void setNonNullMember(IPrimitive<S> dst, IEntity... srcs) {
        for (IEntity src : srcs) {
            if (!src.getMember(dst.getFieldName()).isNull()) {
                dst.set((IPrimitive<S>) src.getMember(dst.getFieldName()));
                break;
            }
        }
    }

    @Override
    public AbstractEquifaxFee getEquifaxFee() {
        DefaultEquifaxFee defaultEfxFeee = TaskRunner.runInAdminNamespace(new Callable<DefaultEquifaxFee>() {
            @Override
            public DefaultEquifaxFee call() {
                return Persistence.service().retrieve(EntityQueryCriteria.create(DefaultEquifaxFee.class));
            }
        });
        final Pmc pmc = VistaDeployment.getCurrentPmc().duplicate();
        TaskRunner.runInAdminNamespace(new Callable<PmcEquifaxFee>() {
            @Override
            public PmcEquifaxFee call() {
                Persistence.service().retrieveMember(pmc.equifaxFee());
                return pmc.equifaxFee();
            }
        });

        AbstractEquifaxFee efxFeee = EntityFactory.create(AbstractEquifaxFee.class);
        setNonNullMember(efxFeee.recommendationReportPerApplicantFee(), pmc.equifaxFee(), defaultEfxFeee);
        setNonNullMember(efxFeee.recommendationReportSetUpFee(), pmc.equifaxFee(), defaultEfxFeee);
        setNonNullMember(efxFeee.fullCreditReportPerApplicantFee(), pmc.equifaxFee(), defaultEfxFeee);
        setNonNullMember(efxFeee.fullCreditReportSetUpFee(), pmc.equifaxFee(), defaultEfxFeee);
        return efxFeee;
    }

    @Override
    public AbstractPaymentFees getPaymentFees() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getTenantSureMerchantTerminalId() {
        return new MerchantTerminalSourceTenantSure().getMerchantTerminalId();
    }

    @Override
    public String getVistaMerchantTerminalId() {
        return new MerchantTerminalSourceVista().getMerchantTerminalId();
    }

}
