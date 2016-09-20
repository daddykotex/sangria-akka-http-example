import scala.concurrent.Future

trait ModelRepo {
  def fetchModel(namespace: String): Future[Option[Model]]
}

trait Definition {
  def key: String
  def displayName: String
  def description: String
}

case class Timeseries(key: String, highLevelType: String, displayName: String, description: String) extends Definition

case class ObjectAttribute(key: String, highLevelType: String, containerType: String, displayName: String, description: String) extends Definition

case class Model(namespace: String, timeseries: List[Timeseries], objectAttributes: List[ObjectAttribute])

object ModelRepoImpl extends ModelRepo {
  private val models = Seq(
    Model(
      "buzz",
      List(
        Timeseries("ts1", HighLevelType.String, "dp ts 1", "desc ts1"),
        Timeseries("temp", HighLevelType.String, "dp ts 2", "desc ts2")
      ),
      List(
        ObjectAttribute("oa1", HighLevelType.Float, ContainerType.List, "oa1 dp", "oa1 desc")
      )
    ),
    Model(
      "kolombo",
      List(
        Timeseries("ts3", HighLevelType.String, "dp ts 1", "desc ts1"),
        Timeseries("brand", HighLevelType.String, "dp brand", "Brand yo")
      ),
      List(
        ObjectAttribute("oa1", HighLevelType.Float, ContainerType.List, "oa1 dp", "oa1 desc"),
        ObjectAttribute("oa2", HighLevelType.Float, ContainerType.List, "oa2 dp", "oa2 desc")
      )
    )
  )
  private val modelsByNamespace = models.map(model => model.namespace -> model).toMap

  override def fetchModel(namespace: String): Future[Option[Model]] = Future.successful(modelsByNamespace.get(namespace))
}
