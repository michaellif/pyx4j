/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-16
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.building.catalog;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.versioning.VersionedLister;
import com.propertyvista.domain.financial.offering.Concession;

public class ConcessionLister extends VersionedLister<Concession> {

    private final static I18n i18n = I18n.get(ConcessionLister.class);

    public ConcessionLister() {
        super(Concession.class, true, true);
        getDataTablePanel().setFilteringEnabled(false);

        setColumnDescriptors(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().version().versionNumber()).build(),
            new MemberColumnDescriptor.Builder(proto().version().type()).build(),
            new MemberColumnDescriptor.Builder(proto().version().term()).build(),
            new ColumnDescriptor(proto().version().value().getPath().toString(), proto().version().value().getMeta().getCaption()) {              
                @Override
                public String convert(IEntity entity) {
                    if (entity != null) {
                        Concession consssion = (Concession) entity;
                        String format = null;
                        switch(consssion.version().type().getValue()) {
                        case percentageOff:
                            format = "{0,number,percent}";
                            break;
                        case free:
                        case monetaryOff:
                        case promotionalItem:                           
                        default:
                            format = "${0,number,#.##}";
                        }
                        String formattedValue = SimpleMessageFormat.format(format, consssion.version().value().getValue());
                        return formattedValue;
                    } else {
                        return super.convert(entity);
                    }                        
                }
            },
            new MemberColumnDescriptor.Builder(proto().version().condition()).build(),
            new MemberColumnDescriptor.Builder(proto().version().effectiveDate()).build(),
            new MemberColumnDescriptor.Builder(proto().version().expirationDate()).build()
        );//@formatter:on        
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().version().type(), false));
    }
}
