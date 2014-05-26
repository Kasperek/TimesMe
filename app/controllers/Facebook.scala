package controllers

import play.api.libs.ws._
import scala.concurrent.{Await, Future}
import play.api.mvc._;
import play.api.libs.ws.WS.WSRequestHolder
import scala.concurrent.duration.Duration
import play.api.Play
import play.api.Play.current
import services.UserService
import models.{Provider, AccessToken}

object Facebook extends Controller with securesocial.core.SecureSocial {

  val APP_ID = Play.configuration getString "fb.appID" getOrElse ""
  val APP_SECRET = Play.configuration getString "fb.appSecret" getOrElse ""
  val ACCESSTOKEN_URL = Play.configuration getString "fb.accessTokenUrl" getOrElse ""
  val CALLBACK_URL = Play.configuration getString "fb.callbackUrl" getOrElse ""
  val LOGIN_URL = Play.configuration getString "fb.loginUrl" getOrElse ""

  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  def login = SecuredAction {
    implicit request =>
      val queryString = Map("client_id" -> List(APP_ID), "redirect_uri" -> List(CALLBACK_URL), "scope" -> List("email, read_stream"))
      Redirect(LOGIN_URL, queryString)
  }

  /*
  Takes in code returned by Facebook and uses it to generate
  a user accessToken and store it, the user is then redirected
  to the main page.
   */
  def postLogin(code: String) = SecuredAction {
    implicit request =>
      val accessTokenRequest: WSRequestHolder = WS.url(ACCESSTOKEN_URL)
      val futureResponse = accessTokenRequest.withQueryString("client_id" -> APP_ID, "redirect_uri" -> CALLBACK_URL, "client_secret" -> APP_SECRET, "code" -> code).get
      val response = Await.result(futureResponse, Duration(10, "seconds"))
      val accessTokenRegex = "access_token=(.*)&expires=(.*)".r
      response.body match {
        case accessTokenRegex(accessToken, expires) => {
          val token = AccessToken(Provider.Facebook, accessToken, expires.toLong)
          UserService.addUserAccessToken(request.user.identityId.userId + request.user.identityId.providerId, token)
          Redirect(controllers.routes.Application.index)
        }
        case _ => {
          Redirect(controllers.routes.Application.index)
        }
      }
  }

}