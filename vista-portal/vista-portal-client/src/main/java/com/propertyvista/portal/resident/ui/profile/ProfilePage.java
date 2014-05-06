/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 18, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.profile;

import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CImage;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFluidPanel.Location;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.VistaFileURLBuilder;
import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.validators.PastDateIncludeTodayValidator;
import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.tenant.CustomerPicture;
import com.propertyvista.domain.tenant.EmergencyContact;
import com.propertyvista.portal.rpc.portal.resident.dto.ResidentProfileDTO;
import com.propertyvista.portal.rpc.portal.shared.services.CustomerPicturePortalUploadService;
import com.propertyvista.portal.shared.themes.EntityViewTheme;
import com.propertyvista.portal.shared.ui.CPortalEntityEditor;
import com.propertyvista.portal.shared.ui.util.editors.EmergencyContactFolder;
import com.propertyvista.shared.config.VistaFeatures;

public class ProfilePage extends CPortalEntityEditor<ResidentProfileDTO> {

    private static final I18n i18n = I18n.get(ProfilePage.class);

    public ProfilePage(ProfilePageViewImpl view) {
        super(ResidentProfileDTO.class, view, "My Profile", ThemeColor.contrast2);
        asWidget().setStyleName(EntityViewTheme.StyleName.EntityView.name());
    }

    @Override
    protected IsWidget createContent() {
        BasicCFormPanel formPanel = new BasicCFormPanel(this);

        CImage imageHolder = new CImage(GWT.<CustomerPicturePortalUploadService> create(CustomerPicturePortalUploadService.class), new VistaFileURLBuilder(
                CustomerPicture.class));
        imageHolder.setImageSize(150, 200);
        imageHolder.setThumbnailPlaceholder(new Image(VistaImages.INSTANCE.profilePicture()));

        formPanel.append(Location.Dual, proto().picture().file(), imageHolder).decorate().customLabel("");

        formPanel.h1(i18n.tr("Basic Information"));

        formPanel.append(Location.Dual, proto().person().name(), new CEntityLabel<Name>()).decorate().customLabel(i18n.tr("Full Name"));
        formPanel.append(Location.Left, proto().person().sex()).decorate().componentWidth(100);
        formPanel.append(Location.Right, proto().person().birthDate()).decorate().componentWidth(150);

        formPanel.h1(i18n.tr("Contact Information"));
        formPanel.append(Location.Left, proto().person().homePhone()).decorate().componentWidth(200);
        formPanel.append(Location.Left, proto().person().mobilePhone()).decorate().componentWidth(200);
        formPanel.append(Location.Left, proto().person().workPhone()).decorate().componentWidth(200);
        formPanel.append(Location.Right, proto().person().email()).decorate().componentWidth(230);

        formPanel.h1(proto().emergencyContacts().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().emergencyContacts(), new EmergencyContactFolder());

        return formPanel;
    }

    @Override
    public void addValidations() {
        get(proto().person().email()).setMandatory(true);
        get(proto().person().birthDate()).addComponentValidator(new PastDateIncludeTodayValidator());
        get(proto().emergencyContacts()).addComponentValidator(new AbstractComponentValidator<List<EmergencyContact>>() {
            @Override
            public FieldValidationError isValid() {
                if (getComponent().getValue() == null || getValue() == null) {
                    return null;
                }

                if (!VistaFeatures.instance().yardiIntegration()) {
                    if (getComponent().getValue().isEmpty()) {
                        return new FieldValidationError(getComponent(), i18n.tr("Empty Emergency Contacts list"));
                    }
                }

                return !EntityGraph.hasBusinessDuplicates(getValue().emergencyContacts()) ? null : new FieldValidationError(getComponent(), i18n
                        .tr("Duplicate Emergency Contacts specified"));
            }
        });
    }
}
