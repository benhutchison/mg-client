package mg

import scala.collection.SortedMap
import scala.scalajs.js
import js.Dynamic.{global => g, _}
import js.annotation.JSExport

import org.scalajs.dom
import org.scalajs.dom.extensions.Ajax
import org.scalajs.dom.{CanvasRenderingContext2D => Cx2D, HTMLCanvasElement, XMLHttpRequest}

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import prickle._

import autowire._

import org.scalajs.dom
import scalatags.JsDom.all._

@JSExport
object MemoryGane {

  @JSExport
  def main() {

    val doc = js.Dynamic.global.document

    val api = MyClient[Api]

    doc.getElementById("root").appendChild(
      div(
        button(
          onclick := (() => {
          })
        )("Run GameStep")
      ).render
    )

  }

  def msg(s: String): Unit = {
    val paragraph = js.Dynamic.global.document.createElement("p")
    paragraph.innerHTML = s"<strong>$s</strong>"
    g.document.getElementById("root").appendChild(paragraph)
  }

}

object MyClient extends autowire.Client[String, Unpickler, Pickler]{
  def write[Result: Pickler](r: Result): String = Pickle.intoString(r)
  def read[Result: Unpickler](p: String): Result = Unpickle[Result].fromString(p).get

  override def doCall(req: Request): concurrent.Future[String] = {
    dom.extensions.Ajax.post(
      url = "http://localhost:8080/api/" + req.path.mkString("/"),
      data = Pickle.intoString(req.args)
    ).map(_.responseText)
  }
}