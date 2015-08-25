package scala.cli

import java.io.{File, InputStream, PrintWriter}
import jline.console.ConsoleReader

class JLineReader {

  private val reader: ConsoleReader = createReader()

  def readLine(prompt: String, mask: Option[Char] = None): Option[String] = {
    JLineReader.withJLineTerminal {
      val result = mask match {
        case Some(m) => reader.readLine(prompt, m)
        case None => reader.readLine(prompt)
      }
      Option(result)
    }
  }

  private def createReader(): ConsoleReader = {
    val reader = new ConsoleReader
    reader.setExpandEvents(false)
    reader.setBellEnabled(false)
    reader
  }

  private def withJLineTerminal[A](f: => A): A = synchronized {
    val t = jline.TerminalFactory.get
    t.synchronized {
      t.init
      try {
        f
      } finally {
        t.restore
      }
    }
  }
}

object JLineReader extends JLineReader
