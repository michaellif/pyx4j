/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services.admin;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Key;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.rpc.services.admin.ProductCodeCrudService;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.YardiChargeCode;
import com.propertyvista.shared.config.VistaFeatures;

public class ProductCodeCrudServiceImpl extends AbstractCrudServiceImpl<ARCode> implements ProductCodeCrudService {

    private static final I18n i18n = I18n.get(ProductCodeCrudServiceImpl.class);

    public ProductCodeCrudServiceImpl() {
        super(ARCode.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    public void save(AsyncCallback<Key> callback, ARCode dto) {
        assertIsValid(dto);
        super.save(callback, dto);
    }

    @Override
    public void create(AsyncCallback<Key> callback, ARCode dto) {
        assertIsValid(dto);
        super.create(callback, dto);
    }

    private void assertIsValid(ARCode arCode) {
        if (VistaFeatures.instance().yardiIntegration()) {
            for (YardiChargeCode yardiCode : arCode.yardiChargeCodes()) {
                assertValidYardiCode(arCode, yardiCode.yardiChargeCode().getValue());
            }
        }
    }

    private void assertValidYardiCode(ARCode arCode, String yardiCode) {
        if (!CommonsStringUtils.isStringSet(yardiCode)) {
            throw new UserRuntimeException(i18n.tr("Yardi Charge Code cannot be empty"));
        }

        EntityQueryCriteria<ARCode> criteria = EntityQueryCriteria.create(ARCode.class);
        criteria.eq(criteria.proto().yardiChargeCodes().$().yardiChargeCode(), yardiCode);
        List<ARCode> arCodes = Persistence.service().query(criteria);
        for (ARCode otherCode : arCodes) {
            if (!otherCode.getPrimaryKey().equals(arCode.getPrimaryKey())
                    && otherCode.type().getValue().getActionType() == otherCode.type().getValue().getActionType()) {
                throw new UserRuntimeException(i18n.tr("An ARCode with same mapping to yardi code already exits: ARCode \"{0}\" > YardiCode \"{1}\"", otherCode
                        .name().getValue(), yardiCode));
            }
        }

    }
}
