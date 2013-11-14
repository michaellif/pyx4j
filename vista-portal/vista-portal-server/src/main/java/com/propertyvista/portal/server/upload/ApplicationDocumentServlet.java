/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 20, 2011
 * @author sergei
 * @version $Id$
 */

package com.propertyvista.portal.server.upload;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Key;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.entity.shared.utils.EntityGraph.ApplyMethod;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.security.shared.SecurityViolationException;

import com.propertyvista.domain.IUserEntity;
import com.propertyvista.domain.media.ApplicationDocumentFile;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.domain.security.VistaCustomerBehavior;
import com.propertyvista.portal.server.portal.prospect.ProspectPortalContext;
import com.propertyvista.server.domain.ApplicationDocumentBlob;

@SuppressWarnings("serial")
public class ApplicationDocumentServlet extends HttpServlet {

    private final static I18n i18n = I18n.get(ApplicationDocumentServlet.class);

    private final static Logger log = LoggerFactory.getLogger(ApplicationDocumentServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String filename = request.getPathInfo();
        String id = FilenameUtils.getPathNoEndSeparator(filename);
        if (CommonsStringUtils.isEmpty(id) || "0".equals(id)) {
            response.setStatus(HttpServletResponse.SC_GONE);
            return;
        }

        //TODO deserialize key
        ApplicationDocumentFile doc = Persistence.service().retrieve(ApplicationDocumentFile.class, new Key(id));
        if (doc == null) {
            log.debug("no such document {} {}", id, filename);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        assertAccessRights(doc);

        ApplicationDocumentBlob adata = Persistence.service().retrieve(ApplicationDocumentBlob.class, doc.blobKey().getValue());
        if (adata == null) {
            log.debug("no such blob {} {}", id, filename);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setContentType(adata.contentType().getValue());
        response.getOutputStream().write(adata.data().getValue());
    }

    private void assertAccessRights(final ApplicationDocumentFile doc) {
        final Boolean[] isAccessRightsFound = new Boolean[] { false };
        if (SecurityController.checkAnyBehavior(VistaCustomerBehavior.Prospective, VistaCustomerBehavior.ProspectiveSubmitted,
                VistaCustomerBehavior.ProspectiveApplicant, VistaCustomerBehavior.ProspectiveSubmittedApplicant, VistaCustomerBehavior.ProspectiveCoApplicant,
                VistaCustomerBehavior.ProspectiveSubmittedCoApplicant, VistaCustomerBehavior.Guarantor, VistaCustomerBehavior.GuarantorSubmitted)) {
            EntityGraph.applyToOwners(doc, new ApplyMethod() {
                @Override
                public boolean apply(IEntity entity) {
                    if (entity.isValueDetached()) {
                        Persistence.service().retrieve(entity);
                    }
                    if (entity.isInstanceOf(IUserEntity.class)) {
                        Key documentOwnerUserKey = ((IUserEntity) entity).user().getPrimaryKey();
                        if (documentOwnerUserKey != null && documentOwnerUserKey.equals(ProspectPortalContext.getCurrentUserPrimaryKey())) {
                            isAccessRightsFound[0] = true;
                            return false;
                        } else {
                            log.warn(SimpleMessageFormat.format(
                                    "An attempt to access {0} that belongs different user was blocked (entity id={1}; owner user id={2}, rogue user id={3})",

                                    doc.getInstanceValueClass().getSimpleName(),

                                    doc.getPrimaryKey(),

                                    documentOwnerUserKey,

                                    ProspectPortalContext.getCurrentUserPrimaryKey()

                            ));

                            throw new SecurityViolationException(i18n.tr("Access Denied"));
                        }
                    } else {
                        return true;
                    }
                }
            });
            if (!isAccessRightsFound[0]) {
                throw new Error(SimpleMessageFormat.format("Failed to get access rights for {0} (id = {1})",

                doc.getInstanceValueClass().getSimpleName(),

                doc.getPrimaryKey()));
            }
        } else {
            SecurityController.assertBehavior(VistaCrmBehavior.Tenants);
        }

    }
}
