package models.form

import play.api.data._
import play.api.data.Forms._

import play.api.data.Form

/**
 * @author adrian
 */
object LoginForm {
  case class Data(
    username: String,
    password: String
  )
  
  def loginForm = Form(
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    )(Data.apply)(Data.unapply)
  )
}