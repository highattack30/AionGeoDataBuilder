package org.haion.tools.xmldecoder.fileformats;

import java.io.IOException;
import java.io.InputStream;

import org.haion.tools.xmldecoder.helpers.ByteHelpers;

public class BinaryXmlStringTable {
    protected byte[] Data;

    public String getData(int index) throws Exception
    {
        if (index == 0)
          return "";
        else
          return ByteHelpers.ReadUTF16Z(this.Data, index * 2);
    }

    public void Read(InputStream input) throws IOException
    {
      int count = BinaryXmlFileHelpers.ReadPackedS32(input);
      this.Data = new byte[count];
      input.read(this.Data, 0, count);
    }
}
