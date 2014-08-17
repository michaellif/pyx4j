/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 22, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.communication;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.activity.EntitySelectorTableVisorController;
import com.pyx4j.site.client.ui.IPane;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.common.client.PrintUtils;
import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmUserVisit;
import com.propertyvista.crm.rpc.services.selections.SelectEmployeeListService;
import com.propertyvista.domain.communication.CommunicationEndpoint.ContactType;
import com.propertyvista.domain.communication.CommunicationThread.ThreadStatus;
import com.propertyvista.domain.communication.MessageCategory.MessageGroupCategory;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.dto.MessageDTO;

public class MessageViewerViewImpl extends CrmViewerViewImplBase<MessageDTO> implements MessageViewerView {

    private static final I18n i18n = I18n.get(MessageViewerViewImpl.class);

    private final MenuItem assignOwnershipAction;

    private final MenuItem assignToMeAction;

    private final MenuItem unassignAction;

    private final Map<ThreadStatus, MenuItem> threadStatusActions;

    public MessageViewerViewImpl() {
        super(true);
        setForm(new MessageForm(this));

        final MessageForm form = (MessageForm) getForm();

        assignToMeAction = new MenuItem(i18n.tr("Assign to Me"), new Command() {
            @Override
            public void execute() {
                assignEmployee(ClientContext.visit(CrmUserVisit.class).getCurrentUser());
            }
        });
        unassignAction = new MenuItem(i18n.tr("Unassign"), new Command() {
            @Override
            public void execute() {
                assignEmployee(null);
            }
        });
        assignOwnershipAction = new MenuItem(i18n.tr("Assign Owner"), new Command() {
            @Override
            public void execute() {
                new EmployeeSelectorDialog(form.getParentView()).show();
            }
        });

        threadStatusActions = new HashMap<ThreadStatus, MenuItem>();

        for (final ThreadStatus ts : ThreadStatus.values()) {
            if (ts.equals(ThreadStatus.Unassigned)) {
                continue;
            }
            threadStatusActions.put(ts, new MenuItem(ThreadStatus.Open.equals(ts) ? ThreadStatus.Open.toString() : i18n.tr("Resolve"), new Command() {
                @Override
                public void execute() {
                    new UpdateThreadStatusBox(form, form.getValue().status().getValue().toString(), ts.toString()) {
                        @Override
                        public boolean onClickOk() {
                            if (validate()) {
                                getValue().thread().set(form.getValue().thread());
                                ((MessageViewerView.Presenter) form.getParentView().getPresenter()).saveMessage(getValue(), ts, true);
                                return true;
                            } else {
                                return false;
                            }
                        }
                    }.show();
                }
            }));
        }

        addAction(assignToMeAction);
        addAction(unassignAction);
        addAction(assignOwnershipAction);
        for (MenuItem action : threadStatusActions.values()) {
            addAction(action);
        }

        MenuItem btnPrint = new MenuItem(i18n.tr("Print"), new Command() {
            @Override
            public void execute() {
                PrintUtils.print(MessageViewerViewImpl.this.getForm().getPrintableElement());
            }
        });

        addAction(btnPrint);
    }

    @Override
    public void reset() {
        for (MenuItem action : threadStatusActions.values()) {
            setActionVisible(action, false);
        }
        super.reset();
    }

    @Override
    public void populate(MessageDTO value) {
        super.populate(value);

        boolean invisible = !MessageGroupCategory.Ticket.equals(value.topic().category().getValue()) || value.isDirect().getValue(false).booleanValue();
        setActionVisible(assignOwnershipAction, !invisible);
        for (ThreadStatus status : threadStatusActions.keySet()) {
            MenuItem action = threadStatusActions.get(status);
            if (status.equals(value.status().getValue())) {
                setActionVisible(action, false);
            }
            // only Open, Resolved;
            else {
                setActionVisible(action, !invisible);
            }
        }
        setActionVisible(assignToMeAction, !invisible && !ClientContext.getUserVisit().getName().equals(value.owner().name().getValue()));
        setActionVisible(unassignAction, !invisible && !ContactType.System.equals(value.owner().type().getValue()));
        setCaption(value.topic().category().getValue() + " " + value.getStringView());
    }

    public void assignEmployee(IEntity e) {
        ((MessageForm) getForm()).assignOwnership(e);

    }

    private class EmployeeSelectorDialog extends EntitySelectorTableVisorController<Employee> {

        public EmployeeSelectorDialog(IPane parentView) {
            super(parentView, Employee.class, false, null, i18n.tr("Select Employee"));
        }

        @Override
        public void onClickOk() {
            assignEmployee(getSelectedItem());
        }

        @Override
        protected List<ColumnDescriptor> defineColumnDescriptors() {
            return Arrays.asList(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().employeeId()).build(),
                    new MemberColumnDescriptor.Builder(proto().name()).searchable(false).build(),
                    new MemberColumnDescriptor.Builder(proto().title()).build(),
                    new MemberColumnDescriptor.Builder(proto().name().firstName()).searchableOnly().build(),
                    new MemberColumnDescriptor.Builder(proto().name().lastName()).searchableOnly().build(),
                    new MemberColumnDescriptor.Builder(proto().email(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().updated(), false).build()
            ); //@formatter:on
        }

        @Override
        public List<Sort> getDefaultSorting() {
            return Arrays.asList(new Sort(proto().employeeId(), false));
        }

        @Override
        protected AbstractListCrudService<Employee> getSelectService() {
            return GWT.<AbstractListCrudService<Employee>> create(SelectEmployeeListService.class);
        }
    }

    static abstract class UpdateThreadStatusBox extends OkCancelDialog {

        private CForm<MessageDTO> content;

        private final String oldStatus;

        private final String newStatus;

        public UpdateThreadStatusBox(final MessageForm form, String oldStatus, String newStatus) {
            super(i18n.tr("Update Status to: ") + newStatus);
            this.oldStatus = oldStatus;
            this.newStatus = newStatus;
            setBody(createBody(form));
        }

        protected Widget createBody(final MessageForm form) {
            getOkButton().setEnabled(true);

            content = new CForm<MessageDTO>(MessageDTO.class) {
                @Override
                protected IsWidget createContent() {
                    FormPanel main = new FormPanel(this);
                    main.append(Location.Dual, inject(proto().text())).decorate().customLabel(i18n.tr("Comment"));

                    return main;
                }

                @Override
                public void addValidations() {
                    super.addValidations();
                }
            };

            content.init();
            MessageDTO ms = EntityFactory.create(MessageDTO.class);
            ms.text().setValue("Status was changed from '" + oldStatus + "' to '" + newStatus + "'.\r\nReason:\r\n");
            content.populate(ms);
            return content.asWidget();
        }

        public MessageDTO getValue() {
            return content.getValue();
        }

        public boolean validate() {
            content.setVisitedRecursive();
            return getValue().text() != null && !getValue().text().isNull();
        }
    }
}
