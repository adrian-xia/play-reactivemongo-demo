package models

import play.api.data.Form
import play.api.data.Forms._

/**
 * @author adrian
 */
object LoginForm {
  
  case class LoginData(username: String, password: String)
  
  val form = Form(
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    ) {((username, password) => LoginData(username, password) )} 
    { (loginData=>
      Some(
        (
          loginData.username,
          loginData.password
        )))
    }
  )
  
}