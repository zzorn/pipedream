package org.skycastle.parser

import java.util.logging.Logger
import reflect.Manifest

/**
 * Can be used to create beans of registered types.
 */
class BeanFactory {
  
  def typeForName(name: String): Class[_] = {
    println("Getting type for " + name)
    
    name match {
      case "double" => classOf[Double]
      case "string" => classOf[String]
      case _ => null
    }
  }

  def nameForType(kind: Class[_]): String = {
    if (kind.isAssignableFrom(classOf[Double]) ||
        kind.isAssignableFrom(classOf[Float]) ||
        kind.isAssignableFrom(classOf[Byte]) ||
        kind.isAssignableFrom(classOf[Short]) ||
        kind.isAssignableFrom(classOf[Int]) ||
        kind.isAssignableFrom(classOf[Long])) "double"
    else kind.getSimpleName
  }

  /*
  type BeanConstructor = () => _ <: Bean
  type BeanCreator = Symbol => _ <: Option[Bean]

  /** True if properties not already present in the bean should be added to it when creating it from a map */
  var addUnknownProperties = true

  private var beanConstructors: Map[Symbol, () => _ <: Bean] = Map()
  private val initialBeanCreator: BeanCreator = { (name: Symbol) => beanConstructors.get(name).flatMap(x => Some(x())) }
  private var beanCreators: List[Symbol => _ <: Option[Bean]] = List(initialBeanCreator)
  private var defaultBeanConstructor: () => _ <: Bean = {() => new PropertyBean()}

  def registerBeanType(beanType: Class[_ <: Bean]) { registerBeanType(Symbol(beanType.getSimpleName), () => beanType.newInstance) }
  def registerBeanTypes(beanTypes: Seq[Class[_ <: Bean]]) {beanTypes foreach {t => registerBeanType(t)}}
  def registerBeanType(typeName: Symbol, createInstance: () => _ <: Bean) { beanConstructors += (typeName -> createInstance) }
  def registerBeanTypes(creator: Symbol => _ <: Option[Bean]) { beanCreators ::= creator }

  def setDefaultBeanType(createInstance: () => _ <: Bean) {defaultBeanConstructor = createInstance}

  def createDefaultBeanInstance(): Bean = defaultBeanConstructor()

  def createBeanInstance(typeName: Symbol, allowFallbackToDefault: Boolean = true): Bean = {
    var bean: Bean = createBeanWithCreator(typeName)
    if (bean == null) {
      if (allowFallbackToDefault) {
        Logger.getLogger(getClass.getName).fine("No bean creator found for bean type " + typeName + ", using default bean type.")
        createDefaultBeanInstance()
      }
      else throw new IllegalStateException("No bean creator found for bean type '"+typeName+"'.")
    }
    else bean
  }

  def createBeanFromMap(propertyValues: Map[Symbol, AnyRef], serializers: Serializers): Bean = {
    val bean: Bean = if (propertyValues.contains(Bean.typePropertyName)) createBeanInstance(asSymbol(propertyValues(Bean.typePropertyName)))
    else createDefaultBeanInstance()
    propertyValues foreach { e =>
      val field: Symbol = e._1
      var value: AnyRef = e._2
      if (field != Bean.typePropertyName) {
        if (bean.contains(field)) {
          // Do any deserialization if needed:
          val kind = bean.properties(field).kind.erasure

          if (value.isInstanceOf[Map[Symbol, AnyRef]]) {
            // Create contained bean
            value = createBeanFromMap(value.asInstanceOf[Map[Symbol, AnyRef]], serializers)
          }
          else {
            // De-serialize primitive values
            value = serializers.deserialize(kind, value.toString)
          }

          bean.set(field, value)
        }
        else {
          if (addUnknownProperties) {
            val kind = if (value == null) classOf[String] else value.getClass()
            bean.addProperty(field, value)(Manifest.classType(kind))
          }
        }
      }
    }

    bean
  }

  private def asSymbol(value: AnyRef): Symbol = {
    if (value.isInstanceOf[Symbol]) value.asInstanceOf[Symbol]
    else Symbol(value.toString)
  }

  private def createBeanWithCreator(typeName: Symbol): Bean = {
    var bean: Bean = null
    beanCreators exists {
      bc =>
        bc(typeName) match {
          case None =>
            false
          case Some(b) =>
            bean = b
            bean.beanType_=(typeName)
            true
        }
    }
    bean
  }
*/
}