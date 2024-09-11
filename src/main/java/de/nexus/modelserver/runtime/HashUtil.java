package de.nexus.modelserver.runtime;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.util.Collection;
import java.util.List;

public final class HashUtil {

    public static long objectToHash(Object object) {
        final HashFunction hf = Hashing.murmur3_128("HierStehtEinTollerSeed17".hashCode());
        return hf.hashBytes(intToByteArray(object.hashCode())).asLong();
    }

    public static long collectionToHash(Collection<Object> objects) {
        final HashFunction hf = Hashing.murmur3_128("HierStehtEinTollerSeed17".hashCode());
        return hf.hashBytes(collectionHashToByteArray(objects)).asLong();
    }

    public static byte[] collectionHashToByteArray(final Collection<Object> objects) {
        byte[] bytes = new byte[objects.size() * 8];
        int idx = 0;
        for (Object object : objects) {
            byte[] objectAsBytes = intToByteArray(object.hashCode());
            bytes[idx] = objectAsBytes[0];
            bytes[idx + 1] = objectAsBytes[1];
            bytes[idx + 2] = objectAsBytes[2];
            bytes[idx + 3] = objectAsBytes[3];
            bytes[idx + 4] = objectAsBytes[4];
            bytes[idx + 5] = objectAsBytes[5];
            bytes[idx + 6] = objectAsBytes[6];
            bytes[idx + 7] = objectAsBytes[7];
            idx += 8;
        }
        return bytes;
    }

    public static byte[] intToByteArray(final long value) {
        byte[] b = new byte[8];
        final long mask = 0x00000000000000ff;
        for (int i = 0; i < 8; i++) {
            b[i] = ((byte) ((value & mask << i * 8) >> i * 8));
        }
        return b;
    }

    public static String arrayToString(byte[] obj) {
        StringBuilder sb = new StringBuilder();
        for (byte b : obj) {
            sb.append(b).append(" ");
        }
        return sb.toString();
    }

    public static String collectionToString(List<Byte> obj) {
        return obj.stream().map(Object::toString).reduce("", (sum, value) -> sum + " " + value);
    }

}

