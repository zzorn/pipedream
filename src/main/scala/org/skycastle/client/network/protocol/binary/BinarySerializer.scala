package org.skycastle.client.network.protocol.binary

import org.apache.mina.core.buffer.IoBuffer
import java.nio.charset.Charset
import org.skycastle.utils.Logging
import org.skycastle.client.messaging.Message
import org.skycastle.client.network.protocol.Message
import org.skycastle.client.network.EntityId


/**
 * Takes care of serializing and de-serializing a set of allowed types from byte buffers.
 */
// TODO: Maybe refactor to separate the functions of encoding and decoding objects and defining the serializers.
class BinarySerializer extends Logging {

  private val NULL_ID: Byte = 0

  private val idToSerializer: Map[Byte, TypeSerializer[_]] = createSerializers()

  def getSerializer(value: Any): Option[TypeSerializer[_]] = idToSerializer.values.find(_.canSerialize(value))

  def encode(buffer: IoBuffer, value: Any) {
    if (value == null) buffer.put(NULL_ID)
    else getSerializer(value) match {
      case Some(serializer) => {
        buffer.put(serializer.id)
        serializer.enc(buffer, value)
      }
      case None => {
        log.warn("When encoding message: No encoder found for object '" + value + "', substituting with null.")
        buffer.put(NULL_ID)
      }
    }
  }

  def decode[T](buffer: IoBuffer): T = {
    val valueType = buffer.get
    if (valueType == NULL_ID) null.asInstanceOf[T]
    else idToSerializer.get(valueType).asInstanceOf[Option[TypeSerializer[T]]] match {
      case Some(serializer) => serializer.dec(buffer)
      case None => {
        log.info("When decoding message: Unknown object type '" + valueType + "', substituting with null.")
        null.asInstanceOf[T]
      }
    }
  }

