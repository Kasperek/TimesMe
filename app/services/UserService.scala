package services

import models.{AccessToken, Post, User}
import play.api.{Logger, Application}
import securesocial.core._
import securesocial.core.providers.Token

object UserService {

  //UserId is used as the key, can be constructed from IdentityId.userId + IdentityId.providerId
  private var users = Map[String, User]()
  private var userSecureSocialToken = Map[String, Token]()
  private var userAccessTokens = Map[String, List[AccessToken]]()

  def getUser(userId: String): Option[Identity] = {
    if ( Logger.isDebugEnabled ) {
      Logger.debug("users = %s".format(users))
    }
    users.get(userId)
  }

  def getUserByEmailAndProvider(email: String, providerId: String): Option[Identity] = {
    if ( Logger.isDebugEnabled ) {
      Logger.debug("users = %s".format(users))
    }
    users.values.find( u => u.email.map( e => e == email && u.identityId.providerId == providerId).getOrElse(false))
  }

  def save(identity: Identity): Identity = {
    val user = User(identity.identityId.userId + identity.identityId.providerId,identity.identityId, identity.firstName, identity.lastName, identity.fullName, identity.email, identity.avatarUrl, identity.authMethod, identity.oAuth1Info, identity.oAuth2Info, identity.passwordInfo)
    users += (identity.identityId.userId + identity.identityId.providerId -> user)
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

  def addUserAccessToken(userId:String, accessToken: AccessToken) {
      var accessTokens: List[AccessToken] = userAccessTokens.getOrElse(userId, List[AccessToken]())
      accessTokens :+= accessToken
      userAccessTokens += (userId -> accessTokens)
  }

  def getUserAccessTokens(userId: String): List[AccessToken] = {
    userAccessTokens.getOrElse(userId, List[AccessToken]())
  }

 // def getUserPostMap(user: User): Map[String, List[Post]] = {
 //   val accessToken = userAccessTokens.get(user).getOrElse("")
 //   Map("facebook" ->FacebookService.getUserPosts(accessToken))
 // }


}
