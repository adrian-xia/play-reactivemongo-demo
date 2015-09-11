package controllers

import play.api._
import play.api.mvc._

/**
 * @author adrian
 */
class UserAction extends Controller{
  
  def regist = Action {
    Ok(views.html.regist("注册页面!"))
  }
  
  def login = Action {
    Ok(views.html.login("登陆页面!"))
  }
  
}