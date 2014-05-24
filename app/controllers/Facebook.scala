package controllers

import play.api.libs.ws._
import scala.concurrent.{Await, Future}
import play.api.mvc._;
import com.restfb.{Parameter, Connection, FacebookClient, DefaultFacebookClient}
import com.restfb.types._
import play.api.libs.ws.WS.WSRequestHolder
import scala.concurrent.duration.Duration
import play.api.Play
import play.api.Play.current
import collection.JavaConversions._
import services.UserService
import models.{Provider, AccessToken}

object Facebook extends Controller with securesocial.core.SecureSocial {

  val APP_ID = Play.configuration getString "fb.appID" getOrElse ""
  val APP_SECRET = Play.configuration getString "fb.appSecret" getOrElse ""
  val ACCESSTOKEN_URL = Play.configuration getString "fb.accessTokenUrl" getOrElse ""
  val CALLBACK_URL = Play.configuration getString "fb.callbackUrl" getOrElse ""
  val LOGIN_URL = Play.configuration getString "fb.loginUrl" getOrElse "" + APP_ID + "&redirect_uri=" + CALLBACK_URL

  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  def login = SecuredAction { implicit request =>
    Redirect("https://www.facebook.com/dialog/oauth?client_id="+APP_ID+"&redirect_uri="+CALLBACK_URL+"&scope=email,read_stream")
    //Redirect("https://www.facebook.com/dialog/oauth?client_id="+APP_ID+"&redirect_uri="+CALLBACK_URL+"&scope=user_status, friends_status, read_stream, publish_actions, read_requests, read_mailbox, user_likes, friends_likes, user_notes, friends_notes, user_events, friends_events, create_event, user_activities, friends_activities, user_location, friends_location, user_online_presence, email, offline_access&response_type=token")
  }

  def postLogin(code: String) = SecuredAction { implicit request =>
    val accessTokenRequest: WSRequestHolder = WS.url(ACCESSTOKEN_URL)
    val futureResponse = accessTokenRequest.withQueryString("client_id" -> APP_ID, "redirect_uri" -> CALLBACK_URL, "client_secret" -> APP_SECRET, "code" -> code).get
    val response = Await.result(futureResponse, Duration(10, "seconds"))
    val accessTokenRegex = "access_token=(.*)&expires=(.*)".r
    response.body match {
      case accessTokenRegex(accessToken, expires) => {
        val token = AccessToken(Provider.Facebook, accessToken)
        UserService.addUserAccessToken(request.user.identityId.userId + request.user.identityId.providerId, token)
        Redirect(controllers.routes.Application.index)
      }
      case _ => {
        Ok("No access token returned")
      }
    }
  }

}