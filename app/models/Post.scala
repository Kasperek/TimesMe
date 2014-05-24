package models

import java.util.Date

trait Post {
  def message: String
  def posterName: String
  def date: Date
}

case class FacebookPost(
                         message: String,
                         posterName: String,
                         date: Date,
                         numLikes: Option[Long] = Some(0L)
                         ) extends Post