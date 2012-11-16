#include <jni.h>
#include <android/log.h>

extern "C" {
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libavutil/opt.h>
#include <libavutil/imgutils.h>
#include <libswscale/swscale.h>
}

#include <liveMedia.hh>
#include <BasicUsageEnvironment.hh>
#include <GroupsockHelper.hh>

#define LOG_TAG "streamer"
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C" {
JNIEXPORT void JNICALL Java_com_parizene_streamer_Streamer_init(JNIEnv *env,
		jobject obj, jstring filename, int width, jint height, jint frameRate);
JNIEXPORT void JNICALL Java_com_parizene_streamer_Streamer_encode(JNIEnv *env,
		jobject obj, jbyteArray data);
JNIEXPORT void JNICALL Java_com_parizene_streamer_Streamer_deinit(JNIEnv *env,
		jobject obj);
JNIEXPORT void JNICALL Java_com_parizene_streamer_Streamer_loop(JNIEnv *env,
		jobject obj, jstring addr);

void play();
void afterPlaying(void* /*clientData*/);
}

AVCodecContext *codecContext;
FILE *file;
AVFrame *frame;
AVFrame *tmpFrame;
AVPacket packet;
int count, got_output;
SwsContext *swsContext;

UsageEnvironment* uEnv;
H264VideoStreamFramer* videoSource;
RTPSink* videoSink;

char const *inputFilename;

JNIEXPORT void JNICALL Java_com_parizene_streamer_Streamer_init(JNIEnv *env,
		jobject obj, jstring filename, jint width, jint height,
		jint frameRate) {
	LOGD("init()");

	av_register_all();

	AVCodec *codec = avcodec_find_encoder(AV_CODEC_ID_H264);
	if (!codec) {
		LOGE("codec not found");
		exit(1);
	}

	codecContext = avcodec_alloc_context3(codec);
	if (!codec) {
		LOGE("couldn't allocate codec context");
		exit(1);
	}

	/* put sample parameters */
	codecContext->bit_rate = 400000;
	/* resolution must be a multiple of two */
	codecContext->width = width;
	codecContext->height = height;
	/* frames per second */
	codecContext->time_base = (AVRational ) {1, frameRate};
	codecContext->gop_size = frameRate; /* emit one intra frame every ten frames */
	codecContext->max_b_frames = 1;
	codecContext->pix_fmt = AV_PIX_FMT_YUV420P;

	av_opt_set(codecContext->priv_data, "profile", "baseline", 0);
	av_opt_set(codecContext->priv_data, "preset", "ultrafast", 0);

	if (avcodec_open2(codecContext, codec, NULL) < 0) {
		LOGE("couldn't open codec");
		exit(1);
	}

	inputFilename = env->GetStringUTFChars(filename, NULL);
	file = fopen(inputFilename, "wb");
	if (!file) {
		LOGE("couldn't open %s", inputFilename);
		exit(1);
	}

	frame = avcodec_alloc_frame();
	if (!frame) {
		LOGE("couldn't allocate frame");
		exit(1);
	}

	frame->format = codecContext->pix_fmt;
	frame->width = codecContext->width;
	frame->height = codecContext->height;

	if (av_image_alloc(frame->data, frame->linesize, codecContext->width,
			codecContext->height, codecContext->pix_fmt, 32) < 0) {
		LOGE("couldn't allocate raw picture buffer");
		exit(1);
	}

	tmpFrame = avcodec_alloc_frame();
	if (!tmpFrame) {
		LOGE("couldn't allocate frame");
		exit(1);
	}

	if (av_image_alloc(tmpFrame->data, tmpFrame->linesize, codecContext->width,
			codecContext->height, AV_PIX_FMT_NV21, 32) < 0) {
		LOGE("couldn't allocate raw picture buffer");
		exit(1);
	}

	count = 0;
}

JNIEXPORT void JNICALL Java_com_parizene_streamer_Streamer_encode(JNIEnv *env,
		jobject obj, jbyteArray data) {
	av_init_packet(&packet);
	packet.data = NULL;
	packet.size = 0;

	swsContext = sws_getCachedContext(swsContext, codecContext->width,
			codecContext->height, AV_PIX_FMT_NV21, codecContext->width,
			codecContext->height, codecContext->pix_fmt, SWS_BILINEAR, NULL,
			NULL, NULL);

	jbyte *_data = env->GetByteArrayElements(data, NULL);

	avpicture_fill((AVPicture*) tmpFrame, (const unsigned char*) _data,
			AV_PIX_FMT_NV21, codecContext->width, codecContext->height);

	env->ReleaseByteArrayElements(data, _data, 0);

	sws_scale(swsContext, tmpFrame->data, tmpFrame->linesize, 0,
			codecContext->height, frame->data, frame->linesize);

	frame->pts = count;

	/* encode the image */
	if (avcodec_encode_video2(codecContext, &packet, frame, &got_output)) {
		LOGE("couldn't encode frame");
		exit(1);
	}

	if (got_output) {
//		LOGI("write frame %3d (size=%5d)", count, packet.size);
		fwrite(packet.data, 1, packet.size, file);
		av_free_packet(&packet);
	}

	count++;
}

