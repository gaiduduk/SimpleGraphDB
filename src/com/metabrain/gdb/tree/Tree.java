package com.metabrain.gdb.tree;

import com.metabrain.gdb.Bytes;
import com.metabrain.gdb.InfinityArray;
import com.metabrain.gdb.InfinityConstArray;

import java.util.Arrays;

public class Tree extends InfinityConstArray {
    // TODO delete new and use only 3 objects to search
    // TODO HALF_LONG value change to max tree node count
    private final static long HALF_LONG = 1000000000000000000L;
    private InfinityArray keys;
    private InfinityArray hashes;

    public Tree(String infinityFileID) {
        super(infinityFileID);
        keys = new InfinityArray(infinityFileID + ".keys");
        hashes = new InfinityArray(infinityFileID + ".hashes");
        if (fileData.sumFilesSize == 0)
            add(new TreeNode("****".getBytes(), new long[TreeNode.LINKS_COUNT]));
    }

    public void put(String str, byte[] hash, long value) {
        put(str.getBytes(), hash, value);
    }

    public void put(byte[] hashKey, byte[] hash, long value) {
        TreeNode node = new TreeNode();
        long prevIndex = Long.MAX_VALUE;
        long nodeIndex = 0;
        int i = 0;
        while (i < TreeNode.SIZE) {
            get(nodeIndex, node);
            byte hashChar = hash[i];
            byte nodeChar = node.mask[i];
            while (nodeChar == hashChar && i + 1 < TreeNode.SIZE) {
                i++;
                hashChar = hash[i];
                nodeChar = node.mask[i];
            }
            if (nodeChar == '*') {
                hashChar -= '0';
                long link = node.links[hashChar];
                if (link == 0) {
                    Hash hash1 = new Hash(getFirst8Bytes(hashKey), keys.add(hashKey), value);
                    HashVariants hashVariants = new HashVariants(hash, hash1);
                    node.links[hashChar] = hashes.add(hashVariants) + HALF_LONG;
                    set(nodeIndex, node);
                    return;
                } else if (link < HALF_LONG) {
                    prevIndex = nodeIndex;
                    nodeIndex = link;
                } else /* hashIndex >= HALF_LONG */ {
                    long hashVariantIndex = link - HALF_LONG;
                    HashVariants hashVariants = new HashVariants();
                    hashes.get(hashVariantIndex, hashVariants);
                    if (Arrays.equals(hash, hashVariants.mask)) {
                        long first8Bytes = getFirst8Bytes(hashKey);
                        boolean findKey = false;
                        for (Hash hashl : hashVariants.hashes)
                            if (hashl.first8Bytes == first8Bytes)
                                if (Arrays.equals(hashKey, keys.getString(hashl.keyIndex).getBytes())) {
                                    hashl.value = value;
                                    findKey = true;
                                    break;
                                }
                        if (!findKey) {
                            long keyIndex = keys.add(hashKey);
                            Hash newHash = new Hash(first8Bytes, keyIndex, value);
                            hashVariants.hashes.add(newHash);
                        }
                        hashes.set(hashVariantIndex, hashVariants);
                    } else {
                        byte[] newMask = "****".getBytes();
                        int j = 0;
                        for (; hash[j] == hashVariants.mask[j]; j++)
                            newMask[j] = hash[j];
                        long[] links = new long[TreeNode.LINKS_COUNT];
                        int previousIndex = hashVariants.mask[j] - '0';
                        int newIndex = hash[j] - '0';
                        links[previousIndex] = link;
                        links[newIndex] = hashes.add(
                                new HashVariants(hash, new Hash(getFirst8Bytes(hashKey), keys.add(hashKey), value))
                        ) + HALF_LONG;
                        node.links[hashChar] = add(new TreeNode(newMask, links));
                        set(nodeIndex, node);
                    }
                    return;
                }
            } else {
                nodeChar -= '0';
                hashChar -= '0';
                byte[] newMask = "****".getBytes();
                System.arraycopy(node.mask, 0, newMask, 0, i);
                long[] links = new long[TreeNode.LINKS_COUNT];
                links[nodeChar] = nodeIndex;
                links[hashChar] = hashes.add(new HashVariants(hash, new Hash(getFirst8Bytes(hashKey), keys.add(hashKey), value))) + HALF_LONG;
                long newIndex = add(new TreeNode(newMask, links));
                if (prevIndex != Long.MAX_VALUE) {
                    get(prevIndex, node);
                    int previousPositionInHash = hash[i - 1] - '0';
                    node.links[previousPositionInHash] = newIndex;
                    set(prevIndex, node);
                }
                return;
            }
        }
    }

    public long get(String str, byte[] hash) {
        return get(str.getBytes(), hash);
    }

    public long get(byte[] hashKey, byte[] hash) {
        TreeNode node = new TreeNode();
        long nodeIndex = 0;
        int i = 0;
        while (i < TreeNode.SIZE) {
            node = (TreeNode) get(nodeIndex, node);
            byte hashChar = hash[i];
            byte nodeChar = node.mask[i];
            while (nodeChar == hashChar && i + 1 < TreeNode.SIZE) {
                i++;
                hashChar = hash[i];
                nodeChar = node.mask[i];
            }
            if (nodeChar == '*') {
                if (hashChar >= 0 && hashChar <= '9')
                    hashChar -= '0';
                if (hashChar >= 'a' && hashChar <= 'z')
                    hashChar -= 'a';

                long link = node.links[hashChar];
                if (link == 0) {
                    return Long.MAX_VALUE;
                } else if (link < HALF_LONG) {
                    nodeIndex = link;
                } else /* hashIndex >= HALF_LONG */ {
                    long hashVariantIndex = link - HALF_LONG;
                    HashVariants hashVariants = new HashVariants();
                    hashes.get(hashVariantIndex, hashVariants);
                    if (Arrays.equals(hash, hashVariants.mask)) {
                        long first8Bytes = getFirst8Bytes(hashKey);
                        for (Hash hashl : hashVariants.hashes)
                            if (hashl.first8Bytes == first8Bytes)
                                if (Arrays.equals(hashKey, keys.getString(hashl.keyIndex).getBytes()))
                                    return hashl.value;
                    }
                    return Long.MAX_VALUE;
                }
            } else {
                return Long.MAX_VALUE;
            }
        }
        return Long.MAX_VALUE;
    }

    private long getFirst8Bytes(byte[] hashKey) {
        byte[] bytes = Arrays.copyOfRange(hashKey, 0, Math.min(7, hashKey.length - 1));
        byte[] firstly8Bytes = new byte[Long.BYTES];
        System.arraycopy(bytes, 0, firstly8Bytes, 0, bytes.length);
        return Bytes.toLong(firstly8Bytes);
    }

}
