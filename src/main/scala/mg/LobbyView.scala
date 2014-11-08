package mg

import domain._

import autowire._
import org.scalajs.dom

import org.scalajs.dom.{Element, HTMLButtonElement, HTMLElement}

import scala.util._
import scalatags.JsDom.all._

import scalajs.concurrent.JSExecutionContext.Implicits.queue

case class LobbyView(val playerName: String, root: HTMLElement) {

  val content = div.render

  def show() = {
    root.setChild(view().render)
  }

  def view(): HTMLElement = {
    val status = span().render
    lazy val joinButton: HTMLButtonElement = button(
      "Join Game",
      onclick := { () =>
        joinButton.textContent = "Waiting for opponent.."

        MyClient[Api].join(playerName).call().onComplete(_ match {
          case Success(game) => {
            new GameView(this, game, content).show()
          }
          case Failure(ex) => status.textContent = s"Server Error: $ex"
        })
      }
    ).render

    MyClient[Api].games().call().map(games => {
      div()(
        games.toSeq.sortBy(_.ended).map(game =>
          div(
            margin := "10px",
            backgroundColor := (if (game.ended) "#ddbbbb" else "#bbbbdd"),
            padding := "10px",
            game.toString()
          )
        )
      )
    }).foreach(gameList => content.appendChild(gameList.render))

    content.setChild(div(
      joinButton, status
    ).render)

    div(
      textAlign.center
    )(
      h1("MM-OMG"),
      p(s"Player: $playerName"),
      content
    ).render
  }
}
