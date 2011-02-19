package com.propertyvista.portal.client.ptapp.ui;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.inject.Inject;
import com.propertyvista.portal.client.ptapp.SiteMap;

import com.pyx4j.commons.StringDebugId;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.site.client.place.AppPlace;
import com.pyx4j.site.client.place.AppPlaceInfo;
import com.pyx4j.site.client.place.AppPlaceListing;

public class TopRightActionsViewImpl extends VerticalPanel implements TopRightActionsView {

    public enum Theme {
        Gainsboro, LightSkyBlue
    }

    private static I18n i18n = I18nFactory.getI18n(TopRightActionsViewImpl.class);

    private final HorizontalPanel topLinksPanel;

    private final HorizontalPanel bottomLinksPanel;

    private Presenter presenter;

    private final HTML greetings;

    private final CHyperlink logout;

    private final CHyperlink login;

    private final CHyperlink themes;

    private Theme otherTheme = Theme.LightSkyBlue;

    @Inject
    public TopRightActionsViewImpl(AppPlaceListing appPlaceListing) {
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
                otherTheme = (otherTheme == Theme.LightSkyBlue ? Theme.Gainsboro : Theme.LightSkyBlue);
                themes.setValue(otherTheme.name());
            }
        });
        themes.setValue(otherTheme.name());

        themes.setDebugId(new StringDebugId("themes"));
        topLinksPanel.add(themes);

        NavigLink link = new NavigLink(appPlaceListing, new SiteMap.PrivacyPolicy());
        bottomLinksPanel.add(link);

        bottomLinksPanel.add(new HTML("&nbsp;-&nbsp;"));

        link = new NavigLink(appPlaceListing, new SiteMap.TermsAndConditions());
        bottomLinksPanel.add(link);

    }

    @Override
    public void setPresenter(final Presenter presenter) {
        this.presenter = presenter;
    }

    private class NavigLink extends CHyperlink {

        public NavigLink(AppPlaceListing listing, final AppPlace place) {
            super(null, new Command() {
                @Override
                public void execute() {
                    presenter.getPlaceController().goTo(place);
                }
            });
            AppPlaceInfo info = listing.getPlaceInfo(place);
            setDebugId(new StringDebugId(info.getCaption()));
            setValue(i18n.tr(info.getCaption()));
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
