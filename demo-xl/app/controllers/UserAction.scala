package controllers

import javax.inject.Inject
import models.User
import models.User._
import play.api._
import play.api.libs.json.Json
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import play.api.mvc._
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.ReactiveMongoComponents
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.ImplicitBSONHandlers._
import play.modules.reactivemongo.json.collection._
import reactivemongo.api.gridfs._
import play.mvc.Results.Redirect
import play.api.i18n.MessagesApi

/**
 * @author adrian
 */
class UserAction @Inject()( val messagesApi: MessagesApi, val reactiveMongoApi: ReactiveMongoApi ) 
            extends Controller with MongoController with ReactiveMongoComponents{
  
  
  import java.util.UUID
  
  type JSONReadFile = ReadFile[JSONSerializationPack.type, JsString]
  
  val collection = db[JSONCollection]("users")
    
  
  def regist = Action {
    Ok(views.html.regist("注册页面!"))
  }
  
  def login = Action {
    Ok(views.html.login(models.form.LoginForm.loginForm))
  }
  
  def loginSubmit = Action { implicit request =>
    val username = request.getQueryString("username")
    val query = Json.obj("username"->username)
    val user = collection.find(query)
    
    play.Logger.debug("user = " + user)
    
    Ok(views.html.login(models.form.LoginForm.loginForm))
  }
  
}