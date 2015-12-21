import java.io.File

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.io.SynchronousFileSource
import akka.stream.scaladsl.Sink
import com.github.tototoshi.csv.DefaultCSVFormat

import scala.concurrent.ExecutionContext.Implicits.global


object Example extends App {

  val csvFile = new File("src/main/resources/us-500.csv")

  implicit val system = ActorSystem("AS")
  implicit val materializer = ActorMaterializer()
  implicit object MyFormat extends DefaultCSVFormat {
    override val lineTerminator = "\r"
  }

  SynchronousFileSource(csvFile).via(FlowBuilder.csvFlow).
    runWith(Sink.foreach(println)).onComplete( _ => system.shutdown())
}
