package cn.rf.hz.web.utils;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

public class LZ4Compress {

    
    public  static byte[] compress(final byte[] src) {
        if (src == null) {
            throw new IllegalArgumentException("src must not be null.");
        }
        LZ4Factory factory = LZ4Factory.fastestInstance();
        LZ4Compressor compressor= factory.highCompressor(9);
 
        return compressor.compress(src);
    }
    
    public byte[] decompressor(final byte[] src, int size) {
        if (src == null) {
            throw new IllegalArgumentException("src must not be null.");
        }
        LZ4FastDecompressor decompressor = LZ4Factory.fastestInstance().fastDecompressor();
    
        return decompressor.decompress(src, 0, size);
    }
}
