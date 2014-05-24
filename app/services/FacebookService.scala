package services
import scala.concurrent.{Await, Future}
import com.restfb.{FacebookClient, DefaultFacebookClient}
import collection.JavaConversions._
import models.{FacebookPost, Post}
import scala.util.control.Breaks

object FacebookService {

  def getUserPosts(accessToken: String): List[Post] = {
    val facebookClient: FacebookClient = new DefaultFacebookClient(accessToken)
    val myFeed = facebookClient.fetchConnection("me/feed", classOf[com.restfb.types.Post])

    val loop = new Breaks
    var postList: List[Post] = List[Post]()

    loop.breakable {
      for (myFeedConnectionPage <- myFeed) {
        val myFilteredFeedConnectionPage = myFeedConnectionPage.filter(p => p.getMessage != null)
        for (post <- myFilteredFeedConnectionPage) {
          System.out.println(post)
          post.getLikesCount match {
            case numLikes: Any => val fbPost = FacebookPost(posterName = post.getFrom.getName, message = post.getMessage, date = post.getCreatedTime, numLikes = Some(numLikes))
            case _ => val fbPost = FacebookPost(posterName = post.getFrom.getName, message = post.getMessage, date = post.getCreatedTime)
              postList :+= fbPost
          }
        }
        if (postList.size >= 10) loop.break
      }
    }
    postList.slice(0, 10)
  }

}
