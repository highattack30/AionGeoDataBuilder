package org.haion.tools.xmldecoder.fileformats;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.haion.tools.xmldecoder.helpers.StreamHelpers;

public class BinaryXmlNode {
    public String Name;
    public String Value;
    public Map<String, String> Attributes;
    public List<BinaryXmlNode> Children;

    public void Read(InputStream input, BinaryXmlStringTable table) throws Exception
    {
      this.Name = table.getData(BinaryXmlFileHelpers.ReadPackedS32(input));
      this.Attributes = new HashMap<String, String>();
      this.Children = new ArrayList<BinaryXmlNode>();
      this.Value = null;
      int num1 = StreamHelpers.ReadU8(input);
      if (((int) num1 & 1) == 1)
        this.Value = table.getData(BinaryXmlFileHelpers.ReadPackedS32(input));
      if (((int) num1 & 2) == 2)
      {
        int num2 = BinaryXmlFileHelpers.ReadPackedS32(input);
        for (int index = 0; index < num2; ++index)
          this.Attributes.put(table.getData(BinaryXmlFileHelpers.ReadPackedS32(input)), 
        		  table.getData(BinaryXmlFileHelpers.ReadPackedS32(input)));
      }
      if (((int) num1 & 4) != 4)
        return;
      int num3 = BinaryXmlFileHelpers.ReadPackedS32(input);
      for (int index = 0; index < num3; ++index)
      {
        BinaryXmlNode binaryXmlNode = new BinaryXmlNode();
        binaryXmlNode.Read(input, table);
        this.Children.add(binaryXmlNode);
      }
    }
}
