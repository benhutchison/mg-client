package mg

import domain._

import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import scala.util.{Try, Failure, Success}
import scalatags.JsDom.all._

import autowire._

import scalajs.concurrent.JSExecutionContext.Implicits.queue


case class GameView(lobby: LobbyView, game: Game, root: HTMLElement) {

  val NoopHandler = () => ()

  val player = game.players.values.find(_.name == lobby.playerName).get
  val yourTurn = game.currentPlayerName == player.name

  val nextGameHandler: Try[Game] => Unit = {
    case Success(nextGame) => {
      println("Updated game state received")
      copy(game = nextGame).show()
    }
    case Failure(ex) =>
      lobby.show()
  }

  def show() = {
    if (game.ended)
      root.setChild(winView.render)
    else {
      if (!yourTurn)
        observeGame()

      root.setChild(playView.render)
    }
  }

  def winView = {
    val winner = game.players(game.winnerId.get)
    val loser = game.opponent(winner.id)
    val lobbyButton = button(
      "Return to Lobby",
      onclick := (() => lobby.show())
    )
    div(
      div(s"Game ended on turn ${game.turn}"),
      lobbyButton,
      div(
        cls := "alert alert-success",
        role := "alert",
        s"${winner.name} won. Score: ${winner.score}"
      ),
      div(
        cls := "alert alert-danger",
        role := "alert",
        s"${loser.name} lost. Score: ${loser.score}"
      )
    )
  }

  def playView: HTMLElement = {
    val concede = button(
      "Concede",
      height := "30px",
      onclick := (() => {

        MyClient[Api].concede(game, player.id).call().foreach(
          nextGame => copy(game = nextGame).show()
        )
      })
    ).render

    div(
      span(cls := s"glyphicon glyphicon-${if (yourTurn) "ok" else "remove"}"),
      p(s"Score ${player.score}"),
      p(s"Opponent ${game.opponent(player.id).name}"),
      if (yourTurn) concede else div(height := "30px"),
      div()(
        game.cards.map(card => {

          val revealed = game.revealed.contains(card)
          val matched = game.matched.contains(card)
          val text = if (revealed || matched) s"${card.num}" else s"?"
          val foreAndBackgroundColors = if (revealed)
            ("#88855", "#cccc99")
          else if (matched)
            ("#997777", "#ddbbbb")
          else
            ("#667799", "#aabbdd")

          val handler = if (!yourTurn || matched)
            NoopHandler
          else
            () => cardClicked(card)

          div(
            textAlign.center,
            text,
            color := foreAndBackgroundColors._1,
            backgroundColor := foreAndBackgroundColors._2,
            borderColor := foreAndBackgroundColors._1,
            borderStyle := "double",
            margin := "20px",
            width := "50px",
            height := "60px",
            display := "inline-block",
            fontSize.`x-large`,
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
