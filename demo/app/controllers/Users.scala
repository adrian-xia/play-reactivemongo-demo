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
  
  def listUsers = Action.async { implicit request =>
    // get a sort document (see getSort method for more information)
    
    // build a selection document with an empty query and a sort subdocument ('$orderby')
    val query = Json.obj("$query" -> Json.obj())

    // the cursor of documents
    val found = collection.find(query).cursor[User]
    // build (asynchronously) a list containing all the articles
    found.collect[List]().map { users =>
      Ok(views.html.listUsers(users))
    }.recover {
      case e =>
        e.printStackTrace()
        BadRequest(e.getMessage())
    }
  }
  
  def showRegistPage = Action { request =>
    implicit val messages = messagesApi.preferred(request)

    Ok(views.html.regist(User.form))
  }
  
  def registSubmit = TODO
  
}