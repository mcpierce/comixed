/*
 * ComiXed - A digital comic book library management application.
 * Copyright (C) 2017, The ComiXed Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses>
 */

package org.comixedproject.rest.user;

import com.fasterxml.jackson.annotation.JsonView;
import io.micrometer.core.annotation.Timed;
import java.security.Principal;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.comixedproject.model.net.user.*;
import org.comixedproject.model.user.ComiXedUser;
import org.comixedproject.service.user.ComiXedUserException;
import org.comixedproject.service.user.UserService;
import org.comixedproject.views.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * <code>UserController</code> provides endpoints for working with instances of {@link ComiXedUser}.
 *
 * @author Darryl L. Pierce
 */
@RestController
@Log4j2
public class UserController {
  @Autowired private UserService userService;

  /**
   * Loads the current user.
   *
   * @param principal the user principal.
   * @return the user
   * @throws ComiXedUserException if no such user exists
   */
  @GetMapping(value = "/api/user", produces = MediaType.APPLICATION_JSON_VALUE)
  @Timed(value = "comixed.user.get-current")
  @PreAuthorize("hasAnyRole('READER','ADMIN')")
  public ComiXedUser loadCurrentUser(final Principal principal) throws ComiXedUserException {
    log.info("Loading current user: {}", principal.getName());
    return this.userService.findByEmail(principal.getName());
  }

