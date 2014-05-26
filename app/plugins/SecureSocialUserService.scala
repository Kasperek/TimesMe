package plugins

import services.UserService
import play.api.Application
import securesocial.core.providers.Token
import securesocial.core.{Identity, UserServicePlugin, IdentityId}

class SecureSocialUserService(application: Application) extends UserServicePlugin(application) {

  def find(id: IdentityId): Option[Identity] = {
    UserService.getUser(UserService.getUserIdFromIdentityId(id))
  }

  def findByEmailAndProvider(email: String, providerId: String): Option[Identity] = {
    UserService.getUserByEmailAndProvider(email, providerId)
  }

  def save(identity: Identity): Identity = {
    UserService.save(identity)
  }

  def save(token: Token) {
    UserService.saveSecureSocialToken(token)
  }

  def findToken(token: String): Option[Token] = {
    UserService.getSecureSocialToken(token)
  }

  def deleteToken(uuid: String) {
    UserService.deleteSecureSocialToken(uuid)
  }

  def deleteTokens() {
    UserService.deleteSecureSocialTokens()
  }

  def deleteExpiredTokens() {
    UserService.deleteExpiredSecureSocialTokens()
  }
}