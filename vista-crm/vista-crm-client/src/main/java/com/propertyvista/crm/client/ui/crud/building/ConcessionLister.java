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
 */
package com.propertyvista.crm.client.ui.crud.building;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;

import com.propertyvista.common.client.ui.components.versioning.VersionedLister;
import com.propertyvista.crm.rpc.services.building.catalog.ConcessionCrudService;
import com.propertyvista.domain.financial.offering.Concession;

public class ConcessionLister extends VersionedLister<Concession> {

    public ConcessionLister() {
        super(Concession.class, GWT.<AbstractCrudService<Concession>> create(ConcessionCrudService.class), true, true);
        setFilteringEnabled(false);

        setColumnDescriptors( //
                new ColumnDescriptor.Builder(proto().version().versionNumber()).build(), //
                new ColumnDescriptor.Builder(proto().version().type()).filterAlwaysShown(true).build(), //
                new ColumnDescriptor.Builder(proto().version().term()).filterAlwaysShown(true).build(), //
                new ColumnDescriptor.Builder(proto().version()).formatter(new IFormatter<IEntity, SafeHtml>() {

                    @Override
                    public SafeHtml format(IEntity value) {
                        SafeHtmlBuilder builder = new SafeHtmlBuilder();
                        if (value != null) {
                            Concession consssion = (Concession) value;
                            String format = null;
                            switch (consssion.version().type().getValue()) {
                            case percentageOff:
                                format = "{0,number,percent}";
                                break;
                            case free:
                            case monetaryOff:
                            case promotionalItem:
                            default:
                                format = "${0,number,#.##}";
                            }
                            builder.appendHtmlConstant(SimpleMessageFormat.format(format, consssion.version().value().getValue()));
                        }
                        return builder.toSafeHtml();
                    }
                }).build(), //
                new ColumnDescriptor.Builder(proto().version().condition()).filterAlwaysShown(true).build(), //
                new ColumnDescriptor.Builder(proto().version().effectiveDate()).filterAlwaysShown(true).build(), //
                new ColumnDescriptor.Builder(proto().version().expirationDate()).build());

        setDataTableModel(new DataTableModel<Concession>());
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().version().type(), false));
    }
}
