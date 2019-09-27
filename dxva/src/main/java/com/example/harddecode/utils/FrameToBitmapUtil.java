package com.example.harddecode.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class FrameToBitmapUtil {
    private final boolean VERBOSE = false;
    private static FrameToBitmapUtil frameUtil;

    public static FrameToBitmapUtil getInstance() {
        synchronized (FrameToBitmapUtil.class) {
            if (null == frameUtil) {
                synchronized (FrameToBitmapUtil.class) {
                    frameUtil = new FrameToBitmapUtil();
                }
            }
        }
        return frameUtil;
    }

    private FrameToBitmapUtil() {

    }

    /**
     * 知识点：MediaCodec类中有三个方法与数据读写有关：queueInputBuffer()/dequeueInputBuffer()/dequeueOutputBuffer()
     * MediaCodec中有维护这两个缓冲区，分别存放的是向MediaCodec中写入的数据，和经MediaCodec解码后写出的数据
     * dequeueInputBuffer(): Returns the index of an input buffer to be filled with valid data
     * dequeueOutputBuffer():Returns the index of an output buffer that has been successfully decoded
     * queueInputBuffer(): After filling a range of the input buffer at the specified index submit it to the component
     *
     * @param path
     * @param time
     * @return
     */
    public Bitmap getBitmapByFile(String path, long time) {
        MediaExtractor extractor = null;
        MediaFormat mediaFormat = null;
        MediaCodec decoder = null;
        Bitmap bitmap = null;
        try {
            //使用视频文件对象初始化extractor
            extractor = initMediaExtractor(new File(path));
            mediaFormat = initMediaFormat(path, extractor);
            decoder = initMediaCodec(mediaFormat);
            //第一个参数是待解码的数据格式(也可用于编码操作);
            // 第二个参数是设置surface，用来在其上绘制解码器解码出的数据；
            // 第三个参数于数据加密有关；
            // 第四个参数上1表示编码器，0是否表示解码器呢？？
            decoder.configure(mediaFormat, null, null, 0);
            //当configure好后，就可以调用start()方法来请求向MediaCodec的inputBuffer中写入数据了
            decoder.start();
            bitmap = getBitmapBySec(extractor, mediaFormat, decoder, time);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            decoder.release();
            extractor.release();
        }
        return bitmap;
    }

    private static final long DEFAULT_TIMEOUT_US = 10000;

    /**
     * 接下来要做到就是，向MediaCodec的inputBuffer中写入数据，而数据就是来自上面MediaExtractor中解析出的Track，代码如下：
     * ByteBuffer[] inputBuffers = mVideoCodec.getInputBuffers();
     * 获取MediaCodec中等待数据写入的ByteBuffer的集合,大概有10个ByteBuffer
     * 上面这个方法获取的是整个待写入数据的ByteBuffer的集合，在MediaExtractor向MediaCodec中写入数据的过程中，需要判断哪些ByteBuffer是可用的，这就可以通过dequeueInputBuffer得到。
     *
     * @param extractor
     * @param mediaFormat
     * @param decoder
     * @param sec
     * @return
     * @throws IOException
     */
    private Bitmap getBitmapBySec(MediaExtractor extractor, MediaFormat mediaFormat, MediaCodec decoder, long sec) throws IOException {
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        Bitmap bitmap = null;
        boolean sawInputEOS = false;
        boolean sawOutputEOS = false;
        boolean stopDecode = false;
        //从得到的MediaFormat中，可以获取视频的相关信息，视频的长/宽/时长等
        final int width = mediaFormat.getInteger(MediaFormat.KEY_WIDTH);
        final int height = mediaFormat.getInteger(MediaFormat.KEY_HEIGHT);
        Log.i("getBitmapBySec", "w: " + width);
        long presentationTimeUs = -1;
        int outputBufferId;
        Image image = null;

        //视频定位到指定的时间的上一帧
        extractor.seekTo(sec, MediaExtractor.SEEK_TO_PREVIOUS_SYNC);
        //因为extractor定位的帧不是准确的，所以我们要用一个循环不停读取下一帧来获取我们想要的时间画面。
        while (!sawOutputEOS && !stopDecode) {
            if (!sawInputEOS) {
                int inputBufferId = decoder.dequeueInputBuffer(DEFAULT_TIMEOUT_US);
                //返回的inputBufferIndex为-1，说明暂无可用的ByteBuffer
                if (inputBufferId >= 0) {
                    //有,就从inputBuffers中拿出那个可用的ByteBuffer的对象
                    ByteBuffer inputBuffer = decoder.getInputBuffer(inputBufferId);
                    // 把MediaExtractor中的数据写入到这个可用的ByteBuffer对象中去
                    int sampleSize = extractor.readSampleData(inputBuffer, 0);
                    // 返回值为-1表示MediaExtractor中数据已全部读完
                    if (sampleSize < 0) {
                        decoder.queueInputBuffer(inputBufferId, 0, 0, 0L, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        sawInputEOS = true;
                    } else {
                        //将已写入数据的id为inputBufferIndex的ByteBuffer提交给MediaCodec进行解码
                        presentationTimeUs = extractor.getSampleTime();
                        decoder.queueInputBuffer(inputBufferId, 0, sampleSize, presentationTimeUs, 0);
                        //在MediaExtractor执行完一次readSampleData方法后，需要调用advance()去跳到下一个sample，然后再次读取数据
                        extractor.advance();
                    }
                }
            }
            // 获得已经成功解码的ByteBuffer的id
            outputBufferId = decoder.dequeueOutputBuffer(info, DEFAULT_TIMEOUT_US);
            if (outputBufferId >= 0) {
                //如果数据读取完毕，并且获取时长大于总时长
                if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0 | presentationTimeUs >= sec) {
                    sawOutputEOS = true;
                    //文件大小不为0
                    boolean doRender = (info.size != 0);
                    if (doRender) {
                        //获取图片数据
                        image = decoder.getOutputImage(outputBufferId);
                        //Image对Bimap的转换，主要是用到YuvImage这个类
                        // 在使用YuvImage这个类前需要把YUV_420_888的编码格式转成NV21格式
                        YuvImage yuvImage = new YuvImage(YUV_420_888toNV21(image), ImageFormat.NV21, width, height, null);
                        //转换成bitmap
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        yuvImage.compressToJpeg(new Rect(0, 0, width, height), 100, stream);
                        bitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                        stream.close();
                        image.close();
                    }
                }
                //释放ByteBuffer
                decoder.releaseOutputBuffer(outputBufferId, true);
            }
        }

        return bitmap;
    }

    private MediaExtractor initMediaExtractor(File path) throws IOException {
        MediaExtractor extractor = null;
        extractor = new MediaExtractor();
        //设置需播放的视频文件路径
        extractor.setDataSource(path.toString());
        return extractor;
    }

    private MediaFormat initMediaFormat(String path, MediaExtractor extractor) {
        int trackIndex = selectTrack(extractor);
        if (trackIndex < 0) {
            throw new RuntimeException("No video track found in " + path);
        }
        //选择视轨所在的轨道子集(这样在之后调用readSampleData()/getSampleTrackIndex()方法时候，返回的就只是视轨的数据了，其他轨的数据不会被返回)
        extractor.selectTrack(trackIndex);
        //根据视轨id获得对应的MediaForamt
        MediaFormat mediaFormat = extractor.getTrackFormat(trackIndex);
        return mediaFormat;
    }

    /**
     * 选择解码通道,从videoExtractor中找到视轨的id，方法如下：
     *
     * @param extractor
     * @return
     */
    private int selectTrack(MediaExtractor extractor) {
        //在videoExtractor的所以Track中遍历，找到视轨的id
        int numTracks = extractor.getTrackCount();
        for (int i = 0; i < numTracks; i++) {
            //获得第id个Track对应的MediaForamt
            MediaFormat format = extractor.getTrackFormat(i);
            //再获取该Track对应的KEY_MIME字段R
            String mime = format.getString(MediaFormat.KEY_MIME);
            //视轨的KEY_MIME是以"video/"开头的，音轨是"audio/"
            if (mime.startsWith("video/")) {
                if (VERBOSE) {
                    Log.d("selectTrack", "Extractor selected track " + i + " (" + mime + "): " + format);
                }
                return i;
            }
        }
        return -1;
    }

    private final int decodeColorFormat = MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible;

    private MediaCodec initMediaCodec(MediaFormat mediaFormat) throws IOException {
        MediaCodec decoder = null;
        //在通过MediaExtractor获得需要解码的音轨的id后，就可以创建对应的MediaCodec来解析数据了
        String mime = mediaFormat.getString(MediaFormat.KEY_MIME);
        decoder = MediaCodec.createDecoderByType(mime);
        if (isColorFormatSupported(decodeColorFormat, decoder.getCodecInfo().getCapabilitiesForType(mime))) {
            //  设置 解码格式
            mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, decodeColorFormat);
        } else {
        }
        return decoder;
    }

    private boolean isColorFormatSupported(int colorFormat, MediaCodecInfo.
            CodecCapabilities caps) {
        for (int c : caps.colorFormats) {
            if (c == colorFormat) {
                return true;
            }
        }
        return false;
    }

    private byte[] YUV_420_888toNV21(Image image) {
        Rect crop = image.getCropRect();
        int format = image.getFormat();
        int width = crop.width();
        int height = crop.height();
        Image.Plane[] planes = image.getPlanes();
        byte[] data = new byte[width * height * ImageFormat.getBitsPerPixel(format) / 8];
        byte[] rowData = new byte[planes[0].getRowStride()];
        if (VERBOSE) Log.v("YUV_420_888toNV21", "get data from " + planes.length + " planes");
        int channelOffset = 0;
        int outputStride = 1;
        for (int i = 0; i < planes.length; i++) {
            switch (i) {
                case 0:
                    channelOffset = 0;
                    outputStride = 1;
                    break;
                case 1:
                    channelOffset = width * height + 1;
                    outputStride = 2;

                    break;
                case 2:
                    channelOffset = width * height;
                    outputStride = 2;

                    break;
            }
            ByteBuffer buffer = planes[i].getBuffer();
            int rowStride = planes[i].getRowStride();
            int pixelStride = planes[i].getPixelStride();
            if (VERBOSE) {
                Log.v("YUV_420_888toNV21", "pixelStride " + pixelStride);
                Log.v("YUV_420_888toNV21", "rowStride " + rowStride);
                Log.v("YUV_420_888toNV21", "width " + width);
                Log.v("YUV_420_888toNV21", "height " + height);
                Log.v("YUV_420_888toNV21", "buffer size " + buffer.remaining());
            }
            int shift = (i == 0) ? 0 : 1;
            int w = width >> shift;
            int h = height >> shift;
            buffer.position(rowStride * (crop.top >> shift) + pixelStride * (crop.left >> shift));
            for (int row = 0; row < h; row++) {
                int length;
                if (pixelStride == 1 && outputStride == 1) {
                    length = w;
                    buffer.get(data, channelOffset, length);
                    channelOffset += length;
                } else {
                    length = (w - 1) * pixelStride + 1;
                    buffer.get(rowData, 0, length);
                    for (int col = 0; col < w; col++) {
                        data[channelOffset] = rowData[col * pixelStride];
                        channelOffset += outputStride;
                    }
                }
                if (row < h - 1) {
                    buffer.position(buffer.position() + rowStride - length);
                }
            }
            if (VERBOSE) Log.v("", "Finished reading data from plane " + i);
        }
        return data;
    }
}