  /**
   * Sets a preference name and value for a user.
   *
   * @param principal the user principal
   * @param name the property name
   * @param request the request body
   * @return the updated user
   * @throws ComiXedUserException if the user is invalid
   */
  @PostMapping(
      value = "/api/user/preferences/{name}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @Timed(value = "comixed.user.save-preference")
  @JsonView(View.UserDetailsView.class)
  @PreAuthorize("hasAnyRole('READER', 'ADMIN')")
  public ComiXedUser saveCurrentUserPreference(
      final Principal principal,
      @PathVariable("name") final String name,
      @RequestBody() final SaveCurrentUserPreferenceRequest request)
      throws ComiXedUserException {
    final String email = principal.getName();
    final String value = request.getValue();

    log.info("Setting user preference: {} {}=\"{}\"", email, name, value);
    return this.userService.setUserProperty(email, name, value);
  }

  /**
   * Deletes a user property.
   *
   * @param principal the user principal
   * @param name the property name
   * @return the updated user
   * @throws ComiXedUserException if the user is invalid
   */
  @DeleteMapping(
      value = "/api/user/preferences/{name}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Timed(value = "comixed.user.delete-preference")
  @JsonView(View.UserDetailsView.class)
  @PreAuthorize("hasAnyRole('READER', 'ADMIN')")
  public ComiXedUser deleteCurrentUserProperty(
      final Principal principal, @PathVariable("name") final String name)
      throws ComiXedUserException {
    final String email = principal.getName();

    log.info("Deleting user preference: {} {}", principal.getName(), name);
    return this.userService.deleteUserProperty(email, name);
  }

  /**
   * Updates the current user.
   *
   * @param id the user record id
   * @param request the request body
   * @return the updated user
   * @throws ComiXedUserException if an error occurs
   */
  @PutMapping(
      value = "/api/user/{id}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @Timed(value = "comixed.user.update-current")
  @JsonView(View.UserDetailsView.class)
  @PreAuthorize("hasRole('READER')")
  public ComiXedUser updateCurrentUser(
      @PathVariable("id") final long id, @RequestBody() final UpdateCurrentUserRequest request)
      throws ComiXedUserException {
    final String email = request.getEmail();
    final String password = request.getPassword();
    log.info("Updated user account: id={} email={}", id, email);
    return this.userService.updateCurrentUser(id, email, password);
  }

  /**
   * Loads the comics read statistics for a user.
   *
   * @param principal the user principal
   * @return the statistics
   * @throws ComiXedUserException if an error occurs
   */
  @GetMapping(
      value = "/api/users/statistics/comics/read",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Timed(value = "comixed.user.comics-read-statistics")
  @JsonView(View.UserStatistics.class)
  @PreAuthorize("hasRole('READER')")
  public List<ComicsReadStatistic> loadComicsReadStatistics(final Principal principal)
      throws ComiXedUserException {
    final String email = principal.getName();
    log.info("Loading comics read statistics: {}", email);
    return this.userService.loadComicsReadStatistics(email);
  }

  /**
   * Returns whether any admin accounts exist.
   *
   * @return the response body
   */
  @GetMapping(value = "/api/users/admins/existing", produces = MediaType.APPLICATION_JSON_VALUE)
  @Timed(value = "comixed.user.existing-accounts")
  public checkForAdminAccountResponse checkForAdmin() {
    log.info("Checking if there are existing user accounts");
    return new checkForAdminAccountResponse(this.userService.hasAdminAccounts());
  }

  /**
   * Creates the initial admin account.
   *
   * @param request the request body
   * @throws ComiXedUserException if an error occurs
   */
  @PostMapping(value = "/api/users/admins", consumes = MediaType.APPLICATION_JSON_VALUE)
  @Timed(value = "comixed.user.create-admin-account")
  public void createAdminAccount(@RequestBody final CreateAdminAccountRequest request)
      throws ComiXedUserException {
    final String email = request.getEmail();
    final String password = request.getPassword();
    log.info("Creating initial admin account: {}", email);
    this.userService.createAdminAccount(email, password);
  }

  /**
   * Returns the list of all user accounts.
   *
   * @return the user account list
   */
  @GetMapping(value = "/api/users/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @Timed(value = "comixed.user.list-accounts")
  @PreAuthorize("hasRole('ADMIN')")
  @JsonView(View.UserList.class)
  public List<ComiXedUser> getUserAccountList() {
    log.info("Getting the user account list");
    return this.userService.getUserAccountList();
  }

  /**
   * Creates a user account.
   *
   * @param request the request body
   * @return the list of users
   * @throws ComiXedUserException if an error occurs
   */
  @PostMapping(
      value = "/api/users/create",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @Timed(value = "comixed.user.create-user")
  @PreAuthorize("hasRole('ADMIN')")
  @JsonView(View.UserList.class)
  public List<ComiXedUser> createUserAccount(@RequestBody final CreateAccountRequest request)
      throws ComiXedUserException {
    final String email = request.getEmail();
    final String password = request.getPassword();
    final boolean admin = request.isAdmin();
    log.info("Creating user account: {} is admin={}", email, admin);
    return this.userService.createUserAccount(email, password, admin);
  }

  /**
   * Updates a user account.
   *
   * @param request the request body
   * @param userId the user id
   * @return the list of users
   * @throws ComiXedUserException if an error occurs
   */
  @PutMapping(
      value = "/api/users/{userId}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @Timed(value = "comixed.user.create-user")
  @PreAuthorize("hasRole('ADMIN')")
  @JsonView(View.UserList.class)
  public List<ComiXedUser> updateUserAccount(
      @RequestBody final CreateAccountRequest request, @PathVariable("userId") final long userId)
      throws ComiXedUserException {
    final String email = request.getEmail();
    final String password = request.getPassword();
    final boolean admin = request.isAdmin();
    log.info("Updating user account: {} is admin={}", email, admin);
    return this.userService.updateUserAccount(userId, email, password, admin);
  }

  /**
   * Deletes a user account.
   *
   * @param userId the user id
   * @throws ComiXedUserException if an error occurs
   */
  @DeleteMapping(value = "/api/users/{userId}")
  @Timed(value = "comixed.user.create-user")
  @PreAuthorize("hasRole('ADMIN')")
  @JsonView(View.UserList.class)
  public void deleteUserAccount(@PathVariable("userId") final long userId)
      throws ComiXedUserException {
    log.info("Deleting user account: {} is admin={}", userId);
    this.userService.deleteUserAccount(userId);
  }
}