  private def createSerializers() = {

    var types: Map[Byte, TypeSerializer[_]] = Map()

    var nextId: Byte = 1
    def add(t: TypeSerializer[_]) {
      t.id = nextId
      nextId = (nextId + 1).byteValue

      if (nextId > 120) throw new IllegalStateException("The number of supported types is growing too large for one byte.  Refactor protocol code.")

      val entry = (t.id, t)
      types = types + entry
    }

    val anySerializer = this

    // NOTE: The order of these additions is important, as that is the order in which type ID:s are defined.
    // Do not change it!

    // Wrapped primitive types:

    add(new TypeSerializer(classOf[java.lang.Boolean]) {
      def enc(buffer: IoBuffer, value: T) {
        buffer.put((if (value.booleanValue) 1 else 0).byteValue)
      }

      def dec(buffer: IoBuffer) = java.lang.Boolean.valueOf(buffer.get != 0)
    })

    add(new TypeSerializer(classOf[java.lang.Byte]) {
      def enc(buffer: IoBuffer, value: T) {
        buffer.put(value.byteValue)
      }

      def dec(buffer: IoBuffer) = java.lang.Byte.valueOf(buffer.get)
    })

    add(new TypeSerializer(classOf[java.lang.Short]) {
      def enc(buffer: IoBuffer, value: T) {
        PackedNumbers.encode(buffer, value.longValue)
      }

      def dec(buffer: IoBuffer) = java.lang.Short.valueOf(PackedNumbers.decode(buffer).shortValue)
    })


    add(new TypeSerializer(classOf[java.lang.Integer]) {
      def enc(buffer: IoBuffer, value: T) {
        PackedNumbers.encode(buffer, value.longValue)
      }

      def dec(buffer: IoBuffer) = java.lang.Integer.valueOf(PackedNumbers.decode(buffer).intValue)
    })

    add(new TypeSerializer(classOf[java.lang.Long]) {
      def enc(buffer: IoBuffer, value: T) {
        PackedNumbers.encode(buffer, value.longValue)
      }

      def dec(buffer: IoBuffer) = java.lang.Long.valueOf(PackedNumbers.decode(buffer))
    })

    add(new TypeSerializer(classOf[java.lang.Float]) {
      def enc(buffer: IoBuffer, value: T) {
        buffer.putFloat(value.floatValue)
      }

      def dec(buffer: IoBuffer) = java.lang.Float.valueOf(buffer.getFloat)
    })

    add(new TypeSerializer(classOf[java.lang.Double]) {
      def enc(buffer: IoBuffer, value: T) {
        buffer.putDouble(value.doubleValue)
      }

      def dec(buffer: IoBuffer) = java.lang.Double.valueOf(buffer.getDouble)
    })

    add(new TypeSerializer(classOf[java.lang.Character]) {
      def enc(buffer: IoBuffer, value: T) {
        buffer.putChar(value.charValue)
      }

      def dec(buffer: IoBuffer) = java.lang.Character.valueOf(buffer.getChar)
    })


    // String:
    add(new TypeSerializer[String](classOf[String]) {
      private val charset: Charset = Charset.forName("UTF-8")
      private val charsetDecoder = charset.newDecoder
      private val charsetEncoder = charset.newEncoder

      def enc(buffer: IoBuffer, value: T) {
        buffer.putPrefixedString(value, charsetEncoder)
      }

      def dec(buffer: IoBuffer): T = buffer.getPrefixedString(charsetDecoder)
    })

    // ID types:

    add(new TypeSerializer(classOf[Symbol]) {
      def enc(buffer: IoBuffer, value: T) {
        anySerializer.encode(buffer, value.name)
      }

      def dec(buffer: IoBuffer) = Symbol(anySerializer.decode(buffer))
    })

    add(new TypeSerializer(classOf[EntityId]) {
      private def getBytes(buffer: IoBuffer, num: Int): Array[Byte] = {
        val bytes = new Array[Byte](num)
        buffer.get(bytes)
        bytes
      }

      def enc(buffer: IoBuffer, value: T) {
        PackedNumbers.encode(buffer, value.id)
      }

      def dec(buffer: IoBuffer) = new EntityId(PackedNumbers.decode(buffer))
    })

    // Collections:

    add(new TypeSerializer[List[Any]](classOf[List[Any]]) {
      def enc(buffer: IoBuffer, value: T) {
        buffer.putInt(value.size)
        value foreach {
          x => anySerializer.encode(buffer, x)
        }
      }

      def dec(buffer: IoBuffer) = {
        var numEntries = buffer.getInt
        var resultList: T = Nil
        while (numEntries > 0) {
          val value = anySerializer.decode[Any](buffer)
          resultList = value :: resultList
          numEntries -= 1
        }

        resultList.reverse
      }
    })

    add(new TypeSerializer[Set[Any]](classOf[Set[Any]]) {
      def enc(buffer: IoBuffer, value: T) {
        buffer.putInt(value.size)
        value foreach {
          x => anySerializer.encode(buffer, x)
        }
      }

      def dec(buffer: IoBuffer) = {
        var numEntries = buffer.getInt
        var resultSet: T = Set()
        while (numEntries > 0) {
          val value = anySerializer.decode[Any](buffer)
          resultSet = resultSet + value
          numEntries -= 1
        }

        resultSet
      }
    })

    add(new TypeSerializer[Map[Any, Any]](classOf[Map[Any, Any]]) {
      def enc(buffer: IoBuffer, value: T) {
        buffer.putInt(value.size)
        value foreach {
          case (key, entryValue) =>
            anySerializer.encode(buffer, key)
            anySerializer.encode(buffer, entryValue)
        }
      }

      def dec(buffer: IoBuffer) = {
        var numEntries = buffer.getInt
        var resultMap: T = Map()
        while (numEntries > 0) {
          val key = anySerializer.decode[Any](buffer)
          val value = anySerializer.decode[Any](buffer)
          val entry = (key, value)
          resultMap = resultMap + entry
          numEntries -= 1
        }

        resultMap
      }
    })


    // The message type

    add(new TypeSerializer(classOf[Message]) {
      def enc(buffer: IoBuffer, value: T) {
        anySerializer.encode(buffer, value.action)
        anySerializer.encode(buffer, value.parameters)
      }

      def dec(buffer: IoBuffer) = {
        val action = anySerializer.decode(buffer).asInstanceOf[Symbol]
        val parameters = anySerializer.decode(buffer).asInstanceOf[Map[Symbol, Any]]
        Message(action, parameters)
      }
    })


    // TODO: Add serialization for byte arrays


    /*
        // Arbitrary types that implement own serialization and deserialization
        add( new TypeSerializer( classOf[Transferable] ) {
          def enc(buffer: IoBuffer, value: T) = {
            symbolSerializer.enc( buffer, Symbol( value.getClass.getName ) )
            anySerializer.encode( buffer, value.toTransferObject )
          }

          def dec(buffer: IoBuffer): T =  {
            val typeName = symbolSerializer.dec( buffer ).name

            try {
              // TODO: Specify list of safe classes or such?
              val typeClass : Class[_]= Class.forName( typeName )
              if ( classOf[T].isAssignableFrom( typeClass ) ) {
                try {
                  val constructor = typeClass.getConstructor( classOf[Object] )
                  val valueObject : Object = anySerializer.decode( buffer )
                  constructor.newInstance( valueObject ).asInstanceOf[T]
                }
                catch {
                  case e  =>
                    ProtocolLogger.logInfo( "When decoding message: Could not instantiate transferable type '"+typeName+"', substituting with null.  Error: " + e.getMessage, e )
                    null.asInstanceOf[T]
                }
              }
              else {
                ProtocolLogger.logWarning( "When decoding message: Can not decode to transferable type '"+typeName+"', it doesn't implement Transferable.  Substituting with null." )
                null.asInstanceOf[T]
              }
            }
            catch {
              case e : ClassNotFoundException =>
                ProtocolLogger.logInfo( "When decoding message: Unknown transferable type '"+typeName+"', substituting with null.", e )
                null.asInstanceOf[T]
            }
          }
        })
    */


    // Primitive types:
    // NOTE: All primitive types seem to get wrapped up in boxed types at the moment, so we can leave the primitive encoders away.
    /*
        add( new TypeSerializer[Boolean]( classOf[Boolean] ) {
          def len(value: T) = 1
          def enc(buffer: ByteBuffer, value: T) = buffer.put( (if(value) 1 else 0).byteValue )
          def dec(buffer: ByteBuffer) = buffer.get != 0
        })

        add( new TypeSerializer( classOf[Byte] ) {
          def len(value: T) = 1
          def enc(buffer: ByteBuffer, value: T) = buffer.put( value )
          def dec(buffer: ByteBuffer) = buffer.get
        })

        add( new TypeSerializer( classOf[Short] ) {
          def len(value: T) = 2
          def enc(buffer: ByteBuffer, value: T) = buffer.putShort( value )
          def dec(buffer: ByteBuffer) = buffer.getShort
        })

        add( new TypeSerializer( classOf[Int] ) {
          def len(value: T) = 4
          def enc(buffer: ByteBuffer, value: T) = buffer.putInt( value )
          def dec(buffer: ByteBuffer) = buffer.getInt
        })

        add( new TypeSerializer( classOf[Long] ) {
          def len(value: T) = 8
          def enc(buffer: ByteBuffer, value: T) = buffer.putLong( value )
          def dec(buffer: ByteBuffer) = buffer.getLong
        })

        add( new TypeSerializer( classOf[Float] ) {
          def len(value: T) = 4
          def enc(buffer: ByteBuffer, value: T) = buffer.putFloat( value )
          def dec(buffer: ByteBuffer) = buffer.getFloat
        })

        add( new TypeSerializer( classOf[Double] ) {
          def len(value: T) = 8
          def enc(buffer: ByteBuffer, value: T) = buffer.putDouble( value )
          def dec(buffer: ByteBuffer) = buffer.getDouble
        })

        add( new TypeSerializer( classOf[Char] ) {
          def len(value: T) = 2
          def enc(buffer: ByteBuffer, value: T) = buffer.putChar( value )
          def dec(buffer: ByteBuffer) = buffer.getChar
        })
    */


    types
  }


}
