/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-03-27
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.adapters;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.domain.media.ApplicationDocument;
import com.propertyvista.shared.adapters.ApplicationDocumentUploadedBlobSecurityAdapter;

public class ApplicationDocumentUploadedBlobSecurityAdapterImpl implements ApplicationDocumentUploadedBlobSecurityAdapter {

    private static final String LIST_SESSION_ATTRIBUTE = ApplicationDocumentUploadedBlobSecurityAdapterImpl.class.getName();

    public static void blobUploaded(Key blobKey) {
        @SuppressWarnings("unchecked")
        Set<Key> userUploadeKeys = (Set<Key>) Context.getVisit().getAttribute(LIST_SESSION_ATTRIBUTE);
        if (userUploadeKeys == null) {
            userUploadeKeys = new HashSet<Key>();
            Context.getVisit().setAttribute(LIST_SESSION_ATTRIBUTE, (Serializable) userUploadeKeys);
        }
        userUploadeKeys.add(blobKey);
    }

    @Override
    public boolean allowModifications(ApplicationDocument entity, MemberMeta meta, Object valueOrig, Object valueNew) {
        @SuppressWarnings("unchecked")
        Set<Key> userUploadeKeys = (Set<Key>) Context.getVisit().getAttribute(LIST_SESSION_ATTRIBUTE);
        if (userUploadeKeys == null) {
            return false;
        }
        Key blobKeyNew = ((Key) valueNew);
        if (blobKeyNew != null & userUploadeKeys.contains(blobKeyNew)) {
            return true;
        } else {
            return false;
        }
    }
}
