package org.haion.tools.xmldecoder.helpers;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.haion.tools.databuilders.utils.BitConverter;

public final class ByteHelpers {
    public static void Reset(byte[] data, byte value)
    {
      for (int index = 0; index < data.length; ++index)
        data[index] = value;
    }

//    public static T ToStructure<T>(byte[] data, int index)
//    {
//      int length = Marshal.SizeOf(typeof (T));
//      if (index + length > data.length)
//        throw new Exception("not enough data to fit the structure");
//      byte[] numArray = new byte[length];
//      Array.Copy((Array) data, index, (Array) numArray, 0, length);
//      GCHandle gcHandle = GCHandle.Alloc((object) numArray, GCHandleType.Pinned);
//      T obj = (T) Marshal.PtrToStructure(gcHandle.AddrOfPinnedObject(), typeof (T));
//      gcHandle.Free();
//      return obj;
//    }
//
//    public static T ToStructure<T>(byte[] data)
//    {
//      return ByteHelpers.ToStructure<T>(data, 0);
//    }

//    public static String ReadASCIIZ(byte[] data, int offset)
//    {
//      int index = offset;
//      while (index < data.length && (int) data[index] != 0)
//        ++index;
//      if (index == offset)
//        return "";
//      else
//        return Encoding.ASCII.GetString(data, offset, index - offset);
//    }
//
//    public static String ReadASCIIZ(byte[] data, uint offset)
//    {
//      return ByteHelpers.ReadASCIIZ(data, (int) offset);
//    }

//    public static String ReadUTF8Z(byte[] data, int offset)
//    {
//      int index = offset;
//      while (index < data.length && (int) data[index] != 0)
//        ++index;
//      if (index == offset)
//        return "";
//      else
//        return Encoding.UTF8.GetString(data, offset, index - offset);
//    }
//
//    public static String ReadUTF8Z(byte[] data, uint offset)
//    {
//      return ByteHelpers.ReadUTF8Z(data, (int) offset);
//    }

    public static String ReadUTF16Z(byte[] data, int offset) throws Exception
    {
      int startIndex = offset;
      while (startIndex < data.length && (int) BitConverter.toInt16(data, startIndex) != 0)
        startIndex += 2;
      if (startIndex == offset)
        return "";
      else {
    	  Charset charset = Charset.forName("UTF-16LE");
//    	  CharsetDecoder decoder = charset.newDecoder();
//    	  CharBuffer cbuf = decoder.decode(ByteBuffer.wrap(data, offset, startIndex - offset));
//    	  return cbuf.toString();

    	  return new String(data, offset, startIndex - offset, charset);
        //return Encoding.Unicode.GetString(data, offset, startIndex - offset);
      }
    }

//    public static String ReadUTF16Z(byte[] data, uint offset)
//    {
//      return ByteHelpers.ReadUTF16Z(data, (int) offset);
//    }
}
