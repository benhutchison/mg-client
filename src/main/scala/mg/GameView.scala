package mg

import domain._

import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import scala.util.{Try, Failure, Success}
import scalatags.JsDom.all._

import autowire._

import scalajs.concurrent.JSExecutionContext.Implicits.queue


case class GameView(playerName: String, game: Game, mg: MemoryGame)
  extends View {

  val NoopHandler = () => ()

  val yourTurn = game.currentPlayerName == playerName

  val nextGameHandler: Try[Game] => Unit = {
    case Success(nextGame) => {
      println("Updated game state received")
      mg.show(new GameView(playerName, nextGame, mg))
    }
    case Failure(ex) =>
      mg.show(new LobbyView(playerName, mg))
  }

  val view: HTMLElement   = {
    if (!yourTurn)
      observeGame()
    buildView()
  }

  def buildView(): HTMLElement = {
    div(
      p(s"Game View. Your Turn=$yourTurn "),
      span(game.toString),
      div()(
        game.cards.map(card => {

          val revealed = game.revealed.contains(card)
          val matched = game.matched.contains(card)
          val text = if (revealed || matched) s"${card.num}" else s"?"
          val color = if (revealed)
            "#cccc99"
          else if (matched)
            "#ddbbbb"
          else
            "#aabbdd"

          val handler = if (!yourTurn || matched)
            NoopHandler
          else
            () => cardClicked(card)

          div(
            text,
            backgroundColor := color,
            margin := "20px",
            width := "40px",
            height := "30px",
            display := "inline-block",
            onclick := handler
          )
        })
      )
    ).render
  }

  def cardClicked(card: Card) = {
    println(s"Card clicked: ${card.num}")
    MyClient[Api].reveal(game, game.currentPlayerId, card).call().onComplete(nextGameHandler)
  }

  def observeGame() = {
    MyClient[Api].waitOnUpdate(game).call().onComplete(nextGameHandler)
  }

}
