  package services

  import models.{Provider, AccessToken, Post}
  import models.Provider.Provider

  object PostService {

    def getPostsForUser(accessTokens: List[AccessToken]): Map[Provider, Seq[Post]] = {
      if (accessTokens.isEmpty) {
        Map[Provider, Seq[Post]]()
      }
      else {
        var postMap: Map[Provider, Seq[Post]] = Map[Provider, List[Post]]()

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