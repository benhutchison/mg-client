package mg

import org.scalajs.dom.{HTMLInputElement, HTMLElement}

import scalatags.JsDom.all._

case class PlayerNameView(mg: MemoryGame) extends View {

  val inp: HTMLInputElement = input(
    placeholder := "Enter a unique player name",
    autofocus := true
  ).render

  def view(): HTMLElement = {
    div(
      form(
        inp,
        onsubmit := { () =>
          val playerName = inp.value
          if (playerName.nonEmpty)
            mg.show(LobbyView(playerName, mg))
          false
        }
      )
    ).render
  }

}
