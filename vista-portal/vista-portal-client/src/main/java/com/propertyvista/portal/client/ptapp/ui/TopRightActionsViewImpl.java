package com.propertyvista.portal.client.ptapp.ui;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.StringDebugId;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.site.client.place.AppPlaceInfo;

public class TopRightActionsViewImpl extends SimplePanel implements TopRightActionsView {

    private static I18n i18n = I18nFactory.getI18n(TopRightActionsViewImpl.class);

    private final HorizontalPanel linksPanel;

    public TopRightActionsViewImpl() {
        linksPanel = new HorizontalPanel();
        linksPanel.getElement().getStyle().setMargin(4, Unit.PX);
        linksPanel.getElement().getStyle().setFontSize(0.9, Unit.EM);
        setWidget(linksPanel);
    }

    @Override
    public void setPresenter(final Presenter presenter) {
        for (final AppPlaceInfo action : presenter.getActionsPlacesInfo()) {
            CHyperlink link = new CHyperlink(null, new Command() {
                @Override
                public void execute() {
                    String resource = action.getResource();
                    //TODO: display resource!!!
                }
            });
            link.setDebugId(new StringDebugId(action.getResource()));
            link.setValue(i18n.tr(action.getNavigLabel()));
            linksPanel.add(link);

            linksPanel.add(new HTML("&nbsp;-&nbsp;"));
        }

        CHyperlink logout = new CHyperlink(null, new Command() {
            @Override
            public void execute() {
            }
        });
        logout.setDebugId(new StringDebugId("logout"));
        logout.setValue(i18n.tr("LogOut"));
        linksPanel.add(logout);
    }
}
