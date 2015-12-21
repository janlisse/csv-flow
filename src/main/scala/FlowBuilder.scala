import akka.stream.io.Framing
import akka.stream.scaladsl.Flow
import akka.stream.stage.{SyncDirective, Context, PushPullStage}
import akka.util.ByteString
import com.github.tototoshi.csv.{CSVParser, CSVFormat}

object FlowBuilder {

  private def toLine(implicit format: CSVFormat) = Framing.delimiter(
    ByteString(format.lineTerminator),
    maximumFrameLength = 4096,
    allowTruncation = true
  )

  def csvFlow(implicit format: CSVFormat)  =
    Flow[ByteString].via(toLine).map(_.utf8String).transform(() => new CsvStage())

}

class CsvStage()(implicit format: CSVFormat) extends PushPullStage[String, (Int, Map[String, String])] {

  private val parser = new CSVParser(format)
  private var headers: Option[List[String]] = None
  private var line = 0

  override def onPush(elem: String, ctx: Context[(Int, Map[String, String])]): SyncDirective = {
    val parsed = parser.parseLine(elem)
    line = line + 1
    if (headers.isEmpty) {
      headers = parsed
      ctx.pull()
    } else if (parsed.isEmpty) {
      ctx.pull()
    } else {
      val l = line -> headers.get.zip(parsed.get).toMap
      ctx.push(l)
    }
  }

  override def onPull(ctx: Context[(Int, Map[String, String])]): SyncDirective = ctx.pull()
}
