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
package com.propertyvista.portal.web.client.ui.profile;

import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.forms.client.images.EntityFolderImages;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CImage;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.gwt.shared.FileURLBuilder;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.components.MediaUtils;
import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.tenant.CustomerPicture;
import com.propertyvista.domain.tenant.EmergencyContact;
import com.propertyvista.portal.rpc.portal.web.dto.ResidentProfileDTO;
import com.propertyvista.portal.rpc.portal.web.services.ResidentPictureUploadService;
import com.propertyvista.portal.web.client.themes.EntityViewTheme;
import com.propertyvista.portal.web.client.ui.CPortalEntityEditor;
import com.propertyvista.portal.web.client.ui.util.decorators.FormWidgetDecoratorBuilder;
import com.propertyvista.shared.config.VistaFeatures;

public class ProfilePage extends CPortalEntityEditor<ResidentProfileDTO> {

    private static final I18n i18n = I18n.get(ProfilePage.class);

    public ProfilePage(ProfilePageViewImpl view) {
        super(ResidentProfileDTO.class, view, "My Profile", ThemeColor.contrast2);
        asWidget().setStyleName(EntityViewTheme.StyleName.EntityView.name());
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel mainPanel = new BasicFlexFormPanel();
        int row = -1;

        CImage<CustomerPicture> imageHolder = new CImage<CustomerPicture>(GWT.<ResidentPictureUploadService> create(ResidentPictureUploadService.class)) {
            @Override
            protected EntityFolderImages getFolderIcons() {
                return VistaImages.INSTANCE;
            }

            @Override
            public Widget getImageEntryView(CEntityForm<CustomerPicture> entryForm) {
                VerticalPanel infoPanel = new VerticalPanel();
                return infoPanel;
            }
        };
        imageHolder.setFileUrlBuilder(new ImageFileURLBuilder());
        imageHolder.setImageSize(150, 200);
        imageHolder.setThumbnailPlaceholder(new Image(VistaImages.INSTANCE.profilePicture()));

        mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().picture(), imageHolder)).customLabel("").build());

        mainPanel.setH1(++row, 0, 1, i18n.tr("Basic Information"));

        mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().person().name(), new CEntityLabel<Name>()), 200)
                .customLabel(i18n.tr("Full Name")).build());

        mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().person().sex()), 100).build());
        mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().person().birthDate()), 150).build());

        get(proto().person().birthDate()).addValueValidator(new EditableValueValidator<LogicalDate>() {
            @Override
            public ValidationError isValid(CComponent<LogicalDate> component, LogicalDate value) {
                if (value != null && value.compareTo(new LogicalDate()) > 0) {
                    return new ValidationError(component, i18n.tr("This date is in the future. Please enter your birthdate."));
                }
                return null;
            }
        });

        get(proto().person().birthDate()).addValueValidator(new EditableValueValidator<LogicalDate>() {
            @Override
            public ValidationError isValid(CComponent<LogicalDate> component, LogicalDate value) {
                if (value != null && value.compareTo(new LogicalDate(System.currentTimeMillis() - 120L * 365 * 24 * 60 * 60 * 1000)) < 0) {
                    return new ValidationError(component, i18n.tr("This date is too far in the past. Please enter your birthdate."));
                }
                return null;
            }
        });

        mainPanel.setH1(++row, 0, 1, i18n.tr("Contact Information"));
        mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().person().homePhone()), 200).build());
        mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().person().mobilePhone()), 200).build());
        mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().person().workPhone()), 200).build());
        mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().person().email()), 230).build());

        mainPanel.setH1(++row, 0, 1, proto().emergencyContacts().getMeta().getCaption());
        mainPanel.setWidget(++row, 0, inject(proto().emergencyContacts(), new EmergencyContactFolder((ProfilePageViewImpl) getView())));

        return mainPanel;
    }

    @Override
    public void addValidations() {
        get(proto().emergencyContacts()).addValueValidator(new EditableValueValidator<List<EmergencyContact>>() {
            @Override
            public ValidationError isValid(CComponent<List<EmergencyContact>> component, List<EmergencyContact> value) {
                if (value == null || getValue() == null) {
                    return null;
                }

                if (!VistaFeatures.instance().yardiIntegration()) {
                    if (value.isEmpty()) {
                        return new ValidationError(component, i18n.tr("Empty Emergency Contacts list"));
                    }
                }

                return !EntityGraph.hasBusinessDuplicates(getValue().emergencyContacts()) ? null : new ValidationError(component, i18n
                        .tr("Duplicate Emergency Contacts specified"));
            }
        });
    }

    class ImageFileURLBuilder implements FileURLBuilder<CustomerPicture> {
        @Override
        public String getUrl(CustomerPicture pic) {
            return MediaUtils.createCustomerPictureUrl(pic);
        }
    }
}
