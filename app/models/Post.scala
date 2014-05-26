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
                         comments: Option[List[Comment]] = Some(List[Comment]())
                         ) extends Post

case class Comment(
                    message: String,
                    posterName: String,
                    date: Date
                    ) extends Post