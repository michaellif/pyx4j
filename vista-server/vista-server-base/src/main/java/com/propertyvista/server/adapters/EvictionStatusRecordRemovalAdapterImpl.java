/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 2, 2015
 * @author stanp
 */
package com.propertyvista.server.adapters;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.eviction.EvictionDocument;
import com.propertyvista.domain.eviction.EvictionStatusN4;
import com.propertyvista.domain.eviction.EvictionStatusRecord;
import com.propertyvista.shared.adapters.EvictionStatusRecordRemovalAdapter;

public class EvictionStatusRecordRemovalAdapterImpl implements EvictionStatusRecordRemovalAdapter {

    /*
     * If status record with attachments is removed from EvictionStatusN4.statusRecords(), the possible
     * references from EvictionStatusN4.generatedForms() must be removed as well
     */
    @Override
    public void onBeforeUpdate(EvictionStatusN4 origEntity, EvictionStatusN4 newEntity) {
        // find deleted records
        for (EvictionStatusRecord record : origEntity.statusRecords()) {
            if (!newEntity.statusRecords().contains(record)) {
                // remove other references, if any
                Persistence.ensureRetrieve(record.attachments(), AttachLevel.IdOnly);
                for (EvictionDocument doc : record.attachments()) {
                    newEntity.generatedForms().remove(doc);
                }
            }
        }
    }
}
