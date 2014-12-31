/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 31, 2014
 * @author stanp
 */
package com.propertyvista.crm.client.ui.crud.lease.eviction.n4;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.forms.client.ui.CPhoneField;
import com.pyx4j.forms.client.ui.CPhoneField.PhoneType;
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.backoffice.ui.prime.CEntityCrudHyperlink;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;

import com.propertyvista.common.client.ui.components.editors.InternationalAddressEditor;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.legal.n4.N4BatchItem;
import com.propertyvista.dto.N4BatchDTO;

public class N4BatchForm extends CrmEntityForm<N4BatchDTO> {

    private static final I18n i18n = I18n.get(N4BatchForm.class);

    public N4BatchForm(IPrimeFormView<N4BatchDTO, ?> view) {
        super(N4BatchDTO.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.h1(i18n.tr("General:"));
        formPanel.append(Location.Left, proto().name()).decorate();
        formPanel.append(Location.Left, proto().created()).decorate();

        formPanel.append(Location.Right, proto().noticeDate()).decorate();
        formPanel.append(Location.Right, proto().signingEmployee(), new CEntityCrudHyperlink<Employee>(AppPlaceEntityMapper.resolvePlace(Employee.class)))
                .decorate();

        formPanel.h1(i18n.tr("Contact Information:"));
        formPanel.append(Location.Left, proto().companyLegalName()).decorate();
        formPanel.append(Location.Left, proto().companyEmailAddress()).decorate();
        formPanel.append(Location.Right, proto().companyPhoneNumber(), new CPhoneField(PhoneType.northAmerica)).decorate();
        formPanel.append(Location.Right, proto().companyFaxNumber(), new CPhoneField(PhoneType.northAmerica)).decorate();
        formPanel.h3(i18n.tr("Mailing Address:"));
        formPanel.append(Location.Dual, proto().companyAddress(), new InternationalAddressEditor());

        formPanel.h1(i18n.tr("Batch Records"));
        formPanel.append(Location.Dual, proto().items(), new N4BatchItemFolder());

        selectTab(addTab(formPanel, i18n.tr("General")));
        setTabBarVisible(false);
    }

    class N4BatchItemFolder extends VistaTableFolder<N4BatchItem> {

        public N4BatchItemFolder() {
            super(N4BatchItem.class);
        }

        @Override
        public List<FolderColumnDescriptor> columns() {
            return Arrays.asList( //
                    new FolderColumnDescriptor(proto().terminationDate(), "9em"), //
                    new FolderColumnDescriptor(proto().totalRentOwning(), "10em"));
        }

    }
}
