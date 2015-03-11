/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 10, 2015
 * @author VladL
 */
package com.propertyvista.server;

import java.util.EnumSet;
import java.util.concurrent.Callable;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.VersionedCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.operations.domain.legal.VistaTerms;
import com.propertyvista.shared.i18n.CompiledLocale;

public class VistaTermsUtils {

    public static VistaTerms retrieveVistaTerms(final VistaTerms.Target target) {
        return retrieveVistaTerms(target, CompiledLocale.getEnglishLocales());
    }

    public static VistaTerms retrieveVistaTerms(final VistaTerms.Target target, final EnumSet<CompiledLocale> locales) {
        VistaTerms terms = TaskRunner.runInOperationsNamespace(new Callable<VistaTerms>() {
            @Override
            public VistaTerms call() {
                EntityQueryCriteria<VistaTerms> criteria = EntityQueryCriteria.create(VistaTerms.class);
                criteria.eq(criteria.proto().target(), target);
                criteria.in(criteria.proto().version().document().$().locale(), locales);
                // and current finalized version only:
                criteria.setVersionedCriteria(VersionedCriteria.onlyFinalized);
                return Persistence.service().retrieve(criteria);
            }
        });
        if (terms == null) {
            throw new RuntimeException("Terms not found");
        }
        return terms;
    }
}
