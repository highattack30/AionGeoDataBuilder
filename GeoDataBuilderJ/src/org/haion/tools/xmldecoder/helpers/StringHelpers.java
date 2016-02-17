package org.haion.tools.xmldecoder.helpers;

public final class StringHelpers {
    public static int FNV32(String input)
    {
      String str = input.toLowerCase();
      long num = 2166136261L;
      for (int index = 0; index < str.length(); ++index)
        num = num * 16777619L ^ (long) str.charAt(index);
      return (int)num;
    }

//    public static long FNV64(String input)
//    {
//      String str = input.ToLowerInvariant();
//      long num = 14695981039346656037UL;
//     ) for (int index = 0; index < str.Length; ++index)
//        num = num * 1099511628211UL ^ (long) str[index];
//      return num;
//    }

    public static int ParseHex32(String input)
    {
      if (input.startsWith("0x"))
        return Integer.parseInt(input.substring(2)); //, NumberStyles.AllowHexSpecifier);
      else
        return Integer.parseInt(input);
    }

    public static long ParseHex64(String input)
    {
      if (input.startsWith("0x"))
        return Long.parseLong(input.substring(2)); //, NumberStyles.AllowHexSpecifier);
      else
        return Long.parseLong(input);
    }
}
