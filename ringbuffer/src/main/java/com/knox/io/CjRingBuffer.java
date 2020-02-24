package com.knox.io;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Objects;

/**
 * 存放数据的是ByteBuffer
 * 由于Java和C共用, 采用ByteBuffer.allocate
 * <p>
 * 还需要用一个Queue来保存BufferData(pos, size, pts, ...)
 * 如果Queue是thread-safe, 那么应该就可以认为RingBuffer是thread-safe的
 * <p>
 * BufferData可以由Java来创建或者C来创建, 那么释放问题如何解决?
 * <p>
 * read()
 * write()
 * <p>
 * dequeue()
 * queue()
 */

public class CjRingBuffer {
    private final ByteBuffer              buffer;
    private       ArrayList<CjBufferData> cjList          = new ArrayList<>();
    private       int                     cap;
    private       int                     ePos; // end pos
    private       int                     sPos; // start pos
    private       boolean                 ePosIsAfterSPos = true; // true then end is limit, otherwise start is limit

    private CjRingBuffer(ByteBuffer buffer) {
        this.buffer = buffer;
        this.cap = buffer.capacity();
    }

    public static CjRingBuffer allocateDirect(int cap) {
        return new CjRingBuffer(ByteBuffer.allocateDirect(cap));
    }

    public int capacity() {
        return cap;
    }

    public int used() {
        if (ePosIsAfterSPos) {
            return ePos - sPos;
        } else {
            return ePos + cap - sPos;
        }
    }

    public int free() {
        return cap - used();
    }

    public int count() {
        return cjList.size();
    }

    private int storeLimit() {
        if (ePosIsAfterSPos) {
            return cap - ePos; // we can store until capacity
        } else {
            return sPos - ePos; // we can store until the `start` pos
        }
    }

    private int retrieveLimit() {
        return retrieveLimit(this.sPos, this.ePosIsAfterSPos);
    }

    private int retrieveLimit(int sPos, boolean ePosIsLimit) {
        if (ePosIsLimit) {
            return ePos - sPos; // we can retrieve until end
        } else {
            return cap - sPos; // we can retrieve until capacity
        }
    }

    // read ==> data from ByteBuffer to byte[]
    private int jRead(final byte[] pBuffer, final int pOffset, final int pLength) {
        Objects.requireNonNull(pBuffer, "Buffer");

        // 查询有效数据的长度
        int lim = retrieveLimit();
        if (lim == 0)
            return 0; // buffer is empty
        // 如果数据需要读完尾巴, 再从头开始读, 就需要分两部分来读
        // 第一部分应该读fWrite个byte
        int fRead = Math.min(lim, pLength);
        buffer.limit(sPos + fRead).position(sPos);
        buffer.get(pBuffer, pOffset, fRead);
        sPos += fRead;
        if (sPos == cap) {
            sPos = 0;
            ePosIsAfterSPos = true;
        }

        if (fRead == lim && fRead < pLength) {
            // 第二部分数据则读sRead个byte
            lim = retrieveLimit();
            if (lim == 0)
                return fRead; // buffer is empty
            int sRead = Math.min(lim, pLength - fRead/* the bytes left to read */);
            buffer.limit(sPos + sRead).position(sPos);
            buffer.get(pBuffer, pOffset + fRead, sRead);
            sPos += sRead;
            return fRead + sRead;
        } else {
            return fRead;
        }
    }

    // write ==> data from byte[] to ByteBuffer
    private int jWrite(final byte[] pBuffer, final int pOffset, final int pLength) {
        int lim = storeLimit();
        return 0;
    }

}
