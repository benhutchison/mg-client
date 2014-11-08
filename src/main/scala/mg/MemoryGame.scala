package mg

import org.scalajs.dom
import org.scalajs.dom.{CanvasRenderingContext2D => Cx2D}
import prickle._

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.annotation.JSExport

@JSExport
object Launcher {
  @JSExport
  def main(): Unit = {
    new PlayerNameView(dom.document.getElementById("root")).show()
  }
}

object MyClient extends autowire.Client[String, Unpickler, Pickler]{
  def write[Result: Pickler](r: Result): String = Pickle.intoString(r)
  def read[Result: Unpickler](p: String): Result = Unpickle[Result].fromString(p).get

  override def doCall(req: Request): concurrent.Future[String] = {
    dom.extensions.Ajax.post(
      url = "http://localhost:8080/" + req.path.mkString("/"),
      data = Pickle.intoString(req.args)
    ).map(_.responseText)
  }
}