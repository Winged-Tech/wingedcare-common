package com.wingedtech.common.security;

import com.wingedtech.common.security.oauth2.OAuth2TokenAdditionalInformation;
import com.wingedtech.common.util.GzipUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.util.Base64Utils;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;

import java.util.*;

public class PlatformUserAuthenticationConverter implements UserAuthenticationConverter {

    private Collection<? extends GrantedAuthority> defaultAuthorities;

    private UserDetailsService userDetailsService;

    /**
     * Optional {@link UserDetailsService} to use when extracting an {@link Authentication} from the incoming map.
     *
     * @param userDetailsService the userDetailsService to set
     */
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Default value for authorities if an Authentication is being created and the input has no data for authorities.
     * Note that unless this property is set, the default Authentication created by {@link #extractAuthentication(Map)}
     * will be unauthenticated.
     *
     * @param defaultAuthorities the defaultAuthorities to set. Default null.
     */
    public void setDefaultAuthorities(String[] defaultAuthorities) {
        this.defaultAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(StringUtils
            .arrayToCommaDelimitedString(defaultAuthorities));
    }

    @Override
    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        Map<String, Object> response = new LinkedHashMap<String, Object>();
        response.put(USERNAME, authentication.getName());
        if (authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
            String encoded = getCompressedAuthorities(authentication);
            response.put(AUTHORITIES, encoded);
        }
        return response;
    }

    public static String getCompressedAuthorities(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        byte[] compressData = compressAuthorities(authorities);
        return Base64Utils.encodeToString(compressData);
    }

    public static byte[] compressAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<String> data = AuthorityUtils.authorityListToSet(authorities);
        return GzipUtils.compress(SerializationUtils.serialize(data));
    }

    @Override
    public Authentication extractAuthentication(Map<String, ?> map) {
        if (map.containsKey(USERNAME)) {
            Object principal = map.get(USERNAME);
            Collection<? extends GrantedAuthority> authorities = getAuthorities(map);
            if (userDetailsService != null) {
                UserDetails user = userDetailsService.loadUserByUsername((String) map.get(USERNAME));
                authorities = user.getAuthorities();
                principal = user;
            }
            PlatformUsernamePasswordAuthenticationToken platformUsernamePasswordAuthenticationToken = new PlatformUsernamePasswordAuthenticationToken(principal, "N/A", authorities);

            if (map.containsKey(OAuth2TokenAdditionalInformation.PLATFORM_LOGIN_TYPE)) {
                platformUsernamePasswordAuthenticationToken.setLoginType(map.get(OAuth2TokenAdditionalInformation.PLATFORM_LOGIN_TYPE).toString());
            }

            return platformUsernamePasswordAuthenticationToken;
//            return new UsernamePasswordAuthenticationToken(principal, "N/A", authorities);
        }
        return null;
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Map<String, ?> map) {
        if (!map.containsKey(AUTHORITIES)) {
            return defaultAuthorities;
        }
        Object authorities = map.get(AUTHORITIES);
        if (authorities instanceof String) {
            @SuppressWarnings("unchecked")
            Set<String> authoritiesSource = (HashSet<String>) SerializationUtils.deserialize(GzipUtils.uncompress(Base64Utils.decodeFromString((String) authorities)));
            List<GrantedAuthority> result = new ArrayList<>(authoritiesSource.size());
            authoritiesSource.forEach(authority -> result.add(new SimpleGrantedAuthority(authority)));
            return result;
        }
        if (authorities instanceof Collection) {
            return AuthorityUtils.commaSeparatedStringToAuthorityList(StringUtils
                .collectionToCommaDelimitedString((Collection<?>) authorities));
        }
        throw new IllegalArgumentException("Authorities must be either a String or a Collection");
    }
}
