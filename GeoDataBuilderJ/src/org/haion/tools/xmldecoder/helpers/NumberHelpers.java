package org.haion.tools.xmldecoder.helpers;

import org.haion.tools.databuilders.utils.CommonUtils;

public final class NumberHelpers {
    public static short BigEndian(short value)
    {
      if (CommonUtils.isLittleEndianByteOrder)
        return NumberHelpers.Swap(value);
      else
        return value;
    }

//    public static ushort BigEndian(this ushort value)
//    {
//      if (BitConverter.IsLittleEndian)
//        return NumberHelpers.Swap(value);
//      else
//        return value;
//    }

    public static int BigEndian(int value)
    {
      if (CommonUtils.isLittleEndianByteOrder)
        return NumberHelpers.Swap(value);
      else
        return value;
    }

//    public static uint BigEndian(this uint value)
//    {
//      if (BitConverter.IsLittleEndian)
//        return NumberHelpers.Swap(value);
//      else
//        return value;
//    }

    public static long BigEndian(long value)
    {
      if (CommonUtils.isLittleEndianByteOrder)
        return NumberHelpers.Swap(value);
      else
        return value;
    }

//    public static ulong BigEndian(this ulong value)
//    {
//      if (BitConverter.IsLittleEndian)
//        return NumberHelpers.Swap(value);
//      else
//        return value;
//    }

    public static short LittleEndian(short value)
    {
      if (!CommonUtils.isLittleEndianByteOrder)
        return NumberHelpers.Swap(value);
      else
        return value;
    }

//    public static ushort LittleEndian(this ushort value)
//    {
//      if (!BitConverter.IsLittleEndian)
//        return NumberHelpers.Swap(value);
//      else
//        return value;
//    }

    public static int LittleEndian(int value)
    {
      if (!CommonUtils.isLittleEndianByteOrder)
        return NumberHelpers.Swap(value);
      else
        return value;
    }

//    public static uint LittleEndian(this uint value)
//    {
//      if (!BitConverter.IsLittleEndian)
//        return NumberHelpers.Swap(value);
//      else
//        return value;
//    }

    public static long LittleEndian(long value)
    {
      if (!CommonUtils.isLittleEndianByteOrder)
        return NumberHelpers.Swap(value);
      else
        return value;
    }

//    public static ulong LittleEndian(this ulong value)
//    {
//      if (!BitConverter.IsLittleEndian)
//        return NumberHelpers.Swap(value);
//      else
//        return value;
//    }

    public static short Swap(short value)
    {
      return (short) ((int) Byte.MAX_VALUE & (int) value >> 8 | 65280 & (int) value << 8);
    }

//    public static ushort Swap(this ushort value)
//    {
//      return (ushort) ((int) byte.MaxValue & (int) value >> 8 | 65280 & (int) value << 8);
//    }

    public static int Swap(int value)
    {
      int num = value;
      return (int) Byte.MAX_VALUE & (int) (num >> 24) | 65280 & (int) (num >> 8) | 16711680 & (int) num << 8 | -16777216 & (int) num << 24;
    }

    public static int Swap24(int value)
    {
      return (int) Byte.MAX_VALUE & value >> 16 | 65280 & value | 16711680 & value << 16;
    }

//    public static uint Swap(this uint value)
//    {
//      return (uint) ((int) byte.MaxValue & (int) (value >> 24) | 65280 & (int) (value >> 8) | 16711680 & (int) value << 8 | -16777216 & (int) value << 24);
//    }

//    public static uint Swap24(this uint value)
//    {
//      return (uint) ((int) byte.MaxValue & (int) (value >> 16) | 65280 & (int) value | 16711680 & (int) value << 16);
//    }

    public static long Swap(long value)
    {
      long num = value;
      return (long) Byte.MAX_VALUE & (long) (num >> 56) | 65280L & (long) (num >> 40) | 16711680L & (long) (num >> 24) | 4278190080L & (long) (num >> 8) | 1095216660480L & (long) num << 8 | 280375465082880L & (long) num << 24 | 71776119061217280L & (long) num << 40 | -72057594037927936L & (long) num << 56;
    }

//    public static ulong Swap(this ulong value)
//    {
//      return (ulong) ((long) byte.MaxValue & (long) (value >> 56) | 65280L & (long) (value >> 40) | 16711680L & (long) (value >> 24) | 4278190080L & (long) (value >> 8) | 1095216660480L & (long) value << 8 | 280375465082880L & (long) value << 24 | 71776119061217280L & (long) value << 40 | -72057594037927936L & (long) value << 56);
//    }
}
