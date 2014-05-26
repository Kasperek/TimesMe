package services

import models.{Provider, AccessToken, Post}
import models.Provider.Provider

object PostService {

  /*
  Retrieves social media posts from all outlets for passed in accessTokens
   */
  def getPostsForUser(accessTokens: Seq[AccessToken]): Map[Provider, Seq[Post]] = {
    if (accessTokens.isEmpty) {
      Map[Provider, List[Post]]()
    }
    else {
      var postMap = Map[Provider, Seq[Post]]()
      for (accessToken <- accessTokens) {
        accessToken.provider match {
          case Provider.Facebook => {
            postMap += (Provider.Facebook -> FacebookService.getUserPosts(accessToken.token))
          }
          case _ => {
          }
        }
      }
      postMap
    }

  }
}