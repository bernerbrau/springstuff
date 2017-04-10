/* Project: continuum
 * File: JWTConfig.java
 * Created: Mar 28, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.jwt;

import fj.function.Try0;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;

@Configuration
public class JWTConfig
{
  private static final Logger LOGGER = LoggerFactory.getLogger(JWTConfig.class);

  @Order(1)
  @Bean("jwtSecret")
  @ConditionalOnProperty( prefix="jwt.key",
                          name = {"store-location",
                                  "store-type",
                                  "store-password",
                                  "key-alias",
                                  "key-password"})
  Try0<byte[], GeneralSecurityException> getJWTSecret(KeyStore keyStore,
                                                      Environment env)
      throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException
  {
    return () -> keyStore
                     .getKey(env.getProperty("jwt.key.key-alias"),
                         env.getProperty("jwt.key.key-password").toCharArray())
                     .getEncoded();
  }

  @Order(Ordered.LOWEST_PRECEDENCE)
  @Bean("jwtSecret")
  @ConditionalOnProperty(prefix="jwt.key", name = "fail-if-missing", havingValue = "false")
  Try0<byte[], GeneralSecurityException> getDefaultJWTSecret()
      throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException
  {
    return () ->
    {
      LOGGER.warn(
          "*** JWT Keystore is not configured! Using the default key. This is NOT SECURE!!! ***");
      return new byte[] {
          123, 44, -32, -81, 56, -17, 17, 2, 58, -48, -111, 28, 84, -53, -21, -102,
          -93, 68, 45, 64, -121, 21, -66, 82, -114, 74, -67, -55, -41, 34, 64, -123
      };
    };
  }

  @Bean("keyStore")
  @ConditionalOnProperty(prefix="jwt.key",
                         name= {"store-location",
                           "store-type",
                           "store-password"})
  KeyStore getKeyStore(Environment env)
      throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException
  {
    try (InputStream keystoreStream = new FileInputStream(env.getProperty("jwt.key.store-location")))
    {
      KeyStore ks = KeyStore.getInstance(env.getProperty("jwt.key.store-type"));
      ks.load(keystoreStream, env.getProperty("jwt.key.store-password").toCharArray());
      return ks;
    }
  }

}
