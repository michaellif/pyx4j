/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 19, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.rpc;

import java.util.Collection;
import java.util.Vector;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.domain.security.VistaCrmBehaviorDTO;

public class VistaCrmBehaviorDTOCoverter {

    public static Vector<VistaCrmBehaviorDTO> toDTO(Collection<VistaCrmBehavior> c) {
        Vector<VistaCrmBehaviorDTO> r = new Vector<VistaCrmBehaviorDTO>();
        for (VistaCrmBehavior b : c) {
            VistaCrmBehaviorDTO dto = EntityFactory.create(VistaCrmBehaviorDTO.class);
            dto.setPrimaryKey(new Key(b.ordinal() + 1));
            dto.behavior().setValue(b);
            dto.permission().setValue(b.toString());
            r.add(dto);
        }
        return r;
    }

    public static void toDBO(Collection<VistaCrmBehaviorDTO> src, Collection<VistaCrmBehavior> dst) {
        for (VistaCrmBehaviorDTO dto : src) {
            dst.add(dto.behavior().getValue());
        }
    }
}
