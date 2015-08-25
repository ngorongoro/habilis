package scala
package object cli {

  import org.scalactic._

  def readProperty(key: String): String Or ErrorMessage= {
    val prompt = s"$key: "
    JLineReader.readLine(prompt) match {
      case None => Bad(s"Invalid input. A value for '$key' is required.")
      case Some(value) if value.trim.isEmpty => Bad(s"Invalid input. A value for '$key' is required.")
      case Some(value) => Good(value)
    }
  }

  def readProperty(key: String, default: String): String = {
    val prompt = s"$key [$default]: "
    JLineReader.readLine(prompt) match {
      case None => default
      case Some(value) if value.trim.isEmpty => default
      case Some(value) => value
    }
  }

  def readConfirmation(prompt: String): Boolean Or ErrorMessage = {
    JLineReader.readLine(s"$prompt (y/n)? [n] ") match {
       case None => Good(false)
       case Some("y") | Some("Y") => Good(true)
       case Some("n") | Some("N") => Good(false)
       case Some(_) => Bad("Invalid choice. Select 'y' or 'n'.")
    }
  }

  def readByIndex[A](as: Seq[A], prompt: String, conv: A => String): A Or ErrorMessage = {
    def parseInput(s: String): Option[Int] = util.control.Exception.catching(classOf[NumberFormatException]).opt(s.toInt)
    val asByIndex = as.zipWithIndex
    JLineReader.readLine(
     s"""
      |$prompt
      |${asByIndex.map { case (a, i) => s"[ $i ] ${conv(a)}" }.mkString("\n") }
      |Enter index number:
      """.stripMargin.trim + " "
    ).flatMap(parseInput) match {
      case Some(n) if n < as.size => Good(as(n))
      case Some(_) | None => Bad("Invalid choice. Select by index number.")
    }
  }
}
