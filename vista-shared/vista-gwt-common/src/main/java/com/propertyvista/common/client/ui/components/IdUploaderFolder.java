/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 27, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationFailure;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.domain.media.ApplicationDocument;
import com.propertyvista.domain.policy.policies.ApplicationDocumentationPolicy;

public class IdUploaderFolder extends ApplicationDocumentUploaderFolder {

    private final static I18n i18n = I18n.get(IdUploaderFolder.class);

    private static final List<EntityFolderColumnDescriptor> ID_UPLOADER_COLUMNS;

    static {
        ApplicationDocument proto = EntityFactory.getEntityPrototype(ApplicationDocument.class);
        ID_UPLOADER_COLUMNS = new ArrayList<EntityFolderColumnDescriptor>();
        ID_UPLOADER_COLUMNS.add(new EntityFolderColumnDescriptor(proto.idType(), "15em"));
        ID_UPLOADER_COLUMNS.add(new EntityFolderColumnDescriptor(proto.details(), "15em"));
        ID_UPLOADER_COLUMNS.add(new EntityFolderColumnDescriptor(proto.fileName(), "25em"));
        ID_UPLOADER_COLUMNS.add(new EntityFolderColumnDescriptor(proto.fileSize(), "5em"));
    }

    protected ApplicationDocumentationPolicy documentationPolicy = null;

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        return ID_UPLOADER_COLUMNS;
    }

    public IdUploaderFolder(boolean enforceDocumentsPolicy) {
        super();
        if (enforceDocumentsPolicy) {
            this.addValueValidator(new EditableValueValidator<IList<ApplicationDocument>>() {
                @Override
                public ValidationFailure isValid(CComponent<IList<ApplicationDocument>, ?> component, IList<ApplicationDocument> value) {
                    if (value != null) {
                        if (documentationPolicy != null) {
                            Set<Key> usedDocuments = new HashSet<Key>();
                            for (ApplicationDocument doc : value) {
                                if (!doc.blobKey().isNull()) {
                                    usedDocuments.add(doc.blobKey().getValue());
                                }
                            }
                            int numOfRemainingDocs = documentationPolicy.numberOfRequiredIDs().getValue() - usedDocuments.size();
                            if (numOfRemainingDocs > 0) {
                                return new ValidationFailure(i18n.tr("{0} more kinds of documents are required", numOfRemainingDocs));
                            }
                        } else {
                            return new ValidationFailure(i18n.tr("Validation Policy Not Available"));
                        }
                    }
                    return null;
                }
            });
        }

    }

    public IdUploaderFolder() {
        this(false);
    }

    @Override
    protected void onPopulate() {
        ClientPolicyManager.obtainEffectivePolicy(ClientPolicyManager.getOrganizationPoliciesNode(), ApplicationDocumentationPolicy.class,
                new DefaultAsyncCallback<ApplicationDocumentationPolicy>() {
                    @Override
                    public void onSuccess(ApplicationDocumentationPolicy result) {
                        documentationPolicy = result;
                        IdUploaderFolder.super.onPopulate();
                    }
                });
    }

}
