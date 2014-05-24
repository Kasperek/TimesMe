/**
 * Copyright 2012 Jorge Aliss (jaliss at gmail dot com) - twitter: @jaliss
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package controllers

import models.{AccessToken, Post, User}
import models.Provider.Provider
import services.{PostService, UserService}
import play.api.mvc._
import securesocial.core.{Identity, Authorization}

object Application extends Controller with securesocial.core.SecureSocial {

  def index = SecuredAction { implicit request =>
    val userAccessTokens: List[AccessToken] = UserService.getUserAccessTokens(request.user.identityId.userId + request.user.identityId.providerId)
    val userPosts: Map[Provider, Seq[Post]] = PostService.getPostsForUser(userAccessTokens)

    Ok(views.html.index(request.user,userPosts))
  }
}