package com.jun.plugin.oauth2.jwt.demo.config;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import com.jun.plugin.oauth2.jwt.demo.model.UserModel;
import com.jun.plugin.oauth2.jwt.demo.properties.ClientLoadProperties;
import com.jun.plugin.oauth2.jwt.demo.properties.ClientProperties;
import com.jun.plugin.oauth2.jwt.demo.service.MyUserDetailsServiceImpl;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Wujun
 * @description ???????????????
 * @date 2018/12/25 0025 10:39
 */
@Configuration
@EnableAuthorizationServer
public class MyAuthorizationServerConfig  extends AuthorizationServerConfigurerAdapter{

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private MyUserDetailsServiceImpl userDetailsService;
    @Autowired
    private RedisConnectionFactory connectionFactory;
    @Resource
    private ClientLoadProperties clientLoadProperties;

    /**
     * ??????token???????????????
     *
     * @return TokenStore
     */
    @Bean
    public TokenStore tokenStore() {
        return new RedisTokenStore(connectionFactory);
    }

    /**
     * ???????????????????????????????????? ???
     *
     * @param oauthServer oauthServer defines the security constraints on the token endpoint.
     * @throws Exception exception
     */
    @Override
    public void configure(final AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
        oauthServer.allowFormAuthenticationForClients();

    }


    /**
     * ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     *
     * @param clients a configurer that defines the client details service. Client details can be initialized, or you can just refer to an existing store.
     * @throws Exception exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        InMemoryClientDetailsServiceBuilder builder = clients.inMemory();
        if (ArrayUtils.isNotEmpty(clientLoadProperties.getClients())) {
            for (ClientProperties config : clientLoadProperties.getClients()) {
                builder
                        //????????????????????????
                        .withClient(config.getClientId()).secret(config.getClientSecret())
                        //??????token?????????
                        .accessTokenValiditySeconds(7 * 24 * 3600)
                        //??????refreshToken?????????
                        .refreshTokenValiditySeconds(7 * 24 * 3600)
                        //?????????????????????
                        .authorizedGrantTypes("refresh_token", "authorization_code", "password").autoApprove(false)
                        //?????????
                        .scopes("app","write");
            }
        }

    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param endpoints defines the authorization and token endpoints and the token services.
     * @throws Exception exception
     */
    @Override
    public void configure(final AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                //?????????????????????
                .authenticationManager(authenticationManager)
                //????????????????????????
                .userDetailsService(userDetailsService)
                // refresh_token
                .reuseRefreshTokens(false)
                //??????token????????????
                .tokenStore(tokenStore())
                // ??????JwtAccessToken?????????
                .accessTokenConverter(accessTokenConverter());
    }

    /**
     * ??????jwt???????????????
     *
     * @return JwtAccessTokenConverter
     */
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter() {
            @Override
            public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
                final Map<String, Object> additionalInformation = new HashMap<>();
                UserModel userModel = (UserModel) authentication.getUserAuthentication().getPrincipal();
                //??????????????????uin?????????
                additionalInformation.put("uin", userModel.getUin());
                ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInformation);
                return super.enhance(accessToken, authentication);
            }
        };
        //?????????????????????jwt????????????
//        KeyPair keyPair = new KeyStoreKeyFactory(new ClassPathResource("kevin_key.jks"), "123456".toCharArray())
//                .getKeyPair("kevin_key");
//        converter.setKeyPair(keyPair);
//        return converter;
        //????????????
        converter.setSigningKey("123");
        return converter;
    }


}
