package models

import org.joda.time.DateTime

import play.api.data._
import play.api.data.Forms.{ text, longNumber, mapping, nonEmptyText, optional, email }
import play.api.data.validation.Constraints.pattern

import reactivemongo.bson.{
  BSONDateTime, BSONDocument, BSONObjectID
}

case class User(
  id: Option[String],
  username: String,
  password: String,
  email: String,
  createtime: Option[DateTime],
  updatetime: Option[DateTime])

  
object User {
  import play.api.libs.json._

  implicit object UserWrites extends OWrites[User] {
    def writes(user: User): JsObject = Json.obj(
      "_id" -> user.id,
      "username" -> user.username,
      "password" -> user.password,
      "email" -> user.email,
      "createtime" -> user.createtime.fold(-1L)(_.getMillis),
      "updatetime" -> user.updatetime.fold(-1L)(_.getMillis))
  }

  implicit object UserReads extends Reads[User] {
    def reads(json: JsValue): JsResult[User] = json match {
      case obj: JsObject => try {
        val id = (obj \ "_id").asOpt[String]
        val username = (obj \ "username").as[String]
        val password = (obj \ "password").as[String]
        val email = (obj \ "email").as[String]
        val createtime = (obj \ "createtime").asOpt[Long]
        val updatetime = (obj \ "updatetime").asOpt[Long]

        JsSuccess(User(id, username, password, email, createtime.map(new DateTime(_)), updatetime.map(new DateTime(_))))
        
      } catch {
        case cause: Throwable => JsError(cause.getMessage)
      }

      case _ => JsError("expected.jsobject")
    }
  }

  val form = Form(
    mapping(
      "id" -> optional(text verifying pattern(
        """[a-fA-F0-9]{24}""".r, error = "error.objectId")),
      "username" -> nonEmptyText,
      "password" -> nonEmptyText,
      "email" -> email,
      "createtime" -> optional(longNumber),
      "updatetime" -> optional(longNumber)) {((
      id, username, password, email, createtime, updatetime) =>
      User(
        id, username, password, email, createtime.map(new DateTime(_)), updatetime.map(new DateTime(_))))
    } { (user =>
      Some(
        (user.id,
          user.username,
          user.password,
          user.email,
          user.createtime.map(_.getMillis),
          user.updatetime.map(_.getMillis))))
    })
}
