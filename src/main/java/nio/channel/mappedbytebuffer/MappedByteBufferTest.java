package nio.channel.mappedbytebuffer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * MappedByteBuffer  可以让文件直接在内存中修改(堆外内存)，操作系统不需要拷贝一次
 **/
public class MappedByteBufferTest {

    public static void main(String[] args) throws IOException {
        //创建一个文件操作IO对象
        RandomAccessFile accessFile = new RandomAccessFile("file\\202041201.txt", "rw");
        //获取channel对象
        FileChannel channel = accessFile.getChannel();
        MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, 5);
        map.put(0, (byte) 'A');
        map.put(2, (byte) 'B');
        channel.close();
    }
}
