package com.knox.ref.wkgcass;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * 0                                     CAP
 * first
 * ....->[           free space           ]
 * [sPos,ePos-----------------------------]
 * pace       ]->           ->[      free s
 * [---------sPos---------ePos------------]
 * then
 * [  free space  ]->                      ->
 * [------------ePos--------------------sPos
 * then
 * .........->[   free space    ]->
 * [------ePos----------------sPos--------]
 * maybe we have
 * ..........................->(cannot write into buf any more)
 * [----------------------ePos,sPos--------]
 */
public class RingBuffer {
    private final ByteBuffer buffer;
    private       int        ePos; // end pos
    private       int        sPos; // start pos
    private       int        cap;
    private       boolean    ePosIsAfterSPos = true; // true then end is limit, otherwise start is limit

    private RingBuffer(ByteBuffer buffer) {
        this.buffer = buffer;
        this.cap = buffer.capacity();
    }

    public static RingBuffer allocateDirect(int cap) {
        return new RingBuffer(ByteBuffer.allocateDirect(cap));
    }

    private int storeLimit() {
        if (ePosIsAfterSPos) {
            return cap - ePos; // we can store until capacity
        } else {
            return sPos - ePos; // we can store until the `start` pos
        }
    }

    private int retrieveLimit(int sPos, boolean ePosIsLimit) {
        if (ePosIsLimit) {
            return ePos - sPos; // we can retrieve until end
        } else {
            return cap - sPos; // we can retrieve until capacity
        }
    }

    private int retrieveLimit() {
        return retrieveLimit(this.sPos, this.ePosIsAfterSPos);
    }

    public int storeBytesFrom(ReadableByteChannel channel) throws IOException {
        int lim = storeLimit();
        if (lim == 0)
            return 0; // buffer is full
        buffer.limit(ePos + lim).position(ePos);
        int read = channel.read(buffer);
        ePos += read;
        if (ePos == cap) {
            ePos = 0;
            ePosIsAfterSPos = false;
        }
        if (read == lim) {
            // maybe have more bytes to read
            lim = storeLimit();
            if (lim == 0)
                return read; // buffer is full now
            buffer.limit(ePos + lim).position(ePos);
            int read2 = channel.read(buffer);
            ePos += read2;
            // this time, ePos will not reach cap
            return read + read2;
        } else {
            return read;
        }
    }

    public int writeTo(WritableByteChannel channel) throws IOException {
        return writeTo(channel, Integer.MAX_VALUE);
    }

    public int writeTo(WritableByteChannel channel, int maxBytesToWrite) throws IOException {
        int lim = retrieveLimit();
        if (lim == 0)
            return 0; // buffer is empty
        int realWrite = Math.min(lim, maxBytesToWrite);
        buffer.limit(sPos + realWrite).position(sPos);
        int write = channel.write(buffer);
        sPos += write;
        if (sPos == cap) {
            sPos = 0;
            ePosIsAfterSPos = true;
        }
        if (write == lim && write < maxBytesToWrite) {
            // maybe have more bytes to write
            lim = retrieveLimit();
            if (lim == 0)
                return write; // buffer is empty now
            realWrite = Math.min(lim, maxBytesToWrite - write/* the bytes left to write */);
            buffer.limit(sPos + realWrite).position(sPos);
            int write2 = channel.write(buffer);
            sPos += write2;
            // this time, ePos will not reach cap
            return write + write2;
        } else {
            return write;
        }
    }

    public int free() {
        return cap - used();
    }

    public int used() {
        if (ePosIsAfterSPos) {
            return ePos - sPos;
        } else {
            return ePos + cap - sPos;
        }
    }

    public int capacity() {
        return cap;
    }

    public byte[] getBytes() {
        int len = used();
        byte[] arr = new byte[len];
        if (len == 0)
            return arr;
        int lim = retrieveLimit();
        buffer.limit(sPos + lim).position(sPos);
        buffer.get(arr, 0, lim);
        if (ePosIsAfterSPos)
            return arr; // already reached limit
        int lim2 = retrieveLimit(0, true);
        if (lim2 == 0)
            return arr;
        //noinspection PointlessArithmeticExpression
        buffer.limit(0 + lim2).position(0);
        buffer.get(arr, lim, lim2);
        return arr;
    }
}