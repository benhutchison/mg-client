package mg

import org.scalajs.dom.{HTMLInputElement, HTMLElement}

import org.scalajs.dom

import scalatags.JsDom.all._

class PlayerNameView(root: HTMLElement)  {

  val inp: HTMLInputElement = input(
    placeholder := "Enter a player name",
    autofocus := true
  ).render

  def show() = {
    root.setChild(view.render)
  }

  def view() = {
    div(
      textAlign.center
    )(
      h1("Welcome to MM-OMG, the Massively Multiplayer- Online Memory Game"),
      ("To begin, choose your player name"),
      form(
        inp,
        onsubmit := { () =>
          val playerName = inp.value
          if (playerName.nonEmpty)
            new LobbyView(playerName, root).show()
          false
        }
      )
    )
  }

}
