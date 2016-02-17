package org.haion.tools.xmldecoder.fileformats;

import java.io.IOException;
import java.io.InputStream;

import org.haion.tools.xmldecoder.helpers.ByteHelpers;
import org.haion.tools.xmldecoder.helpers.StreamHelpers;

public final class BinaryXmlFileHelpers {
    public static int ReadPackedS32(InputStream stream) throws IOException
    {
      int num1 = StreamHelpers.ReadU8(stream);
      int num2 = 0;
      int num3 = 0;
      for (; (int) num1 >= 128; num1 = StreamHelpers.ReadU8(stream))
      {
        num2 |= ((int) num1 & (int) Byte.MAX_VALUE) << num3;
        num3 += 7;
      }
      return num2 | (int) num1 << num3;
    }

    public static String ReadTable(byte[] data, int offset) throws Exception
    {
      if (offset == 0)
        return "";
      else
        return ByteHelpers.ReadUTF16Z(data, 2 * offset);
    }
}
