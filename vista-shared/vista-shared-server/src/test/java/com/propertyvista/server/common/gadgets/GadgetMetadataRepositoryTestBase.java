/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-09-25
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.server.common.gadgets;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.pyx4j.entity.annotations.AbstractEntity;

import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;

public class GadgetMetadataRepositoryTestBase extends TestCase {

    interface Predicate {

        /**
         * @return null if everything is ok, or else either empty string or detailed information
         */
        String reportWhatIsWrongWith(Class<? extends GadgetMetadata> klass);

    }

    /** skips metadatas marked with {@link AbstractEntity} */
    protected final void assertForEachGagetMetadataClass(String assertion, Predicate p) {
        StringBuilder b = new StringBuilder();
        for (Class<? extends GadgetMetadata> klass : GadgetMetadataRepository.get().getGadgetMetadataClasses()) {
            String error = p.reportWhatIsWrongWith(klass);
            if (error != null) {
                b.append(klass.getName());
                if (!"".equals(error)) {
                    b.append(" REASON: ");
                    b.append(error);
                }
                b.append("\n");
            }

        }
        String errors = b.toString();
        Assert.assertTrue(assertion + ":\n" + errors, errors.length() == 0);
    }

}
