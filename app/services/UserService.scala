package services

import models.{AccessToken, Post, User}
import play.api.Logger
import securesocial.core._
import securesocial.core.providers.Token

/*
UserService used to fetch information about a user.
Currently all user data is being stored in memory and as such
is not suitable for a production environment.
 */

object UserService {

  //Maps a userId to a user
  private var users = Map[String, User]()

  //Stores the token used by SecureSocial for user login
  private var userSecureSocialToken = Map[String, Token]()

  //Stores the tokens for each of the social providers the user has logged into
  private var userAccessTokens = Map[String, Seq[AccessToken]]()

  def getUser(userId: String): Option[Identity] = {
    if (Logger.isDebugEnabled) {
      Logger.debug("users = %s".format(users))
    }
    users.get(userId)
  }

  def getUserIdFromIdentityId(identityId: IdentityId): String = {
    identityId.userId + identityId.providerId
  }

  def getUserByEmailAndProvider(email: String, providerId: String): Option[Identity] = {
    if (Logger.isDebugEnabled) {
      Logger.debug("users = %s".format(users))
    }
    users.values.find(u => u.email.map(e => e == email && u.identityId.providerId == providerId).getOrElse(false))
  }

  def save(identity: Identity): Identity = {
    val userId = getUserIdFromIdentityId(identity.identityId)
    val user = User(userId, identity.identityId, identity.firstName, identity.lastName, identity.fullName, identity.email,
      identity.avatarUrl, identity.authMethod, identity.oAuth1Info, identity.oAuth2Info, identity.passwordInfo)
    users += (userId -> user)
    user
  }

  def saveSecureSocialToken(token: Token) {
    userSecureSocialToken += (token.uuid -> token)
  }

  def getSecureSocialToken(token: String): Option[Token] = {
    userSecureSocialToken.get(token)
  }

  def deleteSecureSocialToken(uuid: String) {
    userSecureSocialToken -= uuid
  }

  def deleteSecureSocialTokens() {
    userSecureSocialToken = Map()
  }

  def deleteExpiredSecureSocialTokens() {
    userSecureSocialToken = userSecureSocialToken.filter(!_._2.isExpired)
  }

  def addUserAccessToken(userId: String, accessToken: AccessToken) {
    var accessTokens = userAccessTokens.getOrElse(userId, List[AccessToken]())
    accessTokens :+= accessToken
    userAccessTokens += (userId -> accessTokens)
  }

  def getUserAccessTokens(userId: String): Seq[AccessToken] = {
    userAccessTokens.getOrElse(userId, List[AccessToken]())
  }

}
