/* Project: continuum
 * File: RoleCheckingSubscriptionRegistry.java
 * Created: Apr 05, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.websockets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.broker.DefaultSubscriptionRegistry;
import org.springframework.messaging.simp.user.SimpSubscription;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
public class SecureSubscriptionRegistry extends DefaultSubscriptionRegistry
{

  private final UserDetailsManager userManager;
  private final SimpUserRegistry userRegistry;

  @Autowired
  public SecureSubscriptionRegistry(final UserDetailsManager inUserManager,
                                    final SimpUserRegistry inUserRegistry)
  {
    userManager = inUserManager;
    userRegistry = inUserRegistry;
  }

  @Override
  protected MultiValueMap<String, String> findSubscriptionsInternal(final String destination,
                                                                    final Message<?> message)
  {
    MultiValueMap<String, String> subscriptions =
        super.findSubscriptionsInternal(destination, message);
    return filterSubscriptions(subscriptions, message);
  }

  private MultiValueMap<String, String> filterSubscriptions(MultiValueMap<String, String> priorMatches,
                                                            Message<?> message)
  {
    return userRegistry.getUsers()
        .stream()
        .flatMap(u -> u.getSessions().stream())
        .flatMap(s -> s.getSubscriptions().stream())
        .filter(sub -> wasPreviouslyMatched(sub, priorMatches))
        .filter(sub -> userMayReceive(sub.getSession().getUser(), message))
        .collect(LinkedMultiValueMap::new,
            (map, sub) -> map.add(sub.getSession().getId(), sub.getId()),
            MultiValueMap::putAll);
  }

  private boolean wasPreviouslyMatched(final SimpSubscription sub,
                                    final MultiValueMap<String, String> priorMatches)
  {
    return priorMatches.containsKey(sub.getSession().getId())
      && priorMatches.get(sub.getSession().getId()).contains(sub.getId());
  }

  private boolean userMayReceive(final SimpUser inSub, final Message<?> inMessage)
  {
//    User userDetails = (User) userManager.loadUserByUsername(inSub.getName());
//    SecurityContext context = SecurityContextHolder.createEmptyContext();
//    context.setAuthentication(
//        new PreAuthenticatedAuthenticationToken(userDetails, null, userDetails.getAuthorities()));

    // TODO - return false if user should not be allowed to see a message

    return true;
  }
}