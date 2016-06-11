package br.com.ezhome.device.program;

import java.util.ArrayList;

/**
 *
 * @author cristofer
 */
public class ByteArrayBuilder {

   private ArrayList<Byte> bytes;
   private int index, bitIndex;

   private byte currentByte;

   public ByteArrayBuilder() {
      bytes = new ArrayList<>();
      index = 0;
      bitIndex = 0;
      currentByte = 0x00;
   }

   public void appendBit(boolean value) {
      if (value) {
//         System.out.println("- "+((byte)(((byte)1) << (byte)(7 - bitIndex)) & 0xff));
         currentByte = (byte) (currentByte | 1 << (7 - bitIndex));
         //currentByte = (byte)b;
//         System.out.println(b & 0x00ff);
      }
      if (bytes.size() > index) {
         bytes.remove(index);
      }
      bytes.add(index, currentByte);
      bitIndex++;
      if (bitIndex > 7) {
         index++;
         currentByte = 0x00;
         bitIndex = 0;
      }
   }

   public void append(byte value, int size, boolean fromLeft, boolean leftToRight) {
      for (int i = 0; i < size; i++) {
         byte mask = 0;
         if (leftToRight) {
            if (fromLeft) {
               mask = (byte) (1 << 7);
            } else {
               mask = (byte) (1 << (size - 1));
            }
         } else {
            mask = 1;
         }
         boolean bitValue = (value & mask) != 0;
         appendBit(bitValue);
         if (leftToRight) {
            value = (byte) (value << 1);
         } else {
            value = (byte) (value >> 1);
         }
      }
   }
   
   public void append(int value, int size, boolean fromLeft, boolean leftToRight) {
      for (int i = 0; i < size; i++) {
         byte mask = 0;
         if (leftToRight) {
            if (fromLeft) {
               mask = (byte) (1 << 31);
            } else {
               mask = (byte) (1 << (size - 1));
            }
         } else {
            mask = 1;
         }
         boolean bitValue = (value & mask) != 0;
         appendBit(bitValue);
         if (leftToRight) {
            value = (byte) (value << 1);
         } else {
            value = (byte) (value >> 1);
         }
      }
   }

   public void appendArray(byte[] bytes, int size, boolean fromLeft, boolean leftToRight) {
      int usedBytes = size / 8;
      if (size % 8 != 0) {
         usedBytes++;
      }
      if (usedBytes > bytes.length) {
         throw new IllegalArgumentException("Size exceds array size.");
      }
      for (int i = 0; i < bytes.length; i++) {
         int index, internalSize;
         if (fromLeft) {
            index = bytes.length - i - 1;
            if (index <= size / 8) {
               internalSize = 8;
            } else {
               internalSize = size % 8;
            }
         } else {
            index = bytes.length - usedBytes - i;
            if (index == bytes.length - usedBytes) {
               internalSize = size % 8;
            } else {
               internalSize = 8;
            }
         }
         append(bytes[index], internalSize, fromLeft, leftToRight);
         size -= 8;
         if (size <= 0) {
            break;
         }
      }
   }

   public void clear() {
      bytes.clear();
      bitIndex = 0;
      index = 0;
      currentByte = 0x00;
   }

   public byte getByte(int index) {
      return bytes.get(index);
   }
   
   public byte[] getBytes() {
      byte[] result = new byte[bytes.size()];
      for (int i = 0; i < bytes.size(); i++) {
         result[i] = bytes.get(i);
      }
      return result;
   }

   public int size() {
      return bytes.size();
   }

   public static String byteToBinaryString(byte value) {
      return String.format("%8s", Integer.toBinaryString(value & 0xFF)).replace(' ', '0');
   }
}
