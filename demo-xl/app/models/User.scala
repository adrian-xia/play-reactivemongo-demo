package models

import play.api.data._
import play.api.data.Forms.{ text, longNumber, mapping, nonEmptyText, optional, email }
import play.api.data.validation.Constraints.pattern


/**
 * @author adrian
 */

case class User(
    id: Option[String],
    username: String,
    password: String,
    email: String
    )

object User {
  
  import play.api.libs.json._
  
  implicit object UserWrites extends OWrites[User] {
    def writes(user: User): JsObject = Json.obj(
      "_id" -> user.id,
      "username" -> user.username,
      "password" -> user.password,
      "email" -> user.email)
  }
  
    implicit object UserReads extends Reads[User] {
    def reads(json: JsValue): JsResult[User] = json match {
      case obj: JsObject => try {
        val id = (obj \ "_id").asOpt[String]
        val username = (obj \ "username").as[String]
        val password = (obj \ "password").as[String]
        val email = (obj \ "email").as[String]
        JsSuccess(User(id, username, password, email))
        
      } catch {
        case cause: Throwable => JsError(cause.getMessage)
      }

      case _ => JsError("expected.jsobject")
    }
  }
  
  val registForm = Form(
    mapping(
      "id" -> optional(text verifying pattern(
      """[a-fA-F0-9]{24}""".r, error = "error.objectId")),
      "username" -> nonEmptyText,
      "password" -> nonEmptyText,
      "email" -> email
    )(User.apply)(User.unapply)
  )
}