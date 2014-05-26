package models

import models.Provider.Provider

case class AccessToken(
                        provider: Provider,
                        token: String,
                        expires: Long)

object Provider extends Enumeration {
  type Provider = Value
  val Facebook = Value
}