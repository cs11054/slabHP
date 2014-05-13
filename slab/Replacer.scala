import scala.swing.SimpleSwingApplication
import scala.swing.MainFrame
import scala.swing.BoxPanel
import scala.swing.Orientation
import scala.swing.GridPanel
import java.awt.Dimension
import scala.swing.Label
import scala.swing.TextField
import scala.swing.Button
import scala.swing.event.ButtonClicked
import java.io.File
import scala.io.Source
import java.io.PrintWriter

object Replacer extends SimpleSwingApplication {
  val msgLabel = new Label("指定したPath以下のファイルを全て検索して置換するよ、危険なプログラムだよ")
  val extStr = new TextField
  def ext = extStr.peer.getText()

  def top = new MainFrame {
    title = "Replacer"
    val path = new TextField
    val trgStr = new TextField
    val repStr = new TextField
    minimumSize = new Dimension(300, 200)
    contents = new BoxPanel(Orientation.Vertical) {
      contents += new GridPanel(4, 4) {
        maximumSize = new Dimension(500, 100)
        contents += new Label("Path")
        contents += path
        contents += new Label("Search")
        contents += trgStr
        contents += new Label("Replace")
        contents += repStr
        contents += new Label("拡張子(例 .txt)")
        contents += extStr
      }
      contents += new Button("Exec") {
        reactions += { case e: ButtonClicked => replace(path.peer.getText(), trgStr.peer.getText(), repStr.peer.getText()) }
      }
      contents += msgLabel
    }

  }

  def replace(path: String, trg: String, rep: String) {
    val repedList = scala.collection.mutable.ArrayBuffer[String]()
    def doReplace(file: File) {
      val lines = Source.fromFile(file).getLines.toList
      var i = 0
      var f = false
      val contents = for (line <- lines) yield {
        i += 1
        println(line, trg, line.contains(trg))
        if (line.contains(trg)) {
          f = true
          repedList += ("[" + file.getName() + ":" + i + "]")
          line.replaceAll(trg, rep)
        } else {
          line
        }
      }
      if (f) {
        val out = new PrintWriter(file)
        contents.foreach(x => out.println(x))
        out.close
      }
    }
    def replacing(path: String): Unit = new File(path) match {
      case file if file.isFile() && file.getName.endsWith(ext) => doReplace(file)
      case dir if dir.isDirectory() => dir.list().foreach(n => replacing(dir.getPath() + "/" + n))
      case _ =>
    }

    if (!new File(path).exists()) {
      msg("path missing!")
      return
    }
    replacing(path)
    msg("置換完了\n" + repedList.mkString("\n"))
    println(path, trg, rep)
  }

  def msg(str: String) { msgLabel.peer.setText(str) }

}