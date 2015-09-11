package models

import play.api.data.Form
import play.api.data.Mapping


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
  
}