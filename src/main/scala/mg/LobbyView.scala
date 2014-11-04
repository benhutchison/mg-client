package mg

import domain._

import autowire._

import org.scalajs.dom.HTMLElement

import scala.util._
import scalatags.JsDom.all._

import scalajs.concurrent.JSExecutionContext.Implicits.queue

case class LobbyView(playerName: String, mg: MemoryGame) extends View {

  def view(): HTMLElement = {
    val status = span()
    div(
      h1("MMOMG"),
      p(s"Player: $playerName"),
      button(
        "New Game",
        onclick := { () =>
          status("Waiting for match up")
          MyClient[Api].join(playerName).call().onComplete(_ match {
            case Success(game) => {
              mg.show(GameView(playerName, game, mg))
            }
            case Failure(ex) => status(s"Server Error: $ex")
          })
        }
      ),
      status
    ).render
  }
}