JNIEXPORT void JNICALL Java_com_parizene_streamer_Streamer_deinit(JNIEnv *env,
		jobject obj) {
	LOGD("deinit()");

	/* get the delayed frames */
	for (got_output = 1; got_output; count++) {
		if (avcodec_encode_video2(codecContext, &packet, NULL, &got_output)
				< 0) {
			LOGE("couldn't encode frame");
			exit(1);
		}

		if (got_output) {
//			LOGI("write frame %3d (size=%5d)", count, packet.size);
			fwrite(packet.data, 1, packet.size, file);
			av_free_packet(&packet);
		}
	}

	uint8_t endcode[] = { 0, 0, 1, 0xb7 };

	/* add sequence end code to have a real mpeg file */
	fwrite(endcode, 1, sizeof(endcode), file);
	fclose(file);

	avcodec_close(codecContext);
	av_free(codecContext);
	av_freep(&frame->data[0]);
	avcodec_free_frame(&frame);
	avcodec_free_frame(&tmpFrame);
}

JNIEXPORT void JNICALL Java_com_parizene_streamer_Streamer_loop(JNIEnv *env,
		jobject obj, jstring addr) {
	// Begin by setting up our usage environment:
	TaskScheduler* scheduler = BasicTaskScheduler::createNew();
	uEnv = BasicUsageEnvironment::createNew(*scheduler);

	// Create 'groupsocks' for RTP and RTCP:
	struct in_addr destinationAddress;
	const char *_addr = env->GetStringUTFChars(addr, NULL);
	destinationAddress.s_addr = our_inet_addr(_addr); /*chooseRandomIPv4SSMAddress(*uEnv);*/
	env->ReleaseStringUTFChars(addr, _addr);
	// Note: This is a multicast address.  If you wish instead to stream
	// using unicast, then you should use the "testOnDemandRTSPServer"
	// test program - not this test program - as a model.

	const unsigned short rtpPortNum = 18888;
	const unsigned short rtcpPortNum = rtpPortNum + 1;
	const unsigned char ttl = 255;

	const Port rtpPort(rtpPortNum);
	const Port rtcpPort(rtcpPortNum);

	Groupsock rtpGroupsock(*uEnv, destinationAddress, rtpPort, ttl);
	Groupsock rtcpGroupsock(*uEnv, destinationAddress, rtcpPort, ttl);

	// Create a 'H264 Video RTP' sink from the RTP 'groupsock':
	OutPacketBuffer::maxSize = 100000;
	videoSink = H264VideoRTPSink::createNew(*uEnv, &rtpGroupsock, 96);

	// Create (and start) a 'RTCP instance' for this RTP sink:
	const unsigned estimatedSessionBandwidth = 500; // in kbps; for RTCP b/w share
	const unsigned maxCNAMElen = 100;
	unsigned char CNAME[maxCNAMElen + 1];
	gethostname((char*) CNAME, maxCNAMElen);
	CNAME[maxCNAMElen] = '\0'; // just in case
	RTCPInstance* rtcp = RTCPInstance::createNew(*uEnv, &rtcpGroupsock,
			estimatedSessionBandwidth, CNAME, videoSink,
			NULL /* we're a server */, True /* we're a SSM source */);
	// Note: This starts RTCP running automatically

	RTSPServer* rtspServer = RTSPServer::createNew(*uEnv, 8554);
	if (rtspServer == NULL) {
		LOGE("Failed to create RTSP server: %s", uEnv->getResultMsg());
		exit(1);
	}
	ServerMediaSession* sms = ServerMediaSession::createNew(*uEnv, "streamer",
			inputFilename, "Session streamed by \"testH264VideoStreamer\"",
			True /*SSM*/);
	sms->addSubsession(
			PassiveServerMediaSubsession::createNew(*videoSink, rtcp));
	rtspServer->addServerMediaSession(sms);

	char* url = rtspServer->rtspURL(sms);
	LOGI("Play this stream using the URL \"%s\"", url);
	delete[] url;

	// Start the streaming:
	LOGI("Beginning streaming...\n");
	play();

	uEnv->taskScheduler().doEventLoop(); // does not return
}

void play() {
	// Open the input file as a 'byte-stream file source':
	ByteStreamFileSource* fileSource = ByteStreamFileSource::createNew(*uEnv,
			inputFilename);
	if (fileSource == NULL) {
		LOGE(
				"Unable to open file \"%s\" as a byte-stream file source", inputFilename);
		exit(1);
	}

	FramedSource* videoES = fileSource;

	// Create a framer for the Video Elementary Stream:
	videoSource = H264VideoStreamFramer::createNew(*uEnv, videoES);

	// Finally, start playing:
	LOGI("Beginning to read from file...\n");
	videoSink->startPlaying(*videoSource, afterPlaying, videoSink);
}

void afterPlaying(void* /*clientData*/) {
	LOGI("...done reading from file");
	videoSink->stopPlaying();
	Medium::close(videoSource);
	// Note that this also closes the input file that this source read from.

	// Start playing once again:
	play();
}
