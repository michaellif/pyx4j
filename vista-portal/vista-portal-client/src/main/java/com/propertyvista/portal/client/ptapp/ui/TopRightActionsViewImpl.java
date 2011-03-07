package com.propertyvista.portal.client.ptapp.ui;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.inject.Inject;
import com.propertyvista.portal.rpc.pt.SiteMap;

import com.pyx4j.commons.StringDebugId;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.site.rpc.AppPlace;

public class TopRightActionsViewImpl extends VerticalPanel implements TopRightActionsView {

    public enum Theme {
        Gainsboro, VillageGreen
    }

    private static I18n i18n = I18nFactory.getI18n(TopRightActionsViewImpl.class);

    private final HorizontalPanel topLinksPanel;

    private final HorizontalPanel bottomLinksPanel;

    private Presenter presenter;

    private final HTML greetings;

    private final CHyperlink logout;

    private final CHyperlink login;

    private final CHyperlink themes;

    private Theme otherTheme = Theme.VillageGreen;

    @Inject
    public TopRightActionsViewImpl() {
        getElement().getStyle().setFontSize(0.9, Unit.EM);

        topLinksPanel = new HorizontalPanel();
        topLinksPanel.getElement().getStyle().setMargin(4, Unit.PX);
        add(topLinksPanel);

        bottomLinksPanel = new HorizontalPanel();
        bottomLinksPanel.getElement().getStyle().setMargin(4, Unit.PX);
        add(bottomLinksPanel);

        greetings = new HTML("");
        topLinksPanel.add(greetings);

        logout = new CHyperlink(null, new Command() {
            @Override
            public void execute() {
                presenter.logout();
            }
        });
        logout.setDebugId(new StringDebugId("logout"));
        logout.setValue(i18n.tr("LogOut"));
        logout.setVisible(false);
        topLinksPanel.add(logout);

        login = new CHyperlink(null, new Command() {
            @Override
            public void execute() {
                presenter.login();
            }
        });
        login.setDebugId(new StringDebugId("login"));
        login.setValue(i18n.tr("LogIn"));
        topLinksPanel.add(login);

        topLinksPanel.add(new HTML("&nbsp;-&nbsp;"));

        themes = new CHyperlink(null, new Command() {
            @Override
            public void execute() {
                presenter.setTheme(otherTheme);
                otherTheme = (otherTheme == Theme.VillageGreen ? Theme.Gainsboro : Theme.VillageGreen);
                themes.setValue(otherTheme.name());
            }
        });
        themes.setValue(otherTheme.name());

        themes.setDebugId(new StringDebugId("themes"));
        topLinksPanel.add(themes);
    }

    @Override
    public void setPresenter(final Presenter presenter) {
        this.presenter = presenter;
    }

    private class NavigLink extends CHyperlink {

        public NavigLink(String name, final AppPlace place) {
            super(null, new Command() {
                @Override
                public void execute() {
                    presenter.getPlaceController().goTo(place);
                }
            });
            setDebugId(new StringDebugId(name));
            setValue(i18n.tr(name));
        }

    }

    @Override
    public void onLogedOut() {
        logout.setVisible(false);
        login.setVisible(true);
        greetings.setHTML("");
    }

    @Override
    public void onLogedIn(String userName) {
        logout.setVisible(true);
        login.setVisible(false);
        greetings.setHTML("Hello " + userName + "&nbsp;-&nbsp;");
    }
}
