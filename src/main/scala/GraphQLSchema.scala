import sangria.schema.{Field, _}

import scala.concurrent.Future

object HighLevelType {
  val String = "STRING"
  val Int = "INT"
  val Float = "FLOAT"
}
object ContainerType {
  val None = "none"
  val List = "list"
}


object GraphQLSchema {
  val HighLevelTypeEnum = EnumType(
    "HighLevelType",
    Some("One of supported types."),
    List(
      EnumValue(
        name = HighLevelType.String,
        value = HighLevelType.String
      ),
      EnumValue(
        name = HighLevelType.Int,
        value = HighLevelType.Int
      ),
      EnumValue(
        name = HighLevelType.Float,
        value = HighLevelType.Float
      )
    )
  )
  val ContainerTypeEnum = EnumType(
    "ContainerType",
    Some("Container type is either list or none."),
    List(
      EnumValue(
        name = ContainerType.List,
        value = ContainerType.List
      ),
      EnumValue(
        name = ContainerType.None,
        value = ContainerType.None
      )
    )
  )

  val DefinitionKeyArgument = Argument("key", OptionInputType(IDType), "The key of the definition you are looking for.")

  /**
    * Negative: really verbose as you define a definition, but still have to the mappings
    * for any fields on the concrete type definition
    */
  val Definition: InterfaceType[Unit, Definition] = InterfaceType(
    "Definition",
    "Interface that defines common fields of a model definition",
    () => fields[Unit, Definition](
      Field(
        "key", IDType,
        Some("Unique key for the definition"),
        List(DefinitionKeyArgument),
        resolve = _.value.key
      ),
      Field(
        "description", StringType,
        Some("Brief description of the definition"),
        resolve = _.value.key
      ),
      Field(
        "displayName", StringType,
        Some("Display name of the definition"),
        resolve = _.value.key
      )
    )
  )

  val Timeseries: ObjectType[Unit, Timeseries] = ObjectType(
    "Timeseries",
    "Model timeseries",
    List(Definition),
    () => fields[Unit, Timeseries](
      Field(
        "key", IDType,
        Some("Unique key for the timeseries"),
        List(DefinitionKeyArgument),
        resolve = _.value.key
      ),
      Field(
        "highLevelType", HighLevelTypeEnum,
        Some("High level type of the timeseries"),
        resolve = _.value.key
      ),
      Field(
        "description", StringType,
        Some("Brief description of the timeseries"),
        resolve = _.value.key
      ),
      Field(
        "displayName", StringType,
        Some("Display name of the timeseries"),
        resolve = _.value.key
      )
    )
  )

  val ObjectAttributes: ObjectType[Unit, ObjectAttribute] = ObjectType(
    "ObjectAttributes",
    "Model object attributes",
    List(Definition),
    () => fields[Unit, ObjectAttribute](
      Field(
        "key", IDType,
        Some("Unique key for the object attribute"),
        List(DefinitionKeyArgument),
        resolve = _.value.key
      ),
      Field(
        "highLevelType", HighLevelTypeEnum,
        Some("High level type of the object attribute"),
        resolve = _.value.highLevelType
      ),
      Field(
        "containerType", ContainerTypeEnum,
        Some("Container type of the object attribute"),
        resolve = _.value.highLevelType
      ),
      Field(
        "description", StringType,
        Some("Brief description of the object attribute"),
        resolve = _.value.description
      ),
      Field(
        "displayName", StringType,
        Some("Display name of the object attribute"),
        resolve = _.value.displayName
      )
    )
  )


  val DefinitionKeysArgument = Argument("keys", OptionInputType(ListInputType(IDType)), "The keys of the definitions you are looking for.")

  val Model: ObjectType[Unit, Model] = ObjectType(
    "Model",
    "The complete object model",
    () => fields[Unit, Model](
      Field(
        "namespace", StringType,
        Some("Namespace this model belongs to."),
        resolve = _.value.namespace
      ),
      Field(
        "timeseries", ListType(Timeseries),
        Some("Timeseries of this model."),
        List(DefinitionKeysArgument),
        resolve = ctx => ctx.argOpt[Seq[String]](DefinitionKeysArgument.name) match {
          case Some(keys) => ctx.value.timeseries.filter(ts => keys.contains(ts.key))
          case None => ctx.value.timeseries
        }
      ),
      Field(
        "objectAttributes", ListType(ObjectAttributes),
        Some("Object attributes of this model."),
        List(DefinitionKeysArgument),
        resolve = ctx => ctx.argOpt[Seq[String]](DefinitionKeysArgument.name) match {
          case Some(keys) => ctx.value.objectAttributes.filter(oa => keys.contains(oa.key))
          case None => ctx.value.objectAttributes
        }
      )
    )
  )

  val ModelNamespaceArg = Argument("namespace", StringType, "The name of the namespace you want.")
  val ModelQuery: ObjectType[ModelRepo, Unit] = ObjectType(
    "ModelQuery",
    fields[ModelRepo, Unit](
      Field(
        "model", OptionType(Model),
        Some("Namespace this model belongs to."),
        List(ModelNamespaceArg),
        resolve = (ctx) => ctx.ctx.fetchModel(ctx.arg(ModelNamespaceArg)))
      )
    )
  val schema = Schema(ModelQuery)
}
