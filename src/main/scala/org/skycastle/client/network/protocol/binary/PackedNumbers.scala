package org.skycastle.client.network.protocol.binary

import java.math.BigInteger
import org.apache.mina.core.buffer.IoBuffer

/**
 * Utility methods for dealing with packed numbers.
 */
// TODO: Implement the packing algorithm directly, instead of instantiating a BigInteger.
object PackedNumbers {

  private val MAX_NUMBER_OF_NUMBER_BYTES = 10

  /**
   * Encodes values between around -110 to 127 in one byte, and larger values in as many bytes as necessary + 1
   */
  def encode(buffer: IoBuffer, value: Long) {

    if (value > Byte.MinValue + MAX_NUMBER_OF_NUMBER_BYTES && value <= Byte.MaxValue) {
      // The number fits in one byte, above the number-of-bytes indicator area
      buffer.put(value.toByte)
    }
    else {
      val bytes = BigInteger.valueOf(value).toByteArray

      // Sanity checking
      val numBytes = bytes.length
      if (numBytes > MAX_NUMBER_OF_NUMBER_BYTES) throw new IllegalStateException("Problem when encoding packed number " + value + ", way too big BigInteger representation.")
      else if (numBytes <= 0) throw new IllegalStateException("Problem when encoding packed number " + value + ", empty representation.")

      // Encode number of bytes used near the negative lower range of a byte
      val indicatorByte: Byte = (Byte.MinValue + numBytes).toByte
      buffer.put(indicatorByte)
      buffer.put(bytes)
    }
  }

  /**
   * Extracts an encoded packed number.
   */
  def decode(buffer: IoBuffer): Long = {
    val indicatorByte: Byte = buffer.get

    if (indicatorByte > Byte.MinValue + MAX_NUMBER_OF_NUMBER_BYTES) {
      // The number is small, was stored in the first byte
      indicatorByte.toLong
    }
    else {
      // Extract number of bytes in representation
      val numBytes = (indicatorByte.toInt) - Byte.MinValue

      // Sanity checking
      if (numBytes > MAX_NUMBER_OF_NUMBER_BYTES) throw new IllegalStateException("Problem when decoding packed number, too many bytes in representation (" + numBytes + ").")
      else if (numBytes <= 0) throw new IllegalStateException("Problem when decoding packed number, no bytes in representation.")

      // Read representation
      val bytes = new Array[Byte](numBytes)
      buffer.get(bytes)

      // Initialize to big integer, and get Long value
      new BigInteger(bytes).longValue
    }
  }


}


