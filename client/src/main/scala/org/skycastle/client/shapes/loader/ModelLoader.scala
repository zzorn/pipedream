package org.skycastle.client.shapes.loader

import org.yaml.snakeyaml.Yaml
import java.io.{FileReader, File}
import org.yaml.snakeyaml.introspector.BeanAccess
import org.yaml.snakeyaml.constructor.SafeConstructor
import org.yaml.snakeyaml.composer.Composer
import org.yaml.snakeyaml.representer.Representer
import org.yaml.snakeyaml.nodes.Tag
import org.skycastle.client.shapes.components._

/**
 *
 */
class ModelLoader {

  private val constructor = new FilterConstructor(classOf[Tome])
  private val representer = new Representer()

  allowType[Tome]
  allowType[Cube]
  allowType[Tube]
  allowType[Ball]

  def allowType[T <: AnyRef](implicit m: Manifest[T]) {
    val kind: Class[T] = m.erasure.asInstanceOf[Class[T]]
    constructor.registerType[T]
    representer.addClassTag(kind, new Tag("!" + kind.getSimpleName))
  }
  
  def loadModels(files: List[File]): Map[String, Tome] = {
    var result = Map[String, Tome]()
    files foreach {f => result ++= loadModels(f)}
    result
  }
  
  def loadModels(file: File): Map[String, Tome] = {
    println("Loading models from " + file)

    var result = Map[String, Tome]()

    constructor.source = file.getPath
    val yaml = new Yaml(constructor, representer)
    val reader = new FileReader(file)
    try {
      val tomes = yaml.loadAll(reader).iterator()
      while (tomes.hasNext) {
        val tome = tomes.next().asInstanceOf[Tome]
        result += (tome.name -> tome)
      }
    }
    finally {
      if (reader != null) reader.close()
    }

    result
  }

}
