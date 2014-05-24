package models

import models.Provider.Provider

case class AccessToken(
                 provider: Provider,
                 token: String)

object Provider extends Enumeration {
  type Provider = Value
  val Facebook, LinkedIn = Value
}