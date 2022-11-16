package com.wingedtech.common.security;

import com.google.common.collect.Sets;
import com.wingedtech.common.constant.CommonPermissions;
import com.wingedtech.common.errors.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.zalando.problem.Status;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Spring Security通用工具，提供方便的security信息获取方法
 */
@Slf4j
public final class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * 获取当前用户的login，如果没有login，则抛出BusinessException异常触发HTTP 401错误
     *
     * @return
     */
    public static String getCurrentUserLoginWithException() {
        return getCurrentUserLoginWithException(true);
    }

    /**
     * 获取当前用户的login，如果没有login并且forceLogin为true，则抛出BusinessException异常触发HTTP 401错误
     *
     * @return
     */
    public static String getCurrentUserLoginWithException(boolean forceLogin) {
        Optional<String> userLogin = getCurrentUserLogin();
        if (userLogin.isPresent()) {
            return userLogin.get();
        } else {
            if (forceLogin) {
                throw new BusinessException("UNAUTHORIZED", "当前用户未登录", Status.UNAUTHORIZED);
            }
            return null;
        }
    }

    /**
     * Get the login of the current user.
     *
     * @return the login of the current user
     */
    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
            .map(SecurityUtils::extractUserId);
    }

    /**
     * 从Authentication对象中抽取用户ID标识
     * @param authentication
     * @return
     */
    public static String extractUserId(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
            log.warn("Getting username from UserDetails, 暂不支持, 还需要进一步开发处理: {}", springSecurityUser);
            throw new UnsupportedOperationException("Unable to extract user id from UserDetails!");
        } else if (authentication instanceof JwtAuthenticationToken) {
            return (String) ((JwtAuthenticationToken)authentication).getToken().getClaims().get(IdTokenClaimNames.SUB);
        } else if (authentication.getPrincipal() instanceof DefaultOidcUser) {
            Map<String, Object> attributes = ((DefaultOidcUser) authentication.getPrincipal()).getAttributes();
            if (attributes.containsKey(IdTokenClaimNames.SUB)) {
                return (String) attributes.get(IdTokenClaimNames.SUB);
            }
        } else if (authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * Check if a user is authenticated.
     *
     * @return true if the user is authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
            getAuthorities(authentication).noneMatch(AuthoritiesConstants.ANONYMOUS::equals);
    }

    /**
     * If the current user has a specific authority (security role).
     * <p>
     * The name of this method comes from the isUserInRole() method in the Servlet API
     *
     * @param authority the authority to check
     * @return true if the current user has the authority, false otherwise
     */
    public static boolean isCurrentUserInRole(String authority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
            getAuthorities(authentication).anyMatch(authority::equals);
    }

    public static Collection<? extends GrantedAuthority> getCurrentUserAuthorities() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return securityContext.getAuthentication().getAuthorities();
    }

    /**
     * Gets the login type of current user from token
     * @return
     */
    public static String getCurrentUserLoginType() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        throw new UnsupportedOperationException("暂不支持该操作");
//        if (authentication instanceof OAuth2Authentication) {
//            Authentication userAuthentication = ((OAuth2Authentication) authentication).getUserAuthentication();
//            if (userAuthentication instanceof PlatformUsernamePasswordAuthenticationToken) {
//                return ((PlatformUsernamePasswordAuthenticationToken)userAuthentication).getLoginType();
//            }
//        }
//        return null;
    }

    public static String getUserLoginFromAuthentication(Authentication authentication) {
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
            return springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * 获取当前用户的permissions权限集合
     * @return Set<String>权限集合
     */
    public static Set<String> getCurrentUserPermissions() {
        return getPermissions(getCurrentUserAuthorities());
    }

    /**
     * 从authorities数组里解析出permissions(包含所有ROLE_/PERM_等权限)
     *
     * @param authorities
     * @return
     */
    public static Set<String> getPermissions(Collection<? extends GrantedAuthority> authorities) {
        Set<String> permissions = Sets.newHashSetWithExpectedSize(authorities.size());

        for (GrantedAuthority authority : authorities) {
            permissions.add(authority.getAuthority());
        }
        return permissions;
    }

    /**
     * 权限定义的前缀并不固定, 有时前缀可能不以PERMISSION_PREFIX开头, 应避免使用此方法
     * @param authority
     * @return
     */
    @Deprecated
    public static String parsePermission(String authority) {
        if (authority.startsWith(CommonPermissions.PERMISSION_PREFIX)) {
            return authority;
        } else {
            return null;
        }
    }

    private static Stream<String> getAuthorities(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication instanceof JwtAuthenticationToken ?
            extractAuthorityFromClaims(((JwtAuthenticationToken) authentication).getToken().getClaims())
            : authentication.getAuthorities();
        return authorities.stream()
            .map(GrantedAuthority::getAuthority);
    }

    public static List<GrantedAuthority> extractAuthorityFromClaims(Map<String, Object> claims) {
        return mapRolesToGrantedAuthorities(getRolesFromClaims(claims));
    }

    @SuppressWarnings("unchecked")
    private static Collection<String> getRolesFromClaims(Map<String, Object> claims) {
        return (Collection<String>) claims.getOrDefault("groups",
            claims.getOrDefault("roles", new ArrayList<>()));
    }

    private static List<GrantedAuthority> mapRolesToGrantedAuthorities(Collection<String> roles) {
        return roles.stream()
            .filter(role -> role.startsWith("ROLE_"))
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
    }
}
