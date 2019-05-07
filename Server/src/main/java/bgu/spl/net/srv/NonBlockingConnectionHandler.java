package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.ConnectionsImpl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NonBlockingConnectionHandler<T> implements ConnectionHandler<T> {

    private static final int BUFFER_ALLOCATION_SIZE = 1 << 13; //8k
    private static final ConcurrentLinkedQueue<ByteBuffer> BUFFER_POOL = new ConcurrentLinkedQueue<>();

    private final BidiMessagingProtocol<T> protocol;
    private final MessageEncoderDecoder<T> encdec;
    private final Queue<ByteBuffer> writeQueue = new ConcurrentLinkedQueue<>();
    private final SocketChannel chan;
    private final Reactor reactor;
    private final int connectionID;
    private ConnectionsImpl connections;

    public NonBlockingConnectionHandler(
            MessageEncoderDecoder<T> reader,
            BidiMessagingProtocol<T> protocol,
            SocketChannel chan,
            Reactor reactor,
            int connectionID,
            ConnectionsImpl connections) {
        this.chan = chan;
        this.encdec = reader;
        this.protocol = protocol;
        this.reactor = reactor;
        this.connectionID=connectionID;
        this.connections=connections;

    }

    public synchronized void send(T msg) {
        writeQueue.add(ByteBuffer.wrap(encdec.encode(msg)));
        reactor.updateInterestedOps(chan, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
    }

    public Runnable continueRead() {
        ByteBuffer buf = leaseBuffer(); // ~ new byte buffer

        boolean success = false;
        try {
            success = chan.read(buf) != -1; //read as many as you can
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (success) { //if something was read
            buf.flip();
            return () -> { //all that perfomed by the threads in executor
                try {
                    protocol.start(connectionID,connections);
                    while (buf.hasRemaining()) { // while buffer contains bytes
                        T nextMessage = encdec.decodeNextByte(buf.get());
                        if (nextMessage != null) {
                            protocol.process(nextMessage);
                            reactor.updateInterestedOps(chan, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                        }
                    }
                } finally {
                    releaseBuffer(buf);
                }
            };
        } else {
            releaseBuffer(buf);
            close();
            return null;
        }
    }
    public void close() {
        try {
            chan.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public boolean isClosed() {
        return !chan.isOpen();
    }

    public void continueWrite() { //selctor/reactor thread invokes it
        while (!writeQueue.isEmpty()) { //write ALL the answer to the client, we can  design it so only one answer will be send..might be asked on the exam
            try {
                ByteBuffer top = writeQueue.peek();
                chan.write(top); // write to the client, as much as possible
                if (top.hasRemaining()) { //if something was left, that means the SC buffer is socket so we should return
                    return;
                } else {
                    writeQueue.remove(); //all bytes were written so we can remove the answer
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                close();
            }
        }

        if (writeQueue.isEmpty()) {
            if (protocol.shouldTerminate()) close();
            else reactor.updateInterestedOps(chan, SelectionKey.OP_READ);
        }
    }

    private static ByteBuffer leaseBuffer() {
        ByteBuffer buff = BUFFER_POOL.poll();
        if (buff == null) {
            return ByteBuffer.allocateDirect(BUFFER_ALLOCATION_SIZE);
        }

        buff.clear();
        return buff;
    }

    private static void releaseBuffer(ByteBuffer buff) {
        BUFFER_POOL.add(buff);
    }

}
