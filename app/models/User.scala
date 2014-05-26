package models

import securesocial.core._
import securesocial.core.OAuth2Info
import securesocial.core.PasswordInfo
import securesocial.core.OAuth1Info

case class User(
                 id: String,
                 identityId: IdentityId,
                 firstName: String,
                 lastName: String,
                 fullName: String,
                 email: Option[String],
                 avatarUrl: Option[String],
                 authMethod: AuthenticationMethod,
                 oAuth1Info: Option[OAuth1Info],
                 oAuth2Info: Option[OAuth2Info],
                 passwordInfo: Option[PasswordInfo] = None) extends Identity
