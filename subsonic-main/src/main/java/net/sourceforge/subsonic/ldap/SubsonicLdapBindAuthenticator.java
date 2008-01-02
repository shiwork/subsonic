package net.sourceforge.subsonic.ldap;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.User;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.service.SettingsService;
import org.acegisecurity.BadCredentialsException;
import org.acegisecurity.ldap.DefaultInitialDirContextFactory;
import org.acegisecurity.ldap.search.FilterBasedLdapUserSearch;
import org.acegisecurity.providers.ldap.LdapAuthenticator;
import org.acegisecurity.providers.ldap.authenticator.BindAuthenticator;
import org.acegisecurity.userdetails.ldap.LdapUserDetails;

import java.util.HashMap;
import java.util.Map;

/**
 * LDAP authenticator which uses a delegate {@link BindAuthenticator}, and which
 * supports dynamically changing LDAP provider URL and search filter.
 *
 * @author Sindre Mehus
 */
public class SubsonicLdapBindAuthenticator implements LdapAuthenticator {

    private static final Logger LOG = Logger.getLogger(SubsonicLdapBindAuthenticator.class);

    private SecurityService securityService;
    private SettingsService settingsService;

    private long authenticatorTimestamp;
    private BindAuthenticator delegateAuthenticator;

    @Override
    public LdapUserDetails authenticate(String username, String password) {

        // User must be defined in Subsonic, and LDAP authentication must be
        // enabled both globally and for the given user.
        if (!settingsService.isLdapEnabled()) {
            throw new BadCredentialsException("LDAP authentication disabled.");
        }
        User user = securityService.getUserByName(username);
        if (user == null) {
            throw new BadCredentialsException("User does not exist.");
        }
        // TODO
//        if (!user.isLdapEnabled()) {
//            throw new BadCredentialsException("LDAP authentication disabled for user.");
//        }

        try {
            createDelegate();
            LdapUserDetails details = delegateAuthenticator.authenticate(username, password);
            if (details != null) {
                LOG.info("User '" + username + "' successfully authenticated in LDAP. DN: " + details.getDn());
            }

            return details;
        } catch (RuntimeException x) {
            LOG.info("Failed to authenticate user '" + username + "' in LDAP.", x);
            throw x;
        }
    }

    /**
     * Creates the delegate {@link BindAuthenticator}.
     */
    private void createDelegate() {

        // Only create it if necessary.
        if (delegateAuthenticator == null || authenticatorTimestamp < settingsService.getSettingsLastChanged()) {

            DefaultInitialDirContextFactory contextFactory = new DefaultInitialDirContextFactory(settingsService.getLdapUrl());
            Map<String, String> extraEnvVars = new HashMap<String, String>();
            extraEnvVars.put("java.naming.referral", "follow");
            contextFactory.setExtraEnvVars(extraEnvVars);

            FilterBasedLdapUserSearch userSearch = new FilterBasedLdapUserSearch("", settingsService.getLdapSearchFilter(), contextFactory);
            userSearch.setSearchSubtree(true);
            userSearch.setDerefLinkFlag(true);

            delegateAuthenticator = new BindAuthenticator(contextFactory);
            delegateAuthenticator.setUserSearch(userSearch);

            authenticatorTimestamp = settingsService.getSettingsLastChanged();
        }
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
}
