package services
import com.restfb.DefaultFacebookClient
import collection.JavaConversions._
import models.{Comment, FacebookPost, Post}
import scala.util.control.Breaks
import play.api.cache.Cache
import play.api.Play.current

object FacebookService {

  val CACHE_EXPIRATION_SECONDS = 60
  val MAXIMUM_NUM_POSTS = 10

  /*
  Checks if posts associated to passed in accessToken are stored in the cache,
  if so then it returns what is stored in the cache. Otherwise
  a call to the Facebook api is made, the result
  is inserted into the cache with expiration set to CACHE_EXPIRATION_SECONDS
   */
  def getUserPosts(accessToken: String): Seq[Post] = {

      val postSeq = Cache.getOrElse(accessToken, CACHE_EXPIRATION_SECONDS) {
      val facebookClient = new DefaultFacebookClient(accessToken)
      val myFeed = facebookClient.fetchConnection("me/feed", classOf[com.restfb.types.Post])

      val loop = new Breaks
      var postList = List[Post]()

      loop.breakable {
        for (myFeedConnectionPage <- myFeed) {
          val myFilteredFeedConnectionPage = myFeedConnectionPage.filter(p => p.getMessage != null)
          for (post <- myFilteredFeedConnectionPage) {
            post.getComments match {
              case restfbCommentList: Any => {
                var commentList = List[Comment]()
                for (comment <- restfbCommentList.getData) {
                  commentList :+= Comment(posterName = comment.getFrom.getName, message = comment.getMessage, date = comment.getCreatedTime)
                }
                val fbPost = FacebookPost(posterName = post.getFrom.getName, message = post.getMessage, date = post.getCreatedTime, comments = Some(commentList))
                postList :+= fbPost
              }
              case _ => {
                val fbPost = FacebookPost(posterName = post.getFrom.getName, message = post.getMessage, date = post.getCreatedTime)
                postList :+= fbPost
              }
            }
            if (postList.size == MAXIMUM_NUM_POSTS) loop.break
          }
        }
      }
      postList
    }
    postSeq
  }

}
