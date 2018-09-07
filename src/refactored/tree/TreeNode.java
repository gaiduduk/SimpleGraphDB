package refactored.tree;

import refactored.Bytes;
import refactored.InfinityConstArray;

import java.util.Arrays;

public class TreeNode {
    public final static int SIZE = HashTree.MASK_SIZE + HashTree.LINKS_SIZE;
    byte[] mask;
    long[] links;

    public TreeNode(byte[] data) {
        set(data);
    }

    public void set(byte[] data) {
        this.mask = Arrays.copyOfRange(data, 0, HashTree.MASK_SIZE - 1);
        this.links = Bytes.toLongArray(Arrays.copyOfRange(data, HashTree.MASK_SIZE, TreeNode.SIZE - 1));
    }

    public TreeNode(byte[] mask, long[] links) {
        this.mask = mask;
        this.links = links;
    }

    public byte[] getBytes() {
        byte[] data = new byte[HashTree.MASK_SIZE + links.length * Long.BYTES];
        System.arraycopy(mask, 0, data, 0, HashTree.MASK_SIZE);
        System.arraycopy(Bytes.fromLongArray(links), 0, data, HashTree.MASK_SIZE - 1, HashTree.LINKS_SIZE);
        return data;
    }

    public void setLink(InfinityConstArray file, long nodeIndex, int linkIndex, long linkValue) {
        links[linkIndex] = linkValue;
        long start = nodeIndex * SIZE + HashTree.MASK_SIZE +  linkIndex * Long.BYTES;
        file.write(start, Bytes.fromLong(linkValue));
    }

}