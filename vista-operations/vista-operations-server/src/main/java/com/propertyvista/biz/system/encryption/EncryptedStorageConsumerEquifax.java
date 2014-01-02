/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-14
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.system.encryption;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.server.TaskRunner;
import com.propertyvista.server.domain.CustomerCreditCheckReport;
import com.propertyvista.server.domain.CustomerCreditCheckReportNoBackup;

public class EncryptedStorageConsumerEquifax implements EncryptedStorageConsumer {

    @Override
    public int countRecords(final Key publicKeyKey) {
        return TaskRunner.runInTargetNamespace(VistaNamespace.expiringNamespace, new Callable<Integer>() {
            @Override
            public Integer call() {
                int count = 0;
                {
                    EntityQueryCriteria<CustomerCreditCheckReport> criteria = EntityQueryCriteria.create(CustomerCreditCheckReport.class);
                    criteria.eq(criteria.proto().publicKey(), publicKeyKey);
                    count += Persistence.service().count(criteria);
                }
                {
                    EntityQueryCriteria<CustomerCreditCheckReportNoBackup> criteria = EntityQueryCriteria.create(CustomerCreditCheckReportNoBackup.class);
                    criteria.eq(criteria.proto().publicKey(), publicKeyKey);
                    count += Persistence.service().count(criteria);
                }
                return count;
            }
        });
    }

    @Override
    public int processKeyRotation(final AtomicInteger progress, final Key fromPublicKeyKey, final Key toPublicKeyKey) {
        return TaskRunner.runInTargetNamespace(VistaNamespace.expiringNamespace, new Callable<Integer>() {
            @Override
            public Integer call() {
                int count = 0;
                {
                    EntityQueryCriteria<CustomerCreditCheckReport> criteria = EntityQueryCriteria.create(CustomerCreditCheckReport.class);
                    criteria.eq(criteria.proto().publicKey(), fromPublicKeyKey);
                    ICursorIterator<CustomerCreditCheckReport> cursor = Persistence.service().query(null, criteria, AttachLevel.Attached);
                    try {
                        while (cursor.hasNext()) {
                            CustomerCreditCheckReport report = cursor.next();
                            byte[] decryptedData = ServerSideFactory.create(EncryptedStorageFacade.class).decrypt(fromPublicKeyKey, report.data().getValue());
                            byte[] encryptedData = ServerSideFactory.create(EncryptedStorageFacade.class).encrypt(toPublicKeyKey, decryptedData);

                            report.publicKey().setValue(toPublicKeyKey);
                            report.data().setValue(encryptedData);
                            Persistence.service().persist(report);
                            Persistence.service().commit();
                            count++;
                            progress.addAndGet(1);
                        }
                    } finally {
                        cursor.close();
                    }
                }

                {
                    EntityQueryCriteria<CustomerCreditCheckReportNoBackup> criteria = EntityQueryCriteria.create(CustomerCreditCheckReportNoBackup.class);
                    criteria.eq(criteria.proto().publicKey(), fromPublicKeyKey);
                    ICursorIterator<CustomerCreditCheckReportNoBackup> cursor = Persistence.service().query(null, criteria, AttachLevel.Attached);
                    try {
                        while (cursor.hasNext()) {
                            CustomerCreditCheckReportNoBackup report = cursor.next();
                            byte[] decryptedData = ServerSideFactory.create(EncryptedStorageFacade.class).decrypt(fromPublicKeyKey, report.data().getValue());
                            byte[] encryptedData = ServerSideFactory.create(EncryptedStorageFacade.class).encrypt(toPublicKeyKey, decryptedData);

                            report.publicKey().setValue(toPublicKeyKey);
                            report.data().setValue(encryptedData);
                            Persistence.service().persist(report);
                            Persistence.service().commit();
                            count++;
                            progress.addAndGet(1);
                        }
                    } finally {
                        cursor.close();
                    }
                }
                return count;
            }
        });
    }

}
