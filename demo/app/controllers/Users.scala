package controllers

import javax.inject.Inject

import org.joda.time.DateTime

import scala.concurrent.Future

import play.api.Logger
import play.api.Play.current
import play.api.i18n.{ I18nSupport, MessagesApi }
import play.api.mvc.{ Action, Controller, Request }
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{ Json, JsObject, JsString }

import reactivemongo.api.gridfs.{ GridFS, ReadFile }

import play.modules.reactivemongo.{
  MongoController, ReactiveMongoApi, ReactiveMongoComponents
}

import play.modules.reactivemongo.json._, ImplicitBSONHandlers._
import play.modules.reactivemongo.json.collection._

import models.User, User._
import models.LoginForm

/**
 * @author adrian
 */
class Users @Inject()(
  val messagesApi: MessagesApi,
  val reactiveMongoApi: ReactiveMongoApi)
  extends Controller with MongoController with ReactiveMongoComponents {
  import java.util.UUID
  import MongoController.readFileReads
  
  type JSONReadFile = ReadFile[JSONSerializationPack.type, JsString]
  
  val collection = db[JSONCollection]("users")
  
  private val gridFS = reactiveMongoApi.gridFS
  
  gridFS.ensureIndex().onComplete {
    case index =>
      Logger.info(s"Checked index, result is $index")
  }
  /**
   * 登陆页面
   */
  def showLoginPage = Action { request =>
    implicit val messages = messagesApi.preferred(request)

    Ok(views.html.login(LoginForm.form))
  }
  /**
   * 登陆方法
   */
  def loginSubmit = TODO
  /**
   * 展示所有用户列表
   */
  def listUsers = Action.async { implicit request =>
    
    val query = Json.obj("$query" -> Json.obj())

    val found = collection.find(query).cursor[User]
    
    found.collect[List]().map { users =>
      Ok(views.html.listUsers(users))
    }.recover {
      case e =>
        e.printStackTrace()
        BadRequest(e.getMessage())
    }
  }
  /**
   * 注册页面
   */
  def showRegistPage = Action { request =>
    implicit val messages = messagesApi.preferred(request)

    Ok(views.html.regist(null, User.form))
  }
  /**
   * 注册方法，保存用户
   */
  def registSubmit = Action.async { implicit request =>
    implicit val messages = messagesApi.preferred(request)
    
    User.form.bindFromRequest.fold(
      errors => Future.successful(
        Ok(views.html.regist(null, errors))),

      // 没有验证错误，插入注册用户
      user => collection.insert(user.copy(
        id = user.id.orElse(Some(UUID.randomUUID().toString)),
        createtime = Some(new DateTime()),
        updatetime = Some(new DateTime()))
      ).map(_ => Redirect(routes.Users.showLoginPage))
    )
  }
  
  /**
   * 删除用户
   */
  def deleteUser(id: String) = Action.async {
    gridFS.find[JsObject, JSONReadFile](Json.obj("user" -> id)).
      collect[List]().flatMap { files =>
        val deletions = files.map(gridFS.remove(_))
        Future.sequence(deletions)
      }.flatMap { _ =>
        collection.remove(Json.obj("_id" -> id))
      }.map(_ => Ok).recover { case _ => InternalServerError }
  }
  /**
   * 转向编辑页面
   */
  def showEditPage(id: String) = Action.async { request =>
    val futureUser = collection.find(Json.obj("_id" -> id)).one[User]
    for {
      maybeUser <- futureUser
      result <- maybeUser.map { user =>
        gridFS.find[JsObject, JSONReadFile](
          Json.obj("user" -> user.id.get)).collect[List]().map { files =>
          val filesWithId = files.map { file =>
            file.id -> file
          }

          implicit val messages = messagesApi.preferred(request)
          
          Ok(views.html.regist(Some(id),
            User.form.fill(user)))
        }
      }.getOrElse(Future(NotFound))
    } yield result
  }
  
}