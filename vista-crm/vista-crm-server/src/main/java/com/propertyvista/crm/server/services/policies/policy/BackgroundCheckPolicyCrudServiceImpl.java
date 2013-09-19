/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 30, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.policies.policy;

import com.propertyvista.crm.rpc.services.policies.policy.BackgroundCheckPolicyCrudService;
import com.propertyvista.crm.server.services.policies.GenericPolicyCrudService;
import com.propertyvista.domain.policy.dto.BackgroundCheckPolicyDTO;
import com.propertyvista.domain.policy.policies.BackgroundCheckPolicy;
import com.propertyvista.domain.policy.policies.BackgroundCheckPolicy.BjccEntry;

public class BackgroundCheckPolicyCrudServiceImpl extends GenericPolicyCrudService<BackgroundCheckPolicy, BackgroundCheckPolicyDTO> implements
        BackgroundCheckPolicyCrudService {

    private static final int bjcc[][][][] = {
     // @formatter:off    
            { { { 1, 2, 3 },    { 4, 5, 6 },    { 7, 8, 9 } },
              { { 10, 11, 12 }, { 13, 14, 15 }, { 16, 17, 18 } }, 
              { { 19, 20, 21 }, { 22, 23, 24 }, { 25, 26, 27 } } 
            },
            { { { 28, 29, 30 }, { 31, 32, 33 }, { 34, 35, 36 } }, 
              { { 37, 38, 39 }, { 40, 41, 42 }, { 43, 44, 45 } },
              { { 47, 48, 49 }, { 50, 51, 52 }, { 53, 54, 55 } } 
            },
            { { { 56, 57, 58 }, { 59, 60, 61 }, { 62, 63, 64 } }, 
              { { 65, 66, 67 }, { 68, 69, 70 }, { 71, 72, 73 } },
              { { 74, 75, 76 }, { 77, 78, 79 }, { 80, 81, 82 } } 
            } };
    // @formatter:on

    public BackgroundCheckPolicyCrudServiceImpl() {
        super(BackgroundCheckPolicy.class, BackgroundCheckPolicyDTO.class);
    }

    @Override
    protected BackgroundCheckPolicyDTO init(InitializationData initializationData) {
        BackgroundCheckPolicyDTO policy = super.init(initializationData);
        // load default values:
        policy.version().bankruptcy().setValue(BjccEntry.m12);
        policy.version().judgment().setValue(BjccEntry.m12);
        policy.version().collection().setValue(BjccEntry.m12);
        policy.version().chargeOff().setValue(BjccEntry.m12);
        policy.strategyNumber().setValue(1);

        return policy;
    }

    @Override
    protected void persist(BackgroundCheckPolicy dbo, BackgroundCheckPolicyDTO in) {
        //The order of indexes is significant
        dbo.strategyNumber().setValue(
                bjcc[in.version().bankruptcy().getValue().ordinal()][in.version().judgment().getValue().ordinal()][in.version().collection().getValue()
                        .ordinal()][in.version().chargeOff().getValue().ordinal()]);

        super.persist(dbo, in);
    }

}
