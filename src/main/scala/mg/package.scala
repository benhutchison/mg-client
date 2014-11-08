import org.scalajs.dom
import org.scalajs.dom.{Node, HTMLElement}

import scalatags.JsDom.TypedTag

package object mg {

  type Tag = TypedTag[HTMLElement]


  implicit class RichElement(element: HTMLElement) {

    def setChild(child: Node) = {
      element.innerHTML = ""
      element.appendChild(child)
    }
  }

}
