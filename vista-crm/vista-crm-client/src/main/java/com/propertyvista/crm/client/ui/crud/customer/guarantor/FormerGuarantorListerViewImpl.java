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
 */
package com.propertyvista.crm.client.ui.crud.customer.guarantor;

import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.site.client.backoffice.ui.prime.lister.AbstractListerView;

import com.propertyvista.dto.GuarantorDTO;

public class FormerGuarantorListerViewImpl extends AbstractListerView<GuarantorDTO> implements GuarantorListerView {

    public FormerGuarantorListerViewImpl() {
        setDataTablePanel(new GuarantorLister() {
            @Override
            protected EntityListCriteria<GuarantorDTO> updateCriteria(EntityListCriteria<GuarantorDTO> criteria) {
                return updateListCriteriaForFormerLeaseParticipants(criteria);
            }
        });
    }
}
