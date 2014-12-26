/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 24, 2012
 * @author ArtyomB
 */
package com.propertyvista.portal.shared.ui.communication;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTable.ItemSelectionHandler;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.DataTablePanel;
import com.pyx4j.forms.client.ui.datatable.ListerDataSource;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.domain.security.PortalResidentBehavior;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.communication.MessageDTO;
import com.propertyvista.portal.shared.resources.PortalImages;
import com.propertyvista.portal.shared.themes.DashboardTheme;
import com.propertyvista.portal.shared.ui.AbstractGadget;

public class MessageViewImpl extends SimplePanel implements MessageView {

    private final static I18n i18n = I18n.get(MessageViewImpl.class);

    private final MessageLister lister;

    private Presenter presenter;

    public MessageViewImpl() {
        setStyleName(DashboardTheme.StyleName.Dashboard.name());

        lister = new MessageLister();
        MessageGadget gadget = new MessageGadget(lister);

        setWidget(gadget);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void populate() {
        lister.setDataSource(new ListerDataSource<MessageDTO>(MessageDTO.class, presenter.getService()));
        lister.populate();
    }

    class MessageGadget extends AbstractGadget<MessageViewImpl> {

        MessageGadget(final MessageLister lister) {
            super(MessageViewImpl.this, null, i18n.tr("Tenant Communication"), ThemeColor.foreground, 0.3);
            lister.setWidth("100%");
            lister.getDataTable().setColumnSelectorVisible(true);

            lister.addItemSelectionHandler(new ItemSelectionHandler() {
                @Override
                public void onChange() {
                    AppSite.getPlaceController().goTo(new PortalSiteMap.Message.MessagePage(lister.getSelectedItem().getPrimaryKey()));

                }

            });

            setContent(lister);
        }

    }

    private static class MessageLister extends DataTablePanel<MessageDTO> {

        public MessageLister() {
            super(MessageDTO.class, SecurityController.check(PortalResidentBehavior.CommunicationCreateMessages), false);
            setFilteringEnabled(false);
            //getDataTablePanel().getAddButton().asWidget().setStyleName(DataTableTheme.StyleName.ListerButton.name());
            // No filtering work for it
            getDataTable().setHasColumnClickSorting(false);
            MessageDTO proto = EntityFactory.getEntityPrototype(MessageDTO.class);
            setColumnDescriptors( //
                    new ColumnDescriptor.Builder(proto.highImportance()).searchable(false).width("27px").formatter(//
                            booleanField2Image(proto.highImportance().getPath(), PortalImages.INSTANCE.messageImportance(), null))//
                            .columnTitleShown(false).build(),//
                    new ColumnDescriptor.Builder(proto.star()).searchable(false).width("27px").formatter(//
                            booleanField2Image(proto.star().getPath(), PortalImages.INSTANCE.fullStar(), PortalImages.INSTANCE.noStar()))//
                            .columnTitleShown(false).build(),//
                    new ColumnDescriptor.Builder(proto.hasAttachments()).searchable(false).width("27px").formatter(//
                            booleanField2Image(proto.hasAttachments().getPath(), PortalImages.INSTANCE.attachement(), null))//
                            .columnTitleShown(false).build(), //
                    new ColumnDescriptor.Builder(proto.senders()).width("100px").formatter(baseFieldViewOnIsRead(proto.senders().getPath())).build(),//
                    new ColumnDescriptor.Builder(proto.subject()).width("200px").formatter(baseFieldViewOnIsRead(proto.subject().getPath())).build(),//
                    new ColumnDescriptor.Builder(proto().date()).width("100px").build(), //
                    new ColumnDescriptor.Builder(proto().messagesInThread()).width("100px").build());

            setDataTableModel(new DataTableModel<MessageDTO>());

        }

        @Override
        protected void onItemNew() {
            AppSite.getPlaceController().goTo(new PortalSiteMap.Message.MessageWizard());

        }

        @Override
        public List<Sort> getDefaultSorting() {
            return Arrays.asList(new Sort(proto().date(), true), new Sort(proto().isRead(), false), new Sort(proto().highImportance(), true));
        }

        private static IFormatter<IEntity, SafeHtml> booleanField2Image(final Path path, final ImageResource trueValueResource,
                final ImageResource falseValueResource) {
            return new IFormatter<IEntity, SafeHtml>() {
                @Override
                public SafeHtml format(IEntity value) {
                    SafeHtmlBuilder builder = new SafeHtmlBuilder();
                    Boolean v = (Boolean) value.getMember(path).getValue();
                    if (v != null && v.booleanValue()) {
                        builder.appendHtmlConstant(new Image(trueValueResource).toString());
                    } else if (falseValueResource != null) {
                        builder.appendHtmlConstant(new Image(falseValueResource).toString());
                    }

                    return builder.toSafeHtml();
                }
            };
        }

        private static IFormatter<IEntity, SafeHtml> baseFieldViewOnIsRead(final Path path) {
            return new IFormatter<IEntity, SafeHtml>() {
                @Override
                public SafeHtml format(IEntity value) {
                    SafeHtmlBuilder builder = new SafeHtmlBuilder();
                    if (value != null) {
                        MessageDTO v = (MessageDTO) value;
                        Boolean isRead = v.isRead().getValue();
                        String s = value.getMember(path).getValue().toString();
                        if ((isRead == null || !isRead.booleanValue())) {
                            Label messageField = new Label(s);
                            messageField.getElement().getStyle().setFontWeight(FontWeight.BOLD);
                            builder.appendHtmlConstant(messageField.toString());
                        } else {
                            builder.appendHtmlConstant(s);
                        }
                    }
                    return builder.toSafeHtml();
                }
            };
        }
    }
}
